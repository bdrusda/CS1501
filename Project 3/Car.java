class Car
{
    private String VIN;
    private String make;
    private String model;
    private int price;
    private int mileage;
    private String color;

    private int VINIndex;           //Index in the master hash table of all VINs
    private int priceIndex;         //Index in the price priority queue
    private int mileageIndex;       //Index in the mileage priority queue
    private int priceMMIndex;       //Index for the price make model PQ (inside hash table)
    private int mileageMMIndex;     //Index for the mileage make model PQ (inside hash table)
    private int priceMMPQIndex;     //Index in the price make model PQ (inside PQ)
    private int mileageMMPQIndex;   //Index in the mileage make model PQ (inside PQ)

    public Car()
    {

    }

    public Car(String VIN, String make, String model, int price, int mileage, String color)
    {
        this.VIN = VIN;
        this.make = make;
        this.model = model;
        this.price = price;
        this.mileage = mileage;
        this.color = color;
    }

    public String getVIN()
    {
        return VIN;
    }

    public String getMake()
    {
        return make;
    }

    public String getModel()
    {
        return model;
    }

    public int getPrice()
    {
        return price;
    }

    public int getMileage()
    {
        return mileage;
    }

    public String getColor()
    {
        return color;
    }

    public void setVIN(String VIN)
    {
        this.VIN = VIN;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public void setMileage(int mileage)
    {
        this.mileage = mileage;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public String toString()
    {
        return ("VIN #: "+VIN+"\tMake: "+make+"\tModel: "+model+"\tPrice: $"+price+"\tMileage: "+mileage+"\tColor: "+color);
    }

    /*PQ Indexes*/
    public int getVINIndex()
    {
        return VINIndex;
    }

    public int getMileageIndex()
    {
        return mileageIndex;
    }

    public int getPriceIndex()
    {
        return priceIndex;
    }

    public int getMileageMMIndex()
    {
        return mileageMMIndex;
    }

    public int getPriceMMIndex()
    {
        return priceMMIndex;
    }

    public int getMileageMMPQIndex()
    {
        return mileageMMPQIndex;
    }

    public int getPriceMMPQIndex()
    {
        return priceMMPQIndex;
    }

    public void setVINIndex(int index)
    {
        VINIndex = index;
    }

    public void setMileageIndex(int index)
    {
        mileageIndex = index;
    }

    public void setPriceIndex(int index)
    {
        priceIndex = index;
    }

    public void setMileageMMIndex(int index)
    {
        mileageMMIndex = index;
    }

    public void setPriceMMIndex(int index)
    {
        priceMMIndex = index;
    }

    public void setMileageMMPQIndex(int index)
    {
        mileageMMPQIndex = index;
    }

    public void setPriceMMPQIndex(int index)
    {
        priceMMPQIndex = index;
    }
}
