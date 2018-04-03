class Car
{
    String VIN;
    String make;
    String model;
    int price;
    int mileage;
    String color;

    int priceIndex;
    int mileageIndex;

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
}
