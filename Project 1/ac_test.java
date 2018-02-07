import java.util.*;
import java.io.*;
import java.math.BigDecimal;

public class ac_test
{
    public static void main(String[] args)
    {
        /*Initialize the trie*/
        DLB dictionary = new DLB();                 //Create the dictionary DLB
        DLB user = new DLB();                       //Create the user history DLB
        BufferedReader import_dict, import_user;    //Create a BufferedReader for each file
        String nextWord;

        try
        {
            //Import the dictionary entries
            import_dict = new BufferedReader(new FileReader("dictionary.txt"));     //Open the dictionary file
            while((nextWord = import_dict.readLine()) != null)                      //While there is another word in the dictionary
                dictionary.add_word(nextWord);                                          //Add the word to the dictioanry DLB
            import_dict.close();                                                    //Close the BR

            //Import the user entries
            if(!(new File("user_history.txt").exists()))                            //If there is not previous a userhistory
            {
                System.out.println("Creating user history");                            //Create a user history file
                new File("user_history.txt").createNewFile();
            }
            import_user = new BufferedReader(new FileReader("user_history.txt"));   //Open the history file

            ArrayList<Word_Freq> history = new ArrayList<Word_Freq>();              //Create an ArrayList to hold the user history
            while((nextWord = import_user.readLine()) != null)                      //While there is another word in the history
            {
                Word_Freq temp = new Word_Freq(nextWord);                               //Create a word_freq object with it
                if(history.contains(temp))                                              //If the word is already in the list
                {
                        int i = 0;
                        while(temp.getWord().compareTo(history.get(i).getWord()) != 0)      //Find the word
                            i++;
                        history.get(i).increment();                                         //Increment the word's frequency
                }
                else                                                                    //If the word is not already in the list
                {
                        history.add(temp);                                                  //Add it to the list
                }
            }

            Collections.sort(history);                                              //Sort the ArrayList by Frequency
            for(int i = 0; i < history.size(); i++)                                 //Traverse through the ArrayList
                user.add_word(history.get(i).getWord());                                //Add the words to the trie accordingly

            import_user.close();                                                    //Close the BR
        }
        catch(IOException e)
        {
            System.out.println("An IO Exception Ocurred");
        }

        /*Prompt the user for input*/
        Scanner in = new Scanner(System.in);                                        //Create a scanner for user input

        double total_prediction_time = 0;                                           //Total time predictions took
        double num_predictions = 0;                                                 //Number of predictions made

        while(true)                                                                 //Until the user terminates the program
        {
            System.out.println("Start typing a word");                                  //Prompt the user for a word

            String[] suggestions = new String[5];                                       //Predictions array
            StringBuilder partial = new StringBuilder();                                //Aggregate of user input

            while(true)                                                                 //Until the user finishes the word
            {
                System.out.print("Enter a character: ");                                    //Prompt for a character
                char userIn = (in.nextLine()).charAt(0);                                    //Take the first char entered

                if(userIn >= 49 && userIn <= 53)                                            //If input is 1-5, user wants to accept a suggestion
                {
                    //Add the corresponding suggestion to user history if it exists
                    if(userIn == 49)
                    {
                        if(suggestions[0] == null)
                        {
                            System.out.println("Prediction "+(userIn-48)+" does not exist, try to retype the word");
                            break;
                        }
                        addToHistory(suggestions[0], user);
                    }
                    else if(userIn == 50)
                    {
                        if(suggestions[1] == null)
                        {
                            System.out.println("Prediction "+(userIn-48)+" does not exist, try to retype the word");
                            break;
                        }
                        addToHistory(suggestions[1], user);
                    }
                    else if(userIn == 51)
                    {
                        if(suggestions[2] == null)
                        {
                            System.out.println("Prediction "+(userIn-48)+" does not exist, try to retype the word");
                            break;
                        }
                        addToHistory(suggestions[2], user);
                    }
                    else if(userIn == 52)
                    {
                        if(suggestions[3] == null)
                        {
                            System.out.println("Prediction "+(userIn-48)+" does not exist, try to retype the word");
                            break;
                        }
                        addToHistory(suggestions[3], user);
                    }
                    else
                    {
                        if(suggestions[4] == null)
                        {
                            System.out.println("Prediction "+(userIn-48)+" does not exist, try to retype the word");
                            break;
                        }
                        addToHistory(suggestions[4], user);
                    }

                    System.out.println("\n\nWORD COMPLETED:\t"+suggestions[userIn-49]+"\n");    //Notify the user that the word is complete and display it
                    break;                                                                      //Push  out of this loop, resetting suggestions and partial
                }
                else if(userIn == 36)                                                       //If input is $, that's the end of the word, start over
                {
                    addToHistory(partial.toString(), user);                                     //Add the word to user history
                    break;                                                                      //The word has been added, reset suggestions and partial
                }
                else if(userIn == 33)                                                       //If input is !, terminate the program
                {
                    /*Output average time that was required to produce a list of predictions*/
                    if(num_predictions > 0)                                                     //If a prediction has been made
                        System.out.println("\nAverage time: "+(BigDecimal.valueOf(total_prediction_time/num_predictions).toPlainString())+" s");    //Output the average prediction time
                    System.out.println("Exiting program");                                      //Notify the user that the program is exiting
                    System.exit(0);                                                             //Exit program
                }
                else                                                                        //If input is anything else, that's a character that we want to add to the existing search
                {
                    partial.append(userIn);                                                     //Append the character to the partial word
                    long start = System.nanoTime();                                             //Track the start time
                    suggestions = predictions(partial.toString(), dictionary, user);            //Get predictions from user and dictionary
                    long end = System.nanoTime();                                               //Track the end time
                    double result_seconds = ((end)-(start))/1000000000.0;                       //Calculate the time the prediction took in seconds

                    total_prediction_time += result_seconds;                                    //Add prediction time to total
                    num_predictions++;                                                          //Increment number of predictions

                    System.out.println("\n("+BigDecimal.valueOf(result_seconds).toPlainString()+" s)"); //Print the time that this prediction took
                    /*Print out all available predictions*/
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
                    else                                                                        //If there are no predictions
                    {
                        System.out.println("No predictions found.\nEnter the remainder of the word one character at a time.\nWhen the word is complete, enter '$'");    //Notify the user that there are no predctions and get the remainder of the word
                        do                                                                          //While we do not have a $
                        {
                            userIn = (in.nextLine()).charAt(0);                                         //Get the next character of the word
                            if(userIn == 33)
                            {
                                /*Output average time that was required to produce a list of predictions*/
                                if(num_predictions > 0)                                                     //If a prediction has been made
                                    System.out.println("\nAverage time: "+(BigDecimal.valueOf(total_prediction_time/num_predictions).toPlainString())+" s");    //Output the average prediction time
                                System.out.println("Exiting program");                                      //Notify the user that the program is exiting
                                System.exit(0); 
                            }
                            if(userIn != 36)                                                            //If it isn't $
                                partial.append(userIn);                                                     //Add the character to the word
                        }while(userIn != 36);

                        addToHistory(partial.toString(), user);                                     //Add the word to the user's history
                        break;                                                                      //Word has been added, reset suggestions and partial
                    }
                }
            }
        }
    }

