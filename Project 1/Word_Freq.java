public class Word_Freq implements Comparable<Word_Freq>
{
    String word;
    int frequency;

    public Word_Freq(String w)
    {
        word = w;
        frequency = 0;
    }

    public Word_Freq(String w, int f)
    {
        word = w;
        frequency = f;
    }

    public void increment()
    {
        frequency++;
    }

    public String getWord()
    {
        return word;
    }

    public int compareTo(Word_Freq comp)
    {
        return comp.frequency - this.frequency;
    }

    public boolean equals(Object comp)
    {
        if((this.getWord()).compareTo(((Word_Freq)comp).getWord()) == 0)
            return true;
        return false;
    }
}
