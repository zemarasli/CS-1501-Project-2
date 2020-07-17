import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.util.Scanner;

public class CarTracker
{

  public static void main(String[] args) throws Exception
  {
    FileReader carsDict = new FileReader("cars.txt");
    BufferedReader brRead = new BufferedReader(carsDict);
    Scanner input = new Scanner(System.in);
    CarDealer cars = new CarDealer();

    String line = brRead.readLine();
    line = brRead.readLine();
    while(line != null)
    {
      //# VIN:Make:Model:Price:Mileage:Color
      String[] carInfo = line.split(":");
      String VIN = carInfo[0].toUpperCase();
      String make = carInfo[1].toLowerCase();
      String model = carInfo[2].toLowerCase();
      int price = Integer.parseInt(carInfo[3]);
      int mileage = Integer.parseInt(carInfo[4]);
      String color = carInfo[5].toLowerCase();

      cars.addCar(VIN, make, model, price, mileage, color);
      line = brRead.readLine();
    }
    brRead.close();
    //System.out.println("cars.txt successfully added!");

    boolean userInput = true;
    printMainMenu();

    while(userInput == true)
    {
        int userIn = input.nextInt();
        switch(userIn)
        {
          case 1:
            addCarFromMenu(cars);
            break;
          case 2:
            updateCarFromMenu(cars);
            break;
          case 3:
            deleteCarFromMenu(cars);
            break;
          case 4:
            retrieveCarFromMenu(cars);
            break;
          case 5:
            retrieveGlobalMin(cars, true);
            break;
          case 6:
            retrieveGlobalMin(cars, false);
            break;
          case 7:
            retrieveLowestPriceMM(cars);
            break;
          case 8:
            retrieveLowestMileageMM(cars);
            break;
          case 9:
            displayMMCarsFromMenu(cars);
            break;
          case 10:
            exitProgram();
            break;
          default:
            System.out.println("Not a valid entry; try again");
        }
        printMainMenu();
    }

  }

  public static void printMainMenu()
  {
    System.out.print("\nMAIN MENU\n(1) Add a car \n(2) Update a car \n(3) Remove a car " +
    "\n(4) Retrieve a car \n(5) Retrieve the lowest price car \n(6) Retrieve the lowest mileage car \n(7) Retrieve the lowest price car by make and model" +
    "\n(8) Retrieve the lowest mileage car by make and model \n(9) Display cars by specific make and model \n(10) Exit program " +
     "\nEnter corresponding number: ");
  }

  public static void addCarFromMenu(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.print("\nADD CAR");

    System.out.print("Enter VIN: ");
    String vin = input.nextLine().toUpperCase();

    System.out.print("Enter make: ");
    String make = input.nextLine().toLowerCase();

    System.out.print("Enter model: " );
    String model = input.nextLine().toLowerCase();

    System.out.print("Enter price: ");
    int price = input.nextInt();

    System.out.print("Enter mileage: ");
    int mileage = input.nextInt();
    input.nextLine();

    System.out.print("Enter color: ");
    String color = input.nextLine().toLowerCase();

    cars.addCar(vin, make, model, price, mileage, color);
    System.out.println("\nCar successfully added!");
  }

  public static void deleteCarFromMenu(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.println("\nDELETE CAR");

    System.out.print("Enter VIN of car to be deleted: ");
    String vin = input.nextLine().toUpperCase();
    System.out.println();
    cars.removeCar(vin);
  }

  public static void updateCarFromMenu(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.println("\nUPDATE A CAR");
    System.out.print("Enter VIN of car to be updated: ");
    String vin = input.nextLine().toUpperCase();


    System.out.print("Select to update (1) price (2) mileage (3) color: ");
    int choice = input.nextInt();
    input.nextLine(); //read in new line left over

    if(choice == 1)
    {
      System.out.print("Enter new car price: ");
      int price = input.nextInt();
      cars.modifyCarValue(vin, price, true);
    } else if(choice == 2)
    {
      System.out.print("Enter new car mileage: ");
      int mileage = input.nextInt();
      cars.modifyCarValue(vin, mileage, false);
    }
    else if(choice == 3)
    {
      System.out.print("Enter new car color: ");
      String color = input.nextLine().toLowerCase();
      cars.modifyCarColor(vin, color);
    }
    else
      System.out.print("\nInvalid choice");

    System.out.println("Car updated successfully");
  }

  public static void retrieveCarFromMenu(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.println("\nRETRIEVE CAR");

    System.out.print("Enter VIN of car to be retrieved: ");
    String vin = input.nextLine().toUpperCase();

    cars.printCar(vin);

  }

  public static void retrieveLowestPriceMM(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.println("\nRETRIEVE LOWEST PRICE CAR FOR A MAKE AND MODEL");

    System.out.print("Enter make: ");
    String make = input.nextLine().toLowerCase();

    System.out.print("\nEnter model: ");
    String model = input.nextLine().toLowerCase();
    System.out.println();
    cars.printMinCarforMM(make, model, true);
  }

  public static void retrieveLowestMileageMM(CarDealer cars)
  {
    Scanner input = new Scanner(System.in);
    System.out.println("\nRETRIEVE LOWEST MILEAGE CAR FOR A MAKE AND MODEL");

    System.out.print("\nEnter make: ");
    String make = input.nextLine().toLowerCase();

    System.out.print("\nEnter model: ");
    String model = input.nextLine().toLowerCase();
    System.out.println();
    cars.printMinCarforMM(make, model, false);
  }

  public static void retrieveGlobalMin(CarDealer cars, boolean isPrice)
  {
    Scanner input = new Scanner(System.in);
    if(isPrice == true)
    {
      System.out.print("\nRETRIEVE LOWEST PRICE CAR");
      System.out.println();
      cars.printGlobalMinValue(true);
    }else
    {
      System.out.print("\nRETRIEVE LOWEST MILEAGE CAR");
      System.out.println();
      cars.printGlobalMinValue(false);
    }
  }

  public static void displayMMCarsFromMenu(CarDealer cars)
  {
      Scanner input = new Scanner(System.in);
      System.out.println("\nLIST CARS");
      System.out.print("Enter make: ");
      String make = input.nextLine().toLowerCase();
      System.out.print("Enter model: ");
      String model = input.nextLine().toLowerCase();

      System.out.print("Choose to list cars either by (1) Price or (2) Mileage: ");
      int choice = input.nextInt();
      System.out.println();
      if(choice == 1) cars.printMMList(make, model, true);
      else if(choice == 2) cars.printMMList(make, model, false);
      else System.out.println("\nInvalid entry");
  }

  public static void exitProgram()
  {
    System.out.println("Goodbye");
    System.exit(0);
  }
}
