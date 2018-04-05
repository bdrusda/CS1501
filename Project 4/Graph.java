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
        ArrayList<Edge> spanningTree = new ArrayList<Edge>();      //Array list of edges for the min spanning tree
        int[] parent = new int[numVertices];
        int[] rank = new int[numVertices];
        for (int i = 0; i < numVertices; i++)   //Initialize parent of i to itself, ranks to 0
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
            Edge e = edges.get(currEdge);
            int v = e.getA().getNumber();
            int w = e.getB().getNumber();
            if(!connected(v, w, parent))
            { //Edge (v,w) does not create a cycle
                union(v, w, parent, rank); //Logically add the edge (v,w) to the minimum spanning tree's union of all its edges
                spanningTree.add(e);  //Add edge e to mst to refer to it later
                latency += e.getTravelTime();
            }
			currEdge++; //In the next iteration, look at the next edge in the tree
        }

        //Once the algorithm is done, compute the latencies
        latency = latency*Math.pow(10, 9);                //Total latnecy in nanoseconds
    	double averageLatency;             //The average latency

        averageLatency = latency/spanningTree.size();

        System.out.println("The minimum spanning tree for this graph, based on latency, is:");
        for(int i = 0; i < spanningTree.size(); i++)
        {
            Edge temp = spanningTree.get(i);
            System.out.println("[" + temp.getA().getNumber() + "," + temp.getB().getNumber() + "]");
        }

        System.out.printf("The average latency of this spanning tree is "+String.format("%.5f", averageLatency)+" nanoseconds.");
    }

	//When any two vertices fail, determine whether or not the graph is still connected
	public void minCutGreaterThanTwo()
    {
		//Permute all possible pairs of vertices failing and calculate if the graph is connected or not
		for(int i = 0; i < numVertices-1; i++)
        {
			for(int j = i+1; j < numVertices; j++)
            {
				//Traverse the graph ignoring vertices[i] and vertices[j]
				//If the path's length ever reaches numVertices-2, then it is connected
				Vertex startVertex = null;
				Vertex failureOne = vertices[i];
				Vertex failureTwo = vertices[j];
				boolean[] visited = new boolean[numVertices];

				//Mark that the failed vertices have already been visited so the traversal doesn't visit them
				visited[failureOne.getNumber()] = true;
				visited[failureTwo.getNumber()] = true;

				//Set the starting vertex for to explore from; it can be any vertex that isn't in the pair of failing vertices
				if(i != 0)
                { //If we're omitting vertex 0, then make sure we don't start there
					startVertex = vertices[0];
				} else
                { //Vertex 0 failed, so determine a new vertex that didn't fail
					if(j != numVertices-1)
                    {
						startVertex = vertices[j+1];
					} else if(j-i != 1)
                    {
						startVertex = vertices[j-1];
					} else
                    {
						System.out.println("-- This graph IS NOT connected when any two vertices fail."); //There are only 2 vertices in the graph so if both fail, it is not connected
						return;
					}
				}

				//Pass in the visited array and mark the vertices that were traversed across
				findConnectivityWithoutTwovertices(startVertex, failureOne, failureTwo, visited);

				//Check to make sure all vertices were visited; in other words, the graph is still connected despite the failures
				boolean graphIsConnected = true;
				for(int k = 0; k < visited.length; k++)
                {
					if(visited[k] == false)
                    { //A node was not visited, so the graph is not connected
						graphIsConnected = false;
						break;
					}
				}

				if(!graphIsConnected)
                { //If we find out that any two pairs of vertices failing causes the graph to not be connected, return
					System.out.println("-- This graph IS NOT connected when any two vertices fail.");
					return;
				}
			}
		}

		System.out.println("-- This graph IS connected when any two vertices fail.");
		return; //All possible combinations of two vertices failing produced connected graphs
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

	//Perform a bread-first search and check that all nodes are visited
	private void findConnectivityWithoutTwovertices(Vertex curr, Vertex a, Vertex b, boolean[] visited)
    {
		if(curr == null || a == null || b == null || visited == null) return; //Any invalid input will return null promptly

		if(visited[curr.getNumber()] == true)
        { //This node has already been visited
			return;
		}

		visited[curr.getNumber()] = true; //Mark that the current node has been visited

		LinkedList<Edge> curredges = curr.getEdges();

		for(Edge edge : curredges)
        { //Perform a depth-first search to attempt to traverse all nodes except for the failed ones in the graph
			Vertex partialDest = edge.getB(); //Get the destination Vertex of this current edge
			if(visited[partialDest.getNumber()] == true) continue; //We already traversed to this node, so don't cycle back to it

			findConnectivityWithoutTwovertices(partialDest, a, b, visited); //Recursively traverse the graph
		}

		return;
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