    private static String[] predictions(String partial, DLB dictionary, DLB user)
    {
        String[] personal = user.get_predictions(partial.toString());               //User predictions array
        String[] generic = dictionary.get_predictions(partial.toString());          //Dictionary predictions array
        String[] return_arr = new String[5];                                        //Return predcitions array

        if(personal == null)                                                        //If no data was returned from personal
        {
            if(generic == null)                                                         //If no data was returned from generic
            {
                return return_arr;                                                          //Return the empty array
            }
            else                                                                        //Fill the return array with dictionary predictions
            {
                int i = 0;
                while(generic[i] != null)                                                   //While there are more predictions from dictionary
                {
                    return_arr[i] = generic[i++];                                               //Put them in the array
                    if(i == 5)                                                                  //If we fill the array
                        break;                                                                      //Exit loop
                }
                return return_arr;                                                              //Return the array
            }
        }
        else                                                                        //Fill the return array with user predictions and then fill the remainder with user predictions
        {
            int i = 0;
            while(personal[i] != null)                                                  //Fill with user history predictions
            {
                return_arr[i] = personal[i++];
                if(i == 5)
                    break;
            }
            if(generic == null)                                                         //If there are no dictionary predictions
            {
                return return_arr;                                                          //Return the array as is
            }
            else                                                                        //If there are dictionary predictions
            {
                int j = 0;
                while(generic[j] != null)                                                   //Fill remainder with dictionary predictions
                {
                    while(Arrays.asList(return_arr).contains(generic[j]))                       //If a dictionary prediction is a duplicate
                    {
                        j++;                                                                        //Go to the next word
                        if(j == 5)                                                                  //If we are out of words to check
                            return return_arr;                                                          //Return the return_arr, we have everything that we're going to get
                    }
                    if(j == 5 || i == 5)                                                        //If we are the bounds of the array
                        break;                                                                      //Exit the loop
                    return_arr[i++] = generic[j++];                                             //If we get here, it means that we have a word to add, add it and try to add another
                    if(j == 5 || i == 5)                                                        //I'm sure there was a more elegant way than putting this twice in a row
                        break;                                                                      //But I do not have the time to implement and test it currently
                }
            }
            return return_arr;                                                          //Return the array of predictions
        }
    }

    private static void addToHistory(String word, DLB user)
    {
        user.add_word(word);                 /*Add user's word to the current trie*/

        try
        {
            BufferedWriter append_user = new BufferedWriter(new FileWriter("user_history.txt", true));  //Open the user history for appending
            append_user.write(word);                                                                    //Save the word in the text file
            append_user.newLine();                                                                      //Write a new line into the file
            append_user.close();                                                                        //Close the BW
        }
        catch(IOException e)
        {
            System.out.println("An IO Exception Ocurred");
        }
    }
}
