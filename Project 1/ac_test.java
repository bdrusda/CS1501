import java.util.*;
import java.io.*;
import java.math.BigDecimal;

public class ac_test
{
    public static void main(String[] args)
    {
        /*Initialize the trie*/
        DLB dictionary = new DLB();       /*Create the DLB*/
        DLB user = new DLB();
        BufferedReader import_dict, import_user;
        String nextWord;
        try
        {
            //Import the dictionary entries
            import_dict = new BufferedReader(new FileReader("dictionary.txt"));
            while((nextWord = import_dict.readLine()) != null)
                dictionary.add_word(nextWord);
            import_dict.close();

            //Import the user entries
            if(!(new File("user_history.txt").exists()))
            {
                System.out.println("Creating user history");
                new File("user_history.txt").createNewFile();
            }
            import_user = new BufferedReader(new FileReader("user_history.txt"));

            ArrayList<Word_Freq> history = new ArrayList<Word_Freq>();
            while((nextWord = import_user.readLine()) != null)
            {
                Word_Freq temp = new Word_Freq(nextWord);   //create a word_freq object
                if(history.contains(temp))                  //if the word is already in the list
                {
                        int i = 0;
                        while(temp.getWord().compareTo(history.get(i).getWord()) != 0)
                        {
                            i++;                                 //find it
                        }
                        (history.get(i)).increment();           //and increment it's frequency
                }
                else                                        //if the word is not already in the list
                {
                        history.add(temp);                       //add it
                }
            }
            //Sort the arraylist
            Collections.sort(history);
            //Add words to the trie accordingly
            for(int i = 0; i < history.size(); i++)
            {
                user.add_word(history.get(i).getWord());
            }

            import_user.close();
        }
        catch(IOException e)
        {
            System.out.println("An IO Exception Ocurred");
        }

        /*Prompt the user for input*/
        Scanner in = new Scanner(System.in);

        double total_prediction_time = 0;
        double num_predictions = 0;

        while(true)
        {
            System.out.println("Start typing a word");

            String[] suggestions = new String[5];       /*Predictions*/
            StringBuilder partial = new StringBuilder();  /*Aggregate of user input*/

            while(true)
            {
                System.out.print("Enter a character: ");
                char userIn = (in.nextLine()).charAt(0);    /*Takes first char entered in string*/

                if(userIn >= 49 && userIn <= 53)            /*If it's 1-5, they're taking the suggestion, start over*/
                {
                    if(userIn == 49)
                        addToHistory(suggestions[0], user);
                    else if(userIn == 50)
                        addToHistory(suggestions[1], user);
                    else if(userIn == 51)
                        addToHistory(suggestions[2], user);
                    else if(userIn == 52)
                        addToHistory(suggestions[3], user);
                    else
                        addToHistory(suggestions[4], user);

                    System.out.println("\n\nWORD COMPLETED:\t"+suggestions[userIn-49]+"\n");
                    break;  //push out of this loop, resetting suggestions and partial
                }
                else if(userIn == 36)                       /*If it's $, that's the end of the word, start over*/
                {
                    addToHistory(partial.toString(), user);
                    break;  //word has been added, reset suggestions and partial
                }
                else if(userIn == 33)                       /*If it's !, terminate the program*/
                {
                    /*Output average time that was required to produce a list of predictions*/

                    if(num_predictions > 0)
                        System.out.println("\nAverage time: "+(BigDecimal.valueOf(total_prediction_time/num_predictions).toPlainString())+" s");
                    System.out.println("Exiting program");
                    System.exit(0);
                }
                else                                        /*If it's anything else, that's a character that we want to add to the existing search*/
                {
                    partial.append(userIn);
                    long start = System.nanoTime();
                    suggestions = predictions(partial.toString(), dictionary, user);
                    long end = System.nanoTime();
                    double result_seconds = ((end)-(start))/1000000000.0;

                    total_prediction_time += result_seconds;
                    num_predictions++;

                    System.out.println("\n("+BigDecimal.valueOf(result_seconds).toPlainString()+" s)");
                    //print out all available predictions
                    if(suggestions[0] != null)
                    {
                        System.out.print("Predictions:\n");
                        System.out.print("(1) "+suggestions[0]+"\t");
                        if(suggestions[1] != null)
                        {
                            System.out.print("(2) "+suggestions[1]+"\t");
                            if(suggestions[2] != null)
                            {
                                System.out.print("(3) "+suggestions[2]+"\t");
                                if(suggestions[3] != null)
                                {
                                    System.out.print("(4) "+suggestions[3]+"\t");
                                    if(suggestions[4] != null)
                                    {
                                        System.out.print("(5) "+suggestions[4]);
                                    }
                                }
                            }
                        }
                        System.out.println("\n");   //double space
                    }
                    else    //if there are no suggestions, notify the userIn
                    {
                        System.out.println("No predictions found.\nEnter the remainder of the word one character at a time.\nWhen the word is complete, enter '$'");
                        do
                        {
                            userIn = (in.nextLine()).charAt(0); //get the next character of the word
                            if(userIn != 36)                    //if it isn't $
                                partial.append(userIn);             //add the character to the word
                        }while(userIn != 36);

                        addToHistory(partial.toString(), user);
                        break;  //word has been added, reset suggestions and partial
                    }
                }
            }
        }
    }

