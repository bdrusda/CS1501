import java.util.*;
import java.io.*;

public class Graph
{
	private int numVertices;                                       //Number of vertices
	private Vertex[] vertices;                                     //Array of vertices
	private ArrayList<Edge> edges;                                 //Array list of edges
	private ArrayList<Edge> minSpanningTreeWithLowestLatency;      //Array list of edges for the min spanning tree
	private double lowestAverageLatency;                           //The lowest average latency

    //Initialize the graph with a set of bertices and edges
	public Graph(Vertex[] vertices, ArrayList<Edge> edges)
    {
		minSpanningTreeWithLowestLatency = null;  //?
        this.vertices = vertices;
        this.edges = edges;
	}

	//Path between two given vertices with the lowest latency
	public void lowestLatency(int a, int b)
    {
		Vertex source = vertices[a];
		Vertex destination = vertices[b];

		//Get the shortest path between two vertices
		LLObject info = shortestPath(source, destination, "" + source.getNumber(), 0L, -1);

		if(info == null)
        {
            return;
        }

		String path = info.getPath();
		String directedPath = "";
		for(int i = 0; i < path.length(); i++)    //Change this too fancy DEBUG
        {
			if(i < path.length()-1)
            {
				directedPath += path.charAt(i) + " -> ";
			} else
            {
				directedPath += path.charAt(i);
			}
		}
		path = directedPath;
		double pathTravelTime = info.getLength();     //Time to send a set of data from the start vertex to the destination vertex in nanoseconds
		int pathMinBandwidth = info.getBandwidth();   //Minimum bandwidth of the path between the vertices; in other words, the max amount of data allowed along the path
		System.out.printf("\nShortest path: %s\nTime/Latency: %.3f nanoseconds\nBandwidth: %d Mbps\n", path, pathTravelTime, pathMinBandwidth);
	}

	//Find out whether graph is connected with only copper, connected considering only copper, or neither
	public void copperConnected()
    {
        boolean hasCopperConnection = false;
		for(int i = 0; i < numVertices; i++)
        { //Iterate through every vertex and check to make sure it has at least one copper connection
			LinkedList<Edge> vertexedges = vertices[i].getEdges();
			hasCopperConnection = false;

			for(Edge edge : vertexedges){
				if(edge.getMaterial().equals("copper"))
                { //There exists a copper wire from this vertex
					hasCopperConnection = true;
					break;
				}
			}

			if(!hasCopperConnection)
            { //If this vertex does not have a single copper connection, then the graph cannot be copper connected
				break;
			}
		}

		if(hasCopperConnection)
        {
            System.out.println("-- This graph can be connected with only copper wires.  But, this graph also has fiber optic wires.");
		}
        else
        {
            System.out.println("-- This graph is not copper-only and cannot be connected with only copper wires.");
        }
    }

	//Find the path between two vertices that allows the maximum amount of data transfer at one time
	public void maxData(int a, int b)
    {
		Vertex source = vertices[a];
		Vertex destination = vertices[b];

		//Calculate all of the paths between the vertices and print out the statistics of the path with the maximum bandwidth
		int maxData = maxDataBetweenTwovertices(source, destination, "" + source.getNumber(), -1);
		System.out.println("\nMax amount of data between vertices " + source.getNumber() + " and " + destination.getNumber() + ": " + maxData + " Mbps");
	}

