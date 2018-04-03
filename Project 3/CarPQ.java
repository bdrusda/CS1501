class CarPQ
{
    Car[] pq;       //Array of cars
    int numCars;    //Number of cars in the pq
    char mode;      //Mode of the pq
    boolean MM;     //True means it's a make model PQ

    public CarPQ(char mode, boolean MM)         //Intialize an empty CarPQ
    {
        pq = new Car[10];   //Default size of 10
        numCars = 0;        //With no cars in it
        this.mode = mode;   //Set the mode
        this.MM = MM;       //Set the make model boolean
    }

    public void add(Car temp)                   //Add a new car
    {
        if(isFull())                                //If the array is full
            resizePQ();                                 //Resize it

        pq[numCars] = temp;                         //Add the car to the PQ
        swim(numCars);                              //Swim the car up the PQ until it is in the right spot
        numCars++;                                  //Increment the number of cars in the PQ
    }

    public void remove(int i)                   //Remove the minimum car
    {
        if(numCars == 0)                        //If the PQ is empty
            System.out.println("\nThere are no more cars to remove");   //Notify the user
        else
        {
            //Replace index with last available leaf and delete it
            swap(i, --numCars);                     //Replace the requested car with the last car and decrement the numbre of cars in the PQ accordingly

            if(i == numCars)                        //If this is the only node
                pq[numCars] = null;                     //Remove the car
            else                                    //Otherwise
                sink(i);                                //Sink the car
        }
    }

    public Car getMin()                         //Get the minimum car
    {
        if(numCars == 0)                            //If there are no cars
            return null;                                //Return null

        return pq[0];                               //Return the top of the heap
    }

    public void update(int i)                       //Change a car's location in PQ after an update
    {
        swim(i);                                        //Swim the car up if necessary
        sink(i);                                        //Sink it down if necessary
    }

    private void swim(int i)                        //Swim a car up
    {
        while((i > 0) && (greater(((i-1)/2), i)))   //While i isn't the root and its parent is greater than it
        {
            swap(i, (i-1)/2);                           //Swap the two
            i = (i-1)/2;                                //And update the index we are at
        }
    }

    private void sink(int i)                        //Sink a car down
    {
        while(((2*i+1) < numCars))                  //While the left child is inside of the array's bounds
        {
            int child = 2*i + 1;
            int smaller = child;                        //The smaller between the left and right children

            if(child+1 < numCars)                       //Check if the right child exists as well
            {
                if(greater(child, child+1))                 //If the left child is greater than the right child
                    smaller = child+1;                          //Set the right child to be the smaller
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

    private void updateIndex(int i)                 //Update the indexes
    {
        if(mode == 'p')                                 //If in price mode
        {
            if(MM)                                          //If in MakeModel PQ
                pq[i].setPriceMMPQIndex(i);                     //Update the index in the MakeModelPQ
            else                                            //Otherwise
                pq[i].setPriceIndex(i);                         //Update the index in the PricePQ
        }
        else                                            //If in mileage mode
        {
            if(MM)                                          //If in MakeModel PQ
                pq[i].setMileageMMPQIndex(i);                   //Update the index in the MakeModelPQ
            else                                            //Otherwise
                pq[i].setMileageIndex(i);                       //Update the index in the MileagePQ
        }
    }

    /*Class methods*/
    private boolean greater(int a, int b)           //Check if a is greater than b
    {
        if(mode == 'p')                             //Compare the two using price
        {
            if(pq[a].getPrice() > pq[b].getPrice())
                return true;
        }
        else                                        //Compare the two using mileage
        {
            if(pq[a].getMileage() > pq[b].getMileage())
                return true;
        }
        return false;
    }

    private void swap(int a, int b)                 //Swap two cars
    {
        //Swap the cars in the given indices
        Car temp = pq[a];
        pq[a] = pq[b];
        pq[b] = temp;

        updateIndex(a);                                 //Update the indexes of the
        updateIndex(b);                                 //two swapped cars
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
            allCars.append("Car "+i+": "+pq[i]+"\n");
        return allCars.toString();
    }
}
