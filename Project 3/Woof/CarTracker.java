import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class CarTracker
{
    static CarPQ db = new CarPQ();                     //Database of cars
    static Scanner in;                                 //Input scanner
    public static void main(String[] args)
    {
        File existing_cars = new File("cars.txt");  //File with car info

        try
        {
            in = new Scanner(existing_cars);        //Direct the scanner to the cars file
            in.nextLine();                          //Remove the initial header line from cars.txt

            while(in.hasNextLine())                 //While there are more cars
            {
                String[] info = in.nextLine().split(":");   //Get the car information, delimited by colons
                Car temp = new Car(info[0], info[1], info[2], Integer.parseInt(info[3]), Integer.parseInt(info[4]), info[5]);   //Create the car object
                addCar(temp);                               //Add the car to the database
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();                    //If the file isn't there, print the stack trace
        }

        in = new Scanner(System.in);                //Redirect the input to standard input
        int option;

/*DEBUG to check the swim is working properly*/            System.out.println(db);

        System.out.println("Welcome to CarTracker!");   //Welcome the user

        while(true)                                     //Run forever
        {
            System.out.println("Would you like to:\n"+
                                "1) Add a car\n"+
                                "2) Update a car\n"+
                                "3) Remove a car\n"+
                                "4) Retrieve the lowest price car\n"+
                                "5) Retrieve the lowest mileage car\n"+
                                "6) Retrieve the lowest price car by make and model\n"+
                                "7) Retrieve the lowest mileage car by make and model\n");

            option = in.nextInt(); in.nextLine();               //Get the user's decision, then clear the nextInt buffer

            if(option == 1)         //Add car
            {
                Car temp = new Car();
                System.out.print("Enter the Car's VIN: ");
                temp.setVIN(in.nextLine());                     //Get the VIN
                System.out.print("Enter the Car's Make: ");
                temp.setMake(in.nextLine());                    //Get the Make
                System.out.print("Enter the Car's Model: ");
                temp.setModel(in.nextLine());                   //Get the Model
                System.out.print("Enter the Car's Price: ");
                temp.setPrice(in.nextInt());                    //Get the price
                System.out.print("Enter the Car's Mileage: ");
                temp.setMileage(in.nextInt());  in.nextLine();  //Get the mileage, then clear the nextInt buffer
                System.out.print("Enter the Car's Color: ");
                temp.setColor(in.nextLine());                   //Get the color

        //Put the car in the symbol table
        //Put the car in each of the four heaps
                addCar(temp);
            }
            else if(option == 2)    //Update a car
            {
                System.out.println("What is the VIN of the car you want to update?");
                String tVIN = in.nextLine();
                updateCar(tVIN);
            }
            else if(option == 3)    //Remove a car
            {
                System.out.print("Enter the VIN of the car you wish to remove: ");
                String tVIN = in.nextLine();
                removeCar(tVIN);
            }
            else if(option == 4)    //Retrieve lowest price
            {
                System.out.println(getCarByPrice());
            }
            else if(option == 5)    //Retrieve lowest mileage
            {
                System.out.println(getCarByMileage());
            }
            else if(option == 6)    //Retrieve lowest price by make and model
            {
                System.out.print("Enter the desired make: ");
                String tMake = in.nextLine();
                System.out.print("Enter the desired model: ");
                String tModel = in.nextLine();

                System.out.println(getCarByPriceMM(tMake, tModel));
            }
            else if(option == 7)    //Retrieve lowest mileage by make and model
            {
                System.out.print("Enter the desired make: ");
                String tMake = in.nextLine();
                System.out.print("Enter the desired model: ");
                String tModel = in.nextLine();

                System.out.println(getCarByMileageMM(tMake, tModel));
            }
            else
                System.out.println("Enter a valid option 1-7");

        }
    }

    public static void addCar(Car temp)
    {
        db.add(temp);
    }

    public static void updateCar(String VIN)
    {
        //We need to get the car somehow
        System.out.println("Would you like to update the:\n"+
                            "1) Price\n"+
                            "2) Mileage\n"+
                            "3) Color\n");
        int option = in.nextInt();   in.nextLine();  //get the user's selection and clear the nextInt buffer

        if(option == 1)
        {
            System.out.print("Enter new price: ");
        }
        else if(option == 2)
        {
            System.out.print("Enter new mileage: ");
        }
        else if(option == 3)
        {
            System.out.print("Enter new color: ");
        }
        else
        {
            System.out.println("Invalid option");
            updateCar(VIN);
        }
    }

    public static void removeCar(String VIN)
    {
        db.remove();
    }

    public static Car getCarByPrice()
    {
        return null;
    }

    public static Car getCarByMileage()
    {
        return db.getMin();
    }

    public static Car getCarByPriceMM(String make, String model)
    {
        return null;
    }

    public static Car getCarByMileageMM(String make, String model)
    {
        return null;
    }
}
