public class VINTable
{
    int size = 503;                 //Prime number over 500, holds around 250 cars well
    Car[] VINs = new Car[size];     //Array of all the cars
    int numCars = 0;                //Number of cars in the array

    public VINTable()                   //Construct the VIN Table
    {

    }

    public void add(Car temp)           //Add car to the VIN Table
    {
        if(isGettingFull())                 //If the table is getting full
            resize();                           //Resize it

        String hashString = temp.getVIN();  //Get the vin and use it to hash
        int index = getHash(hashString);    //Put in hash method

        //If the spot is empty, add the car
        //If it isn't empty, move to the next index
        while(VINs[index%size] != null)     //%so we can circle back around
            index++;

        VINs[index] = temp;                 //Add the car to the hashtable (VIN as key)
        temp.setVINIndex(index);            //Mark car's location in the VINTable
        numCars++;                          //Increment the number of cars in the hashtable
    }

    public void remove(int i)           //Remove a car from the VIN Table
    {
        VINs[i] = null;                     //Set the car's index to null
        numCars--;                          //Decrement the number of cars in the table by 1
    }

    public Car lookup(String VIN)
    {
        int index = getHash(VIN);       //Get the hash using the vin
        int initIndex = index;          //Mark where we started

        try
        {
            while(!(VINs[index%size].getVIN().equals(VIN))) //If the VIN doesn't match (collision), move on until we find where it resides
            {
                index++;
                if(index == initIndex*2)    //If we checked every index
                    return null;                //The car isn't in the VINTable, return null
            }
        }
        catch(NullPointerException e)
        {
            return null;                    //If the index is null, there is no Car here - return null
        }

        return VINs[index%size];        //Return the car in the table
    }

    private int getHash(String hashString)  //Hash the first 5 characters of the substring
    {
        double hash = 0;

        for(int i = 0; i < hashString.length(); i++)    //Horner's method
            hash += hashString.charAt(i) * Math.pow(256, i);

        hash = hash % size;                             //Get a result within the array

        return (int) hash;
    }

    private boolean isGettingFull()     //Check if the array is half full
    {
        return numCars >= (size/2);
    }

    private void resize()              //Resize the hash array if it is getting full
    {
        Car[] temp = new Car[numCars*2+1];          //Create a temp array twice the size of the original (+1 so it's odd)
        for(int i = 0; i < numCars; i++)
            temp[i] = VINs[i];                        //Repopulate the array with cars
        VINs = temp;                                  //Transfer the contents back to hash table
    }

    public String toString()
    {
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            if(VINs[i] != null)
                temp.append("Index "+i+": "+VINs[i]+"\n");
        }
        return temp.toString();
    }
}
