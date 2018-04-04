public class Neighbor
{
    //Neighbor node to keep track of a a vertex's neighbors in the adjacency list
    private int vertex;//The vertex that is a neighbor
    private Neighbor next;  //The next neighbor
/*Constrctors*/
    public Neighbor()
    {
        vertex = -1;
        next = null;
    }

    public Neighbor(int i)
    {
        vertex = i;
        next = null;
    }

/*Setters*/
    public void addNeighbor(int i)
    {
        if(vertex == -1)    /*If this vertex isn't initialized*/
        {
            vertex = i;         /*Set this neighbor equal to the vertex*/
        }
        else                /*If this node has a vertex*/
        {
            if(next == null)    /*If next is not initialized*/
            {
                next = new Neighbor(i); /*Set next equal to the neighbor*/
            }
            else                /*If next exists*/
            {
                next.addNeighbor(i);    /*Go to it and recursively addNeighbor*/
            }
        }
    }

    public void setVertex(int i)
    {
        vertex = i;
    }

/*Getters*/
    public int getVertex()
    {
        return vertex;
    }

    public Neighbor getNext()
    {
        return next;
    }

/*Misc. Methods*/
    public String toString()
    {
        if(next == null)                        /*If next is not initialized*/
        {
            return ""+vertex;                       /*Return the vertex, this is the last one*/
        }
        else                                    /*If next is initialized*/
        {
            return vertex+" "+(next.toString());    /*Recursively add all neighbors*/
        }
    }
}
