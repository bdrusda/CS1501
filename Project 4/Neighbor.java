public class Neighbor
{
    //Neighbor node to keep track of a a vertex's neighbors in the adjacency list
    //If we want to get the edge's information, we will look up vertex a in the adjacency list and the edge is stored with the corresponding b
    private Edge edge;      //The edge between the vertex at the index and the vertex in this neighbor
    private int vertex;     //The vertex that is a neighbor
    private Neighbor next;  //The next neighbor

/*Constrctors*/
    public Neighbor()
    {
        edge = null;
        vertex = -1;
        next = null;
    }

    public Neighbor(Edge i)
    {
        edge = i;
        vertex = i.getB();
        next = null;
    }

/*Setters*/
    public void addNeighbor(Edge i, char to)
    {
        if(vertex == -1)    /*If this vertex isn't initialized*/
        {
            edge = i;
            if(to == 'a')
                vertex = i.getA();          /*Set this neighbor equal to the vertex*/
            else
                vertex = i.getB();          /*Set this neighbor equal to the vertex*/
        }
        else                /*If this node has a vertex*/
        {
            if(next == null)    /*If next is not initialized*/
            {
                next = new Neighbor(i); /*Set next equal to the neighbor*/
                if(to == 'a')           /*If we wanted to go to vertex a instead of b, this is set incorrectly now*/
                    next.setVertex(i.getA());   /*So set the vertex to the correct value*/
            }
            else                /*If next exists*/
            {
                next.addNeighbor(i, to);    /*Go to it and recursively addNeighbor*/
            }
        }
    }

    public void setVertex(int i)
    {
        vertex = i;
    }

/*Getters*/
    public Edge getEdge()
    {
        return edge;
    }

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
