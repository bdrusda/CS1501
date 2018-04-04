import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class NetworkAnalysis
{
    public static void main(String[] args)
    {
        //Ensure that only one commandline argument was used
        if(args.length != 1)
        {
            System.out.println("Invalid number of arguments\nCorrect format: java NetworkAnalysis [data_filename]");
            System.exit(-1);
        }

        Scanner in = new Scanner(System.in);

        try
        {
            in = new Scanner(new File(args[0]));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Data File "+args[0]+" not found");
            System.exit(-1);
        }

        //Interpret the file and store graph as an adjacency list
        int numVertices = in.nextInt();        in.nextLine();   //Get the number of vertices and clear the buffer
        Neighbor[] adjList = new Neighbor[numVertices];         //Create an adjacency list for the graph
        for(int i = 0; i < adjList.length; i++)                 //Initialize the adjacency list
            adjList[i] = new Neighbor();

        while(in.hasNextLine())
        {
            String[] theEdge = in.nextLine().split(" ");        //Take the line of info about the edge
            Edge temp = new Edge();                             //Create a temporary edge
            temp.setA(Integer.parseInt(theEdge[0]));            //Set the first vertex and clear the buffer
            temp.setB(Integer.parseInt(theEdge[1]));            //Set the second vertex and clear the buffer
            temp.setMaterial(theEdge[2]);                       //Set the material
            temp.setBandwidth(Integer.parseInt(theEdge[3]));    //Set the bandwidth
            temp.setLength(Integer.parseInt(theEdge[4]));       //Set the length

            /*It REALLY feels like we should be storing these complete edges somewhere, but idk where*/
                /*Maybe we store the edges in the neighbors?  The array index marks a and the vertex in neighbor is b*/
            adjList[temp.getA()].addNeighbor(temp.getB());      //Mark B as a neighbor of A
            adjList[temp.getB()].addNeighbor(temp.getA());      //Mark A as a neighbor of B
    //This will allow duplicates, but only if the list given to us has duplicates
        }

        //After loading the graph
        //Present user with options
            //1. Find the lowest latency path between any two points
                //Give the bandwidth available along that path
                //To do this:
                    //1. Prompt the user for the two vertices
                    //2. Output the sequence of vertices that comprise the lowest-latency path, in order from a to b
                        //Must find the path between these that will take the least amount of time for a single packet to travel
                            //Assume that time required to travel is the sum of the times required to travel each link
                    //3. Output the bandwidth thtat is available along the resulting path
                        //aka the minimum bandwidth of all the edges in the path

            //2. Determine whether or not the graph is copper-only connected
                //aka connected when only considered copper links (ignore the fiber-optic)
            //3. Find the maximum amount of data that can be transmitted from one vertex to another
                //Prompt the user for the two vertices
                //Output the maximum amount of data the can be transmitted from a to b
            //4. Find the minimum average latency spanning tree
                //aka the spanning tree with the lowest average latency per edge
            //5. Determine whether the graph would remain connected if any two vertices fail
                //You are not prompting the user for two vertices that could fail
                //This is whether any pair of vertices that, should they both fail, would cause the graph to become disconnected
                    //Do we just want to determine the min cut and see if it's two or less?
            //6. Quit the program
    }
}
