import java.util.Scanner;
import java.util.ArrayList;
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
        Vertex[] vertices = new Vertex[numVertices];            //Create an adjacency list for the graph
        for(int i = 0; i < numVertices; i++)                 //Initialize the adjacency list
            vertices[i] = new Vertex(i);

        ArrayList<Edge> edges = new ArrayList<Edge>();
        while(in.hasNextLine())
        {
            String[] theEdge = in.nextLine().split(" ");        //Take the line of info about the edge

            Vertex a = vertices[Integer.parseInt(theEdge[0])];
            Vertex b = vertices[Integer.parseInt(theEdge[1])];
            String material = theEdge[2];
            int bandwidth = Integer.parseInt(theEdge[3]);
            int length = Integer.parseInt(theEdge[4]);

            Edge E1 = new Edge();           //Create a temporary edge
            Edge E2 = new Edge();           //Create a temporary edge

            E1.setA(a);                     //Set the first vertex
            E1.setB(b);                     //Set the second vertex
            E1.setMaterial(material);       //Set the material
            E1.setBandwidth(bandwidth);     //Set the bandwidth
            E1.setLength(length);           //Set the length

            E2.setA(b);                     //Set the first vertex
            E2.setB(a);                     //Set the second vertex
            E2.setMaterial(material);       //Set the material
            E2.setBandwidth(bandwidth);     //Set the bandwidth
            E2.setLength(length);           //Set the length

            a.getEdges().addFirst(E2);
            b.getEdges().addFirst(E1);

            edges.add(E1);
        }

/*Vertices and Edges are completed*/
        Graph network = new Graph(vertices, edges);

        in = new Scanner(System.in);
        while(true)
        {
            /*Present user with options*/
            System.out.println("Would you like to:\n"+
                               " 1. Find the lowest latency path between any two points\n"+
                               " 2. Determine whether or not the graph is copper-only connected\n"+
                               " 3. Find the maximum amount of data that can be transmitted from one vertex to another\n"+
                               " 4. Find the minimum average latency spanning tree\n"+
                               " 5. Determine whether the graph would remain connected even if any two vertices fail\n"+
                               " 6. Quit the program\n");
            int response = -1;
            while(response > 6 || response < 1)
            {
                response = in.nextInt();
            }
            if(response == 1)           //1. Find the lowest latency path between any two points
            {
                //Give the bandwidth available along that path
                //Prompt the user for the two vertices
                System.out.print("Enter any two vertices from "+0+" to "+(numVertices-1)+": ");
                int a = in.nextInt();
                int b = in.nextInt();
                network.lowestLatency(a, b);
            }
            else if(response == 2)      //2. Determine whether or not the graph is copper-only connected
            {
                //aka connected when only considering copper links (ignore the fiber-optic)
                network.copperConnected();
            }
            else if(response == 3)      //3. Find the maximum amount of data that can be transmitted from one vertex to another
            {
                //Prompt the user for the two vertices
                System.out.print("Enter any two vertices from "+0+" to "+(numVertices-1)+": ");
                int a = in.nextInt();
                int b = in.nextInt();
                network.maxData(a, b); //Output the maximum amount of data the can be transmitted from a to b
            }
            else if(response == 4)      //4. Find the minimum average latency spanning tree
            {
                network.lowestAverageLatency();     //aka the spanning tree with the lowest average latency per edge
            }
            else if(response == 5)      //5. Determine whether the graph would remain connected if any two vertices fail
            {
                //You are not prompting the user for two vertices that could fail
                //This is whether any pair of vertices that, should they both fail, would cause the graph to become disconnected
                    //Do we just want to determine the min cut and see if it's two or less?
            }
            else                        //6. Quit the program
            {
                System.out.println("Exiting program");
                System.exit(0);
            }
        }
    }

        //Output the sequence of vertices that comprise the lowest-latency path, in order from a to b
            //Must find the path between these that will take the least amount of time for a single packet to travel
                //Assume that time required to travel is the sum of the times required to travel each link
        //Output the bandwidth thtat is available along the resulting path
                //aka the minimum bandwidth of all the edges in the path
}
