public class MakeModelTable
{
    int size = 103;                 //Prime number over 100, holds around 50 make/model combinations well
    CarPQ[] MM = new CarPQ[size];
    int numPQs = 0;
    char mode;

    public MakeModelTable(char mode)                //Construct the table
    {
        this.mode = mode;           //Set the mode
    }

    public void add(Car temp)                       //Add a car to the table
    {
        if(isGettingFull())         //If we are over half full
            resize();                   //Resize the array

        String hashString = temp.getMake()+temp.getModel();        //Get temp's make and model

        int index = getHash(hashString);                            //Put in hash method
        //We want either an empty spot, or a spot with a PQ that holds the correct make and model
        while(MM[index] != null)   //While there is something in this index
        {
            //Check if it is the right make and model (get min and see what it is)
            Car inIndex = MM[index].getMin();
            if(inIndex.getMake().equals(temp.getMake()) && inIndex.getModel().equals(temp.getModel()))  //If it is the right make and model, add the car
                break;                                                                                      //We're at the right index, break
            else                                                                                        //Otherwise, move on to the next index
                index++;
        }

        if(MM[index] == null)                       //If the index was empty
            MM[index] = new CarPQ(mode, true);          //Make a new PQ

        MM[index].add(temp);                        //Whether the PQ existed or a new one was created, add the car

        if(mode == 'p')                             //Price mode
            temp.setPriceMMIndex(index);
        else                                        //Mileage mode
            temp.setMileageMMIndex(index);
    }

    public Car lookup(String make, String model)    //Lookup a car by make and model
    {
        int index = getHash(make+model);
        int initIndex = index;

        try
        {
            while(!(MM[index%size].getMin().getMake().equals(make)) || !(MM[index%size].getMin().getModel().equals(model)))   //While we don't have a matching make and model PQ to grab the result min from
            {
                index++;
                if(index == initIndex*2)    //If we checked every index
                    return null;                //The make/model PQ isn't in the MMTable, return null
            }
        }
        catch(NullPointerException e)
        {
            return null;                    //If the index is null, there is no PQ here - return null
        }

        return MM[index%size].getMin();          //Return the car in the PQ
    }

    public CarPQ getPQ(int i)                       //Get the PQ in the given index
    {
        return MM[i];               //return the PQ in the given index
    }

    private int getHash(String hashString)          //Produce the hash given the make and model hashstring
    {
        double hash = 0;
        for(int i = 0; i < hashString.length(); i++)    //Horner's method to create a hash
            hash += hashString.charAt(i) * Math.pow(256, i);
        hash = hash % size;                             //Get a result within the array
        return (int) hash;                              //Return the integer result
    }

    private boolean isGettingFull()                 //Check if the array is half full
    {
        return numPQs >= (size/2);
    }

    private void resize()                           //Resize the PQ array if it is getting full
    {
        CarPQ[] temp = new CarPQ[numPQs*2+1];           //Create a temp array twice the size of the original (+1 so it's odd)
        for(int i = 0; i < numPQs; i++)
            temp[i] = MM[i];                        //Repopulate the array with PQs
        MM = temp;                                  //Transfer the contents back to hash table
    }

    public String toString()
    {
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            if(MM[i] != null)
                temp.append("CarPQ "+i+":\n"+MM[i]+"\n");
        }
        return temp.toString();
    }
}
