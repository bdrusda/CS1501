public class LLObject
{
    String path;
    double length;
    int bandwidth;

    public LLObject(String path, double length, int bandwidth)
    {
        this.path = path;
        this.length = length;
        this.bandwidth = bandwidth;
    }

    public String getPath()
    {
        return path;
    }

    public double getLength()
    {
        return length;
    }

    public int getBandwidth()
    {
        return bandwidth;
    }

    public String toString()
    {
        return ("Path: "+path+"\nLength: "+length+"\nBandwidth: "+bandwidth);
    }
}
