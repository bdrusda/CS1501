import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.File;
import java.io.FileNotFoundException;

public class CarTracker
{
    static VINTable master = new VINTable();                    //Hash table of all cars by VIN
    static CarPQ mileage = new CarPQ('m', false);               //Database of cars by mileage
    static CarPQ price = new CarPQ('p', false);                 //Database of cars by price
    static MakeModelTable mileageMM = new MakeModelTable('m');  //Hash table containing priority queues of cars by mileage by make and model
    static MakeModelTable priceMM = new MakeModelTable('p');    //Hash table containing priority queues of cars by price by make and model


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
        int option = -1;

        System.out.println("Welcome to CarTracker!");   //Welcome the user

        while(true)                                     //Run forever
        {
            System.out.println("Would you like to:\n"+
                                "\t1) Add a car\n"+
                                "\t2) Update a car\n"+
                                "\t3) Remove a car\n"+
                                "\t4) Retrieve the lowest price car\n"+
                                "\t5) Retrieve the lowest mileage car\n"+
                                "\t6) Retrieve the lowest price car by make and model\n"+
                                "\t7) Retrieve the lowest mileage car by make and model\n"+
                                "\tEnter a non-numeric character to quit\n");

            try
            {
                option = in.nextInt(); in.nextLine();               //Get the user's decision, then clear the nextInt buffer
            }
            catch(InputMismatchException i)
            {
                System.exit(0);                                     //If the user enters a non-numeric character, exit the program
            }

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

                if(master.lookup(temp.getVIN()) != null)        //Check if the VIN exists
                    System.out.println("\nA car with the provided VIN already exists\n");   //If so alert the user that it is a duplicate
                else
                    addCar(temp);                                                           //Otherwise, add the car
            }
            else if(option == 2)    //Update a car
            {
                System.out.println("What is the VIN of the car you want to update?");
                String tVIN = in.nextLine();                    //Get the VIN of the car to update
                updateCar(tVIN);                                //Update the car
            }
            else if(option == 3)    //Remove a car
            {
                System.out.print("Enter the VIN of the car you wish to remove: ");
                String tVIN = in.nextLine();                    //Get the VIN of the car to remove
                removeCar(tVIN);                                //Remove the car
            }
            else if(option == 4)    //Retrieve lowest price
            {
                Car result = getCarByPrice();                   //Get the lowest priced car
                if(result != null)                              //If the result is a car
                    System.out.println(result);                     //Print out the car info
                else
                    System.out.println("There are no cars in the database");    //Otherise, alert the user that there are no more cars
            }
            else if(option == 5)    //Retrieve lowest mileage
            {
                Car result = getCarByMileage();                 //Get the lowest mileage car
                if(result != null)                              //If the result is a car
                    System.out.println(result);                     //Print out the car info
                else
                    System.out.println("There are no cars in the database");    //Otherwise, alert the user that there are no more cars
            }
            else if(option == 6)    //Retrieve lowest price by make and model
            {
                System.out.print("Enter the desired make: ");
                String tMake = in.nextLine();                   //Get the make
                System.out.print("Enter the desired model: ");
                String tModel = in.nextLine();                  //Get the model

                Car result = getCarByPriceMM(tMake, tModel);    //Get the lowest price of that make and model
                if(result != null)                              //If the result is a car
                    System.out.println(result);                     //Print out the result
                else
                    System.out.println("\nNo cars found under the given make and model\n"); //Otherwise, alert the user that no cars were found under that make and model
            }
            else if(option == 7)    //Retrieve lowest mileage by make and model
            {
                System.out.print("Enter the desired make: ");
                String tMake = in.nextLine();                   //Get the make
                System.out.print("Enter the desired model: ");
                String tModel = in.nextLine();                  //Get the model

                Car result = getCarByMileageMM(tMake, tModel);  //Get the lowest mileage car of the make and model
                if(result != null)                              //If the result is a car
                    System.out.println(result);                     //Print out the result
                else
                    System.out.println("\nNo cars found under the given make and model\n"); //Otherwise, alert the user that no cars were found under that moake and model
            }
            else
                System.out.println("Enter a valid option 1-7"); //If the option was not 1-7, get a valid option
        }
    }

    private static void addCar(Car temp)
    {
        master.add(temp);       //VINTable
        mileage.add(temp);      //MileagePQ
        price.add(temp);        //PricePQ
        mileageMM.add(temp);    //MileageMMPQ
        priceMM.add(temp);      //PriceMMPQ
    }

    private static void updateCar(String VIN)
    {
        Car temp = master.lookup(VIN);  //Lookup the VIN in the VIN Table

        if(temp == null)                //If the car doesn't exist, notify the user
        {
            System.out.println("No car found under the given VIN: "+VIN);
            return;
        }
        else                            //If the car does exist, display its info
            System.out.println("You have selected: "+temp);

        System.out.println("Would you like to update the:\n"+
                            "\t1) Price\n"+
                            "\t2) Mileage\n"+
                            "\t3) Color\n");
        int option = in.nextInt();   in.nextLine(); //Get the user's selection and clear the nextInt buffer

        if(option == 1)                             //Price
        {
            System.out.print("Enter new price: ");
            temp.setPrice(in.nextInt());    in.nextLine();
            //Update the car
            int pI = temp.getPriceIndex();
            int pMMI = temp.getPriceMMIndex();
            int pMMPQI = temp.getPriceMMPQIndex();

            price.update(pI);                       //Update the car's position in the Price PQ
            priceMM.getPQ(pMMI).update(pMMPQI);     //Get the right MM PQ and update the car's position in the Price PQ

        }
        else if(option == 2)                        //Mileage
        {
            System.out.print("Enter new mileage: ");
            temp.setMileage(in.nextInt());  in.nextLine();  //Get the mileage and set it
            //Update the car
            int mI = temp.getMileageIndex();                //Get the mileage index
            int mMMI = temp.getMileageMMIndex();            //Get the index of the make model mileage PQ
            int mMMPQI = temp.getMileageMMPQIndex();        //Get the index in the make model PQ

            price.update(mI);                               //Update the car's position in the Mileage PQ
            priceMM.getPQ(mMMI).update(mMMPQI);             //Get the right MM PQ and update the car's position in the Mileage PQ
        }
        else if(option == 3)                        //Color
        {
            System.out.print("Enter new color: ");
            temp.setColor(in.nextLine());                   //Get and set the color
        }
        else
        {
            System.out.println("Invalid option");
            updateCar(VIN);                                 //Prompt the user again
        }
    }

    private static void removeCar(String VIN)
    {
        Car temp = master.lookup(VIN);

        if(temp == null)    //If the car doesn't exist, notify the user
        {
            System.out.println("No car found under the given VIN: "+VIN);
            return;
        }

        int pI = temp.getPriceIndex();          //Get the price index
        int pMMI = temp.getPriceMMIndex();      //Get the index of the price make model PQ
        int pMMPQI = temp.getPriceMMPQIndex();  //Get the index in the price make model PQ

        int mI = temp.getMileageIndex();        //Get the mileage index
        int mMMI = temp.getMileageMMIndex();    //Get the index of the mileage make model PQ
        int mMMPQI = temp.getMileageMMPQIndex();//Get the index in the mileage make model PQ

        int vI = temp.getVINIndex();            //Get the index of the car in the VIN table

        price.remove(pI);                       //Remove from price
        priceMM.getPQ(pMMI).remove(pMMPQI);     //Remove from price make model
        mileage.remove(mI);                     //Remove from mileage
        mileageMM.getPQ(mMMI).remove(mMMPQI);   //Remove from mileage make model
        master.remove(vI);                      //Remove from the VIN Table
    }

    private static Car getCarByPrice()
    {
        return price.getMin();                  //Return the minimum priced car
    }

    private static Car getCarByMileage()
    {
        return mileage.getMin();                //Return the minimum mileage car
    }

    private static Car getCarByPriceMM(String make, String model)
    {
        return priceMM.lookup(make, model);     //Return the minimum price car by make and model
    }

    private static Car getCarByMileageMM(String make, String model)
    {
        return mileageMM.lookup(make, model);   //Return the minimum mileage car by make and model
    }

    private static void debugToString()
    {
        System.out.println("\t\tVIN Table:\n"+master+"\n\n");
        System.out.println("\t\tMileage PQ:\n"+mileage+"\n\n");
        System.out.println("\t\tPrice PQ:\n"+price+"\n\n");
        System.out.println("\t\tMileage MM PQ's:\n"+mileageMM+"\n\n");
        System.out.println("\t\tPrice MM PQ's:\n"+priceMM+"\n\n");
    }
}
