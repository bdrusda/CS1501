public class Word_Freq implements Comparable<Word_Freq>
{
    String word;
    int frequency;

    public Word_Freq(String w)          //Constructor with just a word
    {
        word = w;
        frequency = 0;
    }

    public Word_Freq(String w, int f)   //Constructor with a word and a frequency
    {
        word = w;
        frequency = f;
    }

    public void increment()             //Increment the frequency
    {
        frequency++;
    }

    public String getWord()             //Return the word
    {
        return word;
    }

    public int compareTo(Word_Freq comp)//Compare words by frequency
    {
        return comp.frequency - this.frequency;
    }

    public boolean equals(Object comp)  //See if two words are equal
    {
        if((this.getWord()).compareTo(((Word_Freq)comp).getWord()) == 0)
            return true;
        return false;
    }
}