    //Calculate the minimum spanning tree with the lowest average latency among the edges.
    //Another definition of this is the tree that allows for the fastest data transfer across the entire graph.
    public void lowestSpanningTree()
    {
        if(minSpanningTreeWithLowestLatency == null)
        { //Generate the minimum spanning tree using Kruskal's algorithm
            lowestAverageLatency = KruskalMST() / minSpanningTreeWithLowestLatency.size(); //Divide the total latency by the number of edges to get the average latency
        } //Else, the Min Spanning Tree has already been created, so no need to waste CPU resources to generate the same on

        //Print out all of the edges used to construct this minimum spanning tree
        System.out.println("Lowest Average Latency Spanning Tree edges\n------------------------------------------");
        for(int i = 0; i < minSpanningTreeWithLowestLatency.size(); i++)
        {
            Edge temp = minSpanningTreeWithLowestLatency.get(i);
            System.out.println("( " + temp.getA().getNumber() + " , " + temp.getB().getNumber() + " )");
        }

        //Print out the average latency of the edges in this minimum spanning tree
        System.out.printf("\nThe average latency of this spanning tree is %.3f nanoseconds.\n", lowestAverageLatency);
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
	private LLObject shortestPath(Vertex source, Vertex destination, String path, double length, int minBandwidth)
    {
		if(source == null || destination == null || path == null || length < 0.0)
        {
            return null; //If anything is null for whatever reason, return as such
            //May not be necssary
        }
		if(source == destination)                         //We are at the end, return what we have
        {
            LLObject info = new LLObject(path, length, minBandwidth);
			return info;
        }

		LinkedList<Edge> currEdges = source.getEdges();   //Get this vertex's edges

		double minLength = -1.0;                         //Length of the minimum length path thusfar
		String partialPath = "";                          //String containing the path thusfar

		for(int i = 0; i < currEdges.size(); i++)         //Loop through all edges in order to determine all possible paths
        {
            Edge tempEdge = currEdges.get(i);                        //Get the i'th edge

			Vertex partialDest = tempEdge.getA();                    //Get the vertex it is going to

			if(!(path.contains("" + partialDest.getNumber())))   //If the path doesn't contain this vertex yet
            {
    			String newPath = path + partialDest.getNumber(); //Generate the new path
    			double newLength = length + tempEdge.getTravelTime();  //Calculate the length to this vertex
    			int newMinBandwidth = minBandwidth;

    			if(minBandwidth == -1.0 || tempEdge.getBandwidth() < minBandwidth) //If there was no previous bandwidth or this one is lower
                {
                    newMinBandwidth = tempEdge.getBandwidth();                 //Update the minimum
                }

    			LLObject info = shortestPath(partialDest, destination, newPath, newLength, newMinBandwidth); //Go to the next path recursively
    			if(info != null)                                         //If this edge has no data, visit the next edge
                {
        			String thisPath = info.getPath();   //A full path to the destination
    /*Then, currLength can be consolidated into length as well DEBUG*/
        			double currLength  = info.getLength();
        			int pathBandwidth = info.getBandwidth();

        			if(minLength == -1 || currLength < minLength)   //If there is no existing path or a better path ahs been found
                    {
        				minLength = currLength;                         //Update the length
        				partialPath = thisPath;                             //Take this edge's path
        				minBandwidth = pathBandwidth;                          //Take this edge's bandiwidth
        			}
                    else if(currLength == minLength && pathBandwidth > minBandwidth)   //If the length's are the same, but the bandwidth is smaller, swap
                    {
        				minLength = currLength;
        				partialPath = thisPath;
        				minBandwidth = pathBandwidth;
        			}
                }
            }
		}

		if(minLength != -1.0)    //If there is a path from this vertex
        {
            LLObject info = new LLObject(partialPath, minLength, minBandwidth);
			return info;
		}

		return null; //We're not at the destination and no edges from the current vertex are valid (there are none or none reach the destination)
	}

	//Helper method to calculate the maximum amount of data (highest bandwidth) that can be transferred between two vertices
	private int maxDataBetweenTwovertices(Vertex curr, Vertex dest, String path, int maxPathBandwidth)
    {
		if(curr == null || dest == null || path == null) return -1; //Any invalid input will return null promptly

		if(curr == dest)
        { //The destination vertex has been reached!  Return the necessary path data
			return maxPathBandwidth;
		}

		LinkedList<Edge> curredges = curr.getEdges();

		int max = -1;
		for(Edge edge : curredges)
        { //Traverse across all possible paths to the destination and find the maximum possible bandwidth
			Vertex partialDest = edge.getB();
			if(path.contains("" + partialDest.getNumber())) continue; //The vertex on the other side of this edge has already been travelled

			int newMaxPathBandwidth = maxPathBandwidth;
			if(newMaxPathBandwidth == -1 || edge.getBandwidth() < newMaxPathBandwidth) newMaxPathBandwidth = edge.getBandwidth(); //Set the new minimum path bandwidth

			String newPath = path + partialDest.getNumber();
			int pathBandwidth = maxDataBetweenTwovertices(partialDest, dest, newPath, newMaxPathBandwidth); //Recursively traverse across the graph
			if(pathBandwidth == -1) continue; //If there is no path data for the edge, go to the next edge
			if(pathBandwidth > max) max = pathBandwidth; //Found a path with a new max bandwidth
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

	//Execute Kruskal's algorithm to find the minimum spanning tree of the graph
	//	with the lowest weight.
	private double KruskalMST()
    {
		//Initialize our union-find components; taken from book's code
		int[] parent = new int[numVertices];
        byte[] rank = new byte[numVertices];
        for (int i = 0; i < numVertices; i++)
        {
            parent[i] = i;
            rank[i] = 0;
        }

		//Use a lambda expression to sort the edges from min to max weight
		Collections.sort(edges, (e1, e2) -> e1.compare(e2));

		minSpanningTreeWithLowestLatency = new ArrayList<Edge>(); //edges in the minimum spanning tree
		double weight = 0.0; //Total weight of the minimum spanning tree; in this case, weight will be the time taken to travel along all of the edges

        //Core of Kruskal's algorithm
		int currEdge = 0; //Current minimum weight edge we're considering adding to the MST assuming it doesn't create a cycle
        while(currEdge != edges.size()-1 && minSpanningTreeWithLowestLatency.size() < numVertices - 1){ //We have edges left and the spanning tree hasn't reached all vertices yet
            Edge e = edges.get(currEdge);
            int v = e.getA().getNumber();
            int w = e.getB().getNumber();
            if(!connected(v, w, parent)){ //Edge (v,w) does not create a cycle
                union(v, w, parent, rank); //Logically add the edge (v,w) to the minimum spanning tree's union of all its edges
                minSpanningTreeWithLowestLatency.add(e);  //Add edge e to mst to refer to it later
                weight += e.getTravelTime();
            }

			currEdge++; //In the next iteration, look at the next edge in the tree
        }

		return weight;
	}

	//Merges the component containing site p with the
	//	the component containing site q.
    private void union(int p, int q, int[] parent, byte[] rank)
    {
        int rootP = find(p, parent);
        int rootQ = find(q, parent);
        if (rootP == rootQ) return;

        //Make root of smaller rank point to root of larger rank
        if(rank[rootP] < rank[rootQ])
        {
            parent[rootP] = rootQ;
        }
        else if(rank[rootP] > rank[rootQ])
        {
            parent[rootQ] = rootP;
        }
        else
        {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
    }

	//Returns true if the the two sites are in the same component.
	//Indicates later on if the two vertices connecting will cause a cycle in the graph.
	private boolean connected(int p, int q, int[] parent)
    {
        return find(p, parent) == find(q, parent);
    }

	//Returns the component identifier for the component containing site p.
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
