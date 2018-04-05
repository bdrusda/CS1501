import java.util.*;
import java.io.*;

public class Graph
{
	private int numVertices;                                       //Number of vertices
	private Vertex[] vertices;                                     //Array of vertices
	private ArrayList<Edge> edges;                                 //Array list of edges

    //Initialize the graph with a set of bertices and edges
	public Graph(Vertex[] vertices, ArrayList<Edge> edges)
    {
        this.vertices = vertices;
        this.edges = edges;
        numVertices = vertices.length;
	}

	//Path between two given vertices with the lowest latency
	public void lowestLatency(int a, int b)
    {
		Vertex source = vertices[a];
		Vertex destination = vertices[b];

		//Get the shortest path between two vertices
		LLObject info = shortestPath(source, destination, ""+source.getNumber(), 0, -1);    //Compute the shortest latency path along with its latency and bandwidth bottleneckage
		if(info == null)
            return;

		String path = info.getPath();
		StringBuilder formatPath = new StringBuilder();
		for(int i = 0; i < path.length(); i++)                //Take the path and put arrows between the vertices
        {
            formatPath.append(path.charAt(i));
			if(i != path.length()-1)
            {
				formatPath.append(" > ");
			}
		}
        path = formatPath.toString();                         //The min path followed
		double latency = info.getLatency()*Math.pow(10,9);    //Time to send one packet from a to b in nanoseconds
		int bandwidth = info.getBandwidth();                  //Bottlenecked bandwidth
		System.out.println("Shortest path from "+source+" to "+destination+": "+path+"\nLatency: "+String.format("%.5f", latency)+" nanoseconds\nBandwidth: "+bandwidth+" Mbps\n");
	}

	//Check if the path is still connected when fiber isn't considered
	public void copperConnected()
    {
        boolean containsCopper = false;
		for(int i = 0; i < numVertices; i++)                      //Iterate through all of the vertices
        {
			LinkedList<Edge> currEdges = vertices[i].getEdges();     //Get all of the edges for vertex i

            containsCopper = false;
			for(int j = 0; j < currEdges.size(); j ++)               //Iterate through all edges
            {
				if(currEdges.get(j).getMaterial().equals("copper"))         //If there is an edge made of copper
                {
					containsCopper = true;
					break;
				}
			}

            if(containsCopper == false)                             //If, after checking all edges, there is no copper attached to this vertex
            {
                System.out.println("The graph is not copper connected");
                return;
            }
		}

		if(containsCopper)                                            //If each vertex has a copper edge, the graph is copper connected
        {
            System.out.println("The graph is copper connected");
		}
    }

	//Find the path between any two vertices that produces the largest bandwidth
	public void maxData(int a, int b)
    {
		Vertex source = vertices[a];
		Vertex destination = vertices[b];

		//Calculate all of the paths between the vertices and print out the statistics of the path with the maximum bandwidth
		int bandwidth = maxData(source, destination, ""+source.getNumber(), -1);
		System.out.println("Max data that can flow from vertex "+source.getNumber()+" to vertex "+destination.getNumber()+": "+bandwidth+" MB/s");
	}

    //Find the minimum spanning tree with the lowest latency
    public void lowestAverageLatency()
    {
        //Kruskal's algorithm modified from princeton method
        ArrayList<Edge> spanningTree = new ArrayList<Edge>();       //Array list of edges for the min spanning tree
        int[] parent = new int[numVertices];
        int[] rank = new int[numVertices];
        for (int i = 0; i < numVertices; i++)                       //Initialize parent of i to itself, ranks to 0
        {
            parent[i] = i;
            rank[i] = 0;
        }

		Collections.sort(edges, (e1, e2) -> e1.compare(e2));  //Sort the edges in by minimum weight DEBUG kinda confusing

		double latency = 0.0;                                 //Tree's total latency

        //Greedy Algorithm - Adapted from princeton KruskalMST
		int currEdge = 0;                                     //Edge that will be added assuming a cycle doesn't occur
        while(currEdge != edges.size()-1 && spanningTree.size() < numVertices - 1)  //Run through all edges or until all vertices are connected
        {
            Edge e = edges.get(currEdge++);     //Get the edge and move to the next one
            int v = e.getA().getNumber();
            int w = e.getB().getNumber();
            if(!connected(v, w, parent))        //If the edge doesn't create a cycle
            {
                union(v, w, parent, rank);      //Add the edge to the minimum spanning tree's union
                spanningTree.add(e);            //Add edge e to mst
                latency += e.getTravelTime();   //Add it to the total latency
            }
        }

        //Once the algorithm is done, compute the latencies
        latency = latency*Math.pow(10, 9);      //Total latency in nanoseconds
    	double averageLatency = latency/spanningTree.size();                 //The average latency

        //Print out the spanning tree and its info
        System.out.println("The minimum spanning tree for this graph, based on latency, is:");
        for(int i = 0; i < spanningTree.size(); i++)
        {
            Edge temp = spanningTree.get(i);
            System.out.println("[" + temp.getA().getNumber() + "," + temp.getB().getNumber() + "]");
        }
        System.out.printf("The average latency of this spanning tree is "+String.format("%.5f", averageLatency)+" nanoseconds.");
    }

