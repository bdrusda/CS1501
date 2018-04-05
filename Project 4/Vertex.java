import java.util.LinkedList;

public class Vertex
{
    private LinkedList<Edge> edges;         //All of this vertex's edges
    private int number;                     //Vertex's identification

/*Constrctors*/
    public Vertex()
    {
        number = -1;
    }

    public Vertex(int i)
    {
        number = i;
        edges = new LinkedList<Edge>();
    }

/*Setters*/
    public LinkedList<Edge> getEdges()
    {
        return edges;
    }


    public int getNumber()
    {
        return number;
    }

    public String toString()
    {
        return (""+number);
    }
}