    private static String[] predictions(String partial, DLB dictionary, DLB user)
    {
        String[] personal = user.get_predictions(partial.toString());
        String[] generic = dictionary.get_predictions(partial.toString());
        String[] return_arr = new String[5];

        if(personal == null)    //if there is no data returned from personal
        {
            if(generic == null)     //if no data was returned from generic
            {
                return return_arr;      //return the empty array
            }
            else                    //fill the return array with generic
            {
                int i = 0;
                while(generic[i] != null)
                {
                    return_arr[i] = generic[i++];
                    if(i == 5)
                        break;
                }
                return return_arr;
            }
        }
        else                    //fill the return array with user predictions and then fill the remainder with user predictions
        {
            int i = 0;
            while(personal[i] != null)                         //fill with user history predictions
            {
                return_arr[i] = personal[i++];
                if(i == 5)
                    break;
            }
            if(generic == null)
            {
                return return_arr;
            }
            else
            {
                int j = 0;
                while(generic[j] != null)                 //fill remainder with dictionary predictions
                {
                    while(Arrays.asList(return_arr).contains(generic[j]))       //if generic(j) is a duplicate
                    {
                        j++;                                                        //go to the j+1
                        if(j == 5)                                                  //if we are out of words to check
                            return return_arr;                                          //return the return_arr, we have everything that we're going to get
                    }
                    if(j == 5 || i == 5)    //there's a more elegant
                        break;
                    return_arr[i++] = generic[j++];                             //if we get here, it means that generic[j] is not a duplicate, add it and try to add again
                    if(j == 5 || i == 5)    //way to do this i'm sure
                        break;
                }
            }
            return return_arr;
        }
    }

    private static void addToHistory(String word, DLB user)
    {
        user.add_word(word);                 /*Add user's word to the current trie*/

        try
        {
            BufferedWriter append_user = new BufferedWriter(new FileWriter("user_history.txt", true));  /*Save the user's word in a text file*/
            append_user.write(word);
            append_user.newLine();
            append_user.close();
        }
        catch(IOException e)
        {
            System.out.println("An IO Exception Ocurred");
        }
    }
}

/*Getting predictions - prioritize past user usage over dictionary suggestions*/
/*Getting predictions - if user guess + dictionary < 5 only show the existing ones*/
/*Getting predictions - if there are no predictions, let the user know*/
    /*allow user to continue entering one key at a time, add the word to user list after $ is hit*/

/*Should be case sensitive*/

/*For each chracter entered, use System.nanoTime() to calculate how long it took to find the predictions*/
    /*Display this time along with the list of predictions*/