	//Determine if the graph will remain connected given any two vertices failing
	public void connectedWithFailure()
    {
		//I feel like this is intended to be a min cut determination, but, as there is no source/destination
        //if you just check that all vertices have more than 2 edges, this will be necessary and sufficient

        boolean threePlusEdges = true;
		for(int i = 0; i < numVertices; i++)
        {
            Vertex curr = vertices[i];  //Get the i'th vertex
            if(curr.getEdges().size() <= 2)   //If it has two or less edges, we can't handle two failures
            {
                threePlusEdges = false;
                break;
            }
        }

        if(threePlusEdges)
        {
            System.out.println("The network will remain connected if any two vertices fail");
        }
        else
        {
            System.out.println("There is a pair of vertices whose failure will disconnect the graph");
        }
	}

    /*Extra Methods*/
    //Calculates the shortest path between two given vertices in terms of latency
	private LLObject shortestPath(Vertex source, Vertex destination, String path, double latency, int minBandwidth)
    {

		if(source == destination)                         //We are at the end, return what we have
        {
            LLObject info = new LLObject(path, latency, minBandwidth);
			return info;
        }

		LinkedList<Edge> currEdges = source.getEdges();   //Get this vertex's edges

		double minLatency = -1.0;                         //Length of the minimum length path thusfar
		String partialPath = "";                          //String containing the path thusfar

		for(int i = 0; i < currEdges.size(); i++)         //Loop through all edges in order to determine all possible paths
        {
            Edge tempEdge = currEdges.get(i);                        //Get the i'th edge

			Vertex partialDest = tempEdge.getA();                    //Get the vertex it is going to

			if(!(path.contains("" + partialDest.getNumber())))   //If the path doesn't contain this vertex yet
            {
    			String newPath = path + partialDest.getNumber(); //Generate the new path
    			double newLatency = latency + tempEdge.getTravelTime();  //Calculate the length to this vertex
    			int newMinBandwidth = minBandwidth;

    			if(minBandwidth == -1.0 || tempEdge.getBandwidth() < minBandwidth) //If there was no previous bandwidth or this one is lower
                {
                    newMinBandwidth = tempEdge.getBandwidth();                 //Update the minimum
                }

    			LLObject info = shortestPath(partialDest, destination, newPath, newLatency, newMinBandwidth); //Go to the next path recursively
    			if(info != null)                                         //If this edge has no data, visit the next edge
                {
        			String thisPath = info.getPath();   //A full path to the destination
        			double currLatency  = info.getLatency();
                    int pathBandwidth = info.getBandwidth();

        			if(minLatency == -1 || currLatency < minLatency)   //If there is no existing path or a better path ahs been found
                    {
        				minLatency = currLatency;                         //Update the length
        				partialPath = thisPath;                             //Take this edge's path
        				minBandwidth = pathBandwidth;                          //Take this edge's bandiwidth
        			}
                    else if(currLatency == minLatency && pathBandwidth > minBandwidth)   //If the length's are the same, but the bandwidth is smaller, swap
                    {
        				minLatency = currLatency;
        				partialPath = thisPath;
        				minBandwidth = pathBandwidth;
        			}
                }
            }
		}

		if(minLatency != -1.0)    //If there is a path from this vertex
        {
            LLObject info = new LLObject(partialPath, minLatency, minBandwidth);
			return info;
		}

		return null; //We're not at the destination and no edges from the current vertex are valid (there are none or none reach the destination)
	}

    //Calculates the path that provides the greatest amount of bandwidth transfer
	private int maxData(Vertex source, Vertex destination, String path, int bandwidth)
    {
		if(source == destination)                         //We have reached the destination; we can return the data
        {
			return bandwidth;
		}

		LinkedList<Edge> edges = source.getEdges();       //Get all of the source's edges

		int max = -1;
		for(int i = 0; i < edges.size(); i++)             //Go through all paths, updating the max bandwidth when applicable
        {
            Edge tempEdge = edges.get(i);                   //Get the current edge
			Vertex partialDest = tempEdge.getA();           //Get the vertex that it is going to
			if(!path.contains(""+partialDest.getNumber()))  //If the vertex has not yet been visited
            {
                int newBandwidth = bandwidth;
                if(newBandwidth == -1 || tempEdge.getBandwidth() < newBandwidth)
                {
                    newBandwidth = tempEdge.getBandwidth();     //Update bandwidth
                }

                String newPath = path+partialDest.getNumber();
                int pathBandwidth = maxData(partialDest, destination, newPath, newBandwidth);  //Move through the graph recursively
                if(pathBandwidth != -1)                                                 //If the search resulted in a new bandwidth
                {
                    if(pathBandwidth > max)                                                 //Check if it is graeter than the max
                    {
                        max = pathBandwidth;                                                    //If it is, update it
                    }
                }
            }
		}

		return max;
	}

	//Inputs adapted to from princeton union method
    private void union(int p, int q, int[] parent, int[] rank)
    {
        int rootP = find(p, parent);
        int rootQ = find(q, parent);
        if (rootP == rootQ) return;

        // make root of smaller rank point to root of larger rank
        if(rank[rootP] < rank[rootQ])
            parent[rootP] = rootQ;
        else if(rank[rootP] > rank[rootQ])
            parent[rootQ] = rootP;
        else
        {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
    }

	//Inputs adapted from the princeton connected method - compares the groups that p and q belong to, determines if they match
	private boolean connected(int p, int q, int[] parent)
    {
        return find(p, parent) == find(q, parent);
    }

	//Inputs adapted from the princeton find method - returns the group that p belongs to
	private int find(int p, int[] parent)
    {
        while (p != parent[p])
        {
            parent[p] = parent[parent[p]];    // path compression by halving
            p = parent[p];
        }
        return p;
    }
}
