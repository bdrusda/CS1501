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

            adjList[temp.getA()].addNeighbor(temp, 'b');             //Mark B as a neighbor of A
            adjList[temp.getB()].addNeighbor(temp, 'a');              //Mark A as a neighbor of B
        }
//        for(int i = 0; i < adjList.length; i++)                 //Initialize the adjacency list
//            System.out.println(adjList[i]);
/*Graph is fully loaded into the adjacency list*/

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

                lowest_latency(adjList, numVertices, a, b);
            }
            else if(response == 2)      //2. Determine whether or not the graph is copper-only connected
            {
                //aka connected when only considered copper links (ignore the fiber-optic)
            }
            else if(response == 3)      //3. Find the maximum amount of data that can be transmitted from one vertex to another
            {
                //Prompt the user for the two vertices
                System.out.print("Enter any two vertices from "+0+" to "+(numVertices-1)+": ");
                int a = in.nextInt();
                int b = in.nextInt();
                //Output the maximum amount of data the can be transmitted from a to b
            }
            else if(response == 4)      //4. Find the minimum average latency spanning tree
            {
                //aka the spanning tree with the lowest average latency per edge
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

    public static void lowest_latency(Neighbor[] adjList, int numVertices, int a, int b)
    {
        boolean reached = false;
        //We're going to want to use Dijkstra's for this
        double distTo[] = new double[numVertices];    //Create distance array corresponding to the distance from a to each vertex
        for(int i = 0; i < distTo.length; i++)  //Set the distance to MAX_INT initially
            distTo[i] = Integer.MAX_VALUE;

        Neighbor curr = adjList[a];             //Set curr = a
        Edge currEdge = curr.getEdge();
        distTo[currEdge.getA()] = 0;                //Distance is itself

        while(!reached)                         //While b is not yet reached
        {
            while(curr != null)                 //Check all edges in the neighbor
            {
                currEdge = curr.getEdge();          //Get the edge of the current vertex
                if(distTo[currEdge.getB()] == Integer.MAX_VALUE)    //The time calculation has not yet begun
                {
/*Replace all lengths with travel time once debugging is done*/
                    //distTo[currEdge.getB()] = currEdge.getTravelTime();    //Compute tentative time from start to the unvisited neighbor through curr
                    distTo[currEdge.getB()] = currEdge.getLength();    //Compute tentative time from start to the unvisited neighbor through curr
                }
                else
                {
                    //distTo[currEdge.getB()] += currEdge.getTravelTime();   //Add this time to the total time
                    distTo[currEdge.getB()] += currEdge.getLength();   //Add this time to the total time
                }
                curr = curr.getNext();

                for(int i = 0; i < distTo.length; i++)
                {
                    System.out.println("Vertex "+i+": "+distTo[i]);
                }
                System.out.println();
            }

                //Update any vertices for which a  lesser distance is computer
            //Mark curr as unvisited
            //Let curr be the unvisited vertex with the smallest tentative distance from start
        }

        //Output the sequence of vertices that comprise the lowest-latency path, in order from a to b
            //Must find the path between these that will take the least amount of time for a single packet to travel
                //Assume that time required to travel is the sum of the times required to travel each link
        //Output the bandwidth thtat is available along the resulting path
                //aka the minimum bandwidth of all the edges in the path
    }
}