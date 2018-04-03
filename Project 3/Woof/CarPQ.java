class CarPQ
{
    Car[] pq = new Car[10];                     //How should this be sized?
    int numCars;

    public CarPQ()                              //Intialize an empty CarPQ
    {
        numCars = 0;
    }

    public void add(Car temp)                   //Add a new car
    {
        if(isFull())
        {
            resizePQ();
        }

        pq[numCars] = temp;                         //Add the car to the PQ
        swim(numCars);                              //Swim the car up the PQ until it is in the right spot
        numCars++;                                  //Increment the number of cars in the PQ
    }

    public void remove()                        //Remove the minimum car
    {
        if(numCars == 0)
        {
            System.out.println("\nThere are no more cars to remove");
        }
        else
        {
            //Replace root with last available leaf and delete it
            swap(0, --numCars);                         //Replace the root with the last car and decrement the numbre of cars in the PQ accordingly
            pq[numCars] = null;                         //Remove the car
            //Sink the new root down until it supports the heap property
            sink(0);
        }
    }

    public Car getMin()
    {
        if(numCars == 0)
        {
            System.out.println("There are no cars in the database");
            return null;
        }

        return pq[0];
    }

    private void swim(int i)                          //Swim a car up
    {
        while((i > 0) && (greater(((i-1)/2), i)))   //While i isn't the root and its parent is greater than it
        {
            swap(i, (i-1)/2);               //Swap the two
            i = (i-1)/2;                             //And update i
        }
    }

    private void sink(int i)                          //Sink a car down
    {
        while(((2*i+1) < numCars))                  //While the left child is inside of the array's bounds
        {
            int child = 2*i + 1;
            int smaller = child;

            if(child+1 < numCars)                       //Check if the right child exists as well
            {
                if(greater(child, child+1))                 //If the left child is greater than the right child
                {
                    smaller = child+1;                          //Set the right child to be the smaller
                }
            }

            if(greater(i, smaller))                     //If the parent is greater than the smaller child
            {
                swap(i, smaller);                           //Swap the parent with the child
                i = smaller;                                //and update i accordingly
            }
            else                                        //Otherwise
            {
                break;                                      //We are done sinking
            }
        }
    }

    private void updateIndex(int i)
    {
        pq[i].updateMIndex(i);
    }

    /*Class methods*/
    private boolean greater(int a, int b)       //Check if a is greater than b
    {
        if(pq[a].getMileage() > pq[b].getMileage())
        {
            return true;
        }
        return false;
    }

    private void swap(int a, int b)          //Swap two cars
    {
        //Swap the cars in the given indices
        Car temp = pq[a];
        pq[a] = pq[b];
        pq[b] = temp;

        updateIndex(a);
        updateIndex(b);
    }

    private boolean isFull()                    //Check if the PQ array is full
    {
        return numCars >= pq.length;
    }

    private void resizePQ()                    //Resize the PQ array if it is full
    {
        Car[] temp = new Car[numCars*2];            //Create a temp array twice the size of the original
        for(int i = 0; i < numCars; i++)
            temp[i] = pq[i];                        //Repopulate the array with cars
        pq = temp;                                  //Transfer the contents back to pq
    }

    public String toString()
    {
        StringBuilder allCars = new StringBuilder();
        for(int i = 0; i < numCars; i++)
            allCars.append(pq[i]+"\n");
        return allCars.toString();
    }
}
