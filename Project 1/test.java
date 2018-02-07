import java.util.*;
import java.io.*;

public class test
{
    public static void main(String[] args)
    {
        /*Initialize the trie*/
        DLB dictionary = new DLB();       /*Create the DLB*/
        BufferedReader import_dict;
        String nextWord;
        try
        {
            //import_dict = new BufferedReader(new FileReader("dictionary.txt"));
            import_dict = new BufferedReader(new FileReader("dictionary_sample.txt"));
            while((nextWord = import_dict.readLine()) != null)
                dictionary.add_word(nextWord);
            import_dict.close();
        }
        catch(IOException e)
        {
            System.out.println("An IO Exception Ocurred");
        }

        System.out.println("True - "+dictionary.check_word("A"));
        System.out.println("True - "+dictionary.check_word("Flatt"));
        System.out.println("True - "+dictionary.check_word("AC's"));
        System.out.println("False - "+dictionary.check_word("Dillon"));
        System.out.println("True - "+dictionary.check_word("Dillpickle"));
        System.out.println("False - "+dictionary.check_word("Dillpickl"));
        System.out.println("False - "+dictionary.check_word("Dillpickles"));
        System.out.println("True - "+dictionary.check_word("A's"));
    }
}
