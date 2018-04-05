public class LLObject
{
    String path;
    double latency;
    int bandwidth;

    public LLObject(String path, double latency, int bandwidth)
    {
        this.path = path;
        this.latency = latency;
        this.bandwidth = bandwidth;
    }

    public String getPath()
    {
        return path;
    }

    public double getLatency()
    {
        return latency;
    }

    public int getBandwidth()
    {
        return bandwidth;
    }

    public String toString()    //Only used in debugging
    {
        return ("Path: "+path+"\nLatency: "+latency+"\nBandwidth: "+bandwidth);
    }
}
