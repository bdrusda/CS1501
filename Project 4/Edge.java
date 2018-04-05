public class Edge
{
//All edges are bidirectional

/*Object variables*/
    private Vertex a;           //endpoint 1
    private Vertex b;           //endpoint 2
    private String material;    //type of cable
    private int bandwidth;      //bandwidth (MB/s)
    private int length;         //length of the edge (meters)
    private double travelTime;  //amount of time it takes to send one packet through the cable
/*Class variables*/
    static final int copperSpeed = 230000000;
    static final int fiberSpeed = 200000000;
/*Constructors*/
    public Edge()
    {
        a = null;
        b = null;
        material = null;
        bandwidth = -1;
        length = -1;
    }

    public Edge(Vertex a, Vertex b, String material, int bandwidth, int length)
    {
        this.a = a;
        this.b = b;
        this.material = material;
        this.bandwidth = bandwidth;
        this.length = length;

        updateTT();
    }
/*Getters*/
    public Vertex getA()
    {
        return a;
    }

    public Vertex getB()
    {
        return b;
    }

    public String getMaterial()
    {
        return material;
    }

    public int getBandwidth()
    {
        return bandwidth;
    }

    public int getLength()
    {
        return length;
    }

    public double getTravelTime()
    {
        return travelTime;
    }

/*Setters*/
    public void setA(Vertex a)
    {
        this.a = a;
    }

    public void setB(Vertex b)
    {
        this.b = b;
    }

    public void setMaterial(String material)
    {
        this.material = material;
    }

    public void setBandwidth(int bandwidth)
    {
        this.bandwidth = bandwidth;
    }

    public void setLength(int length)
    {
        this.length = length;
        updateTT();             /*Recalculate the travel time to reflect the new length*/
    }

/*Misc. Methods*/
    public String toString()
    {
        return ("A: "+a+"\tB:"+b+"\tMaterial: "+material+"\tBandwidth: "+bandwidth+"\tLength: "+length+"\tTravel Time: "+travelTime);
    }

    //Compare two edges to see which has the lower travel time
    public int compare(Edge temp)
    {
        if(travelTime > temp.getTravelTime())
        {
            return 1;
        }
        else if(travelTime < temp.getTravelTime())
        {
            return -1;
        }
        return 0;   //Return 0 if they're equal
    }

    //Update the time to travel
    public void updateTT()
    {
        //The time to travel each link is equal to the length of the cable divided by the speed at which data can be sent (determined by the link type)
        if(material.equals("copper"))
        {
            travelTime = length/(double)copperSpeed;
        }
        else
        {
            travelTime = length/(double)fiberSpeed;
        }
    }
}
