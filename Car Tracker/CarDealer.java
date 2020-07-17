import java.io.*;
import java.util.*;


public class CarDealer
{
  TreeMap<String, IndexableCar> inventory; //logN for containsKey, get, put and remove operations.
  ArrayList<MakeAndModel> mmList;
  Car globalMinPriceCar;
  Car globalMinMileageCar;

  public CarDealer()
  {
    inventory = new TreeMap<String, IndexableCar>(); //my auxillary data structure that stores indices of each car in PQ
    mmList = new ArrayList<MakeAndModel>();
    globalMinPriceCar = null;
    globalMinMileageCar = null;
  }

  public void addCar(String vin, String make, String model, int price, int mil, String color)
  {
    Car c = new Car(vin, make, model, price, mil, color);
    IndexableCar icar = new IndexableCar(c);

    if(inventory.get(vin) != null) return; //duplicate key
    inventory.put(vin, icar);

    //determining initial global mins
    if(inventory.size() == 1)
    {
      globalMinPriceCar = c;
      globalMinMileageCar = c;
    } else
    {
      if(price < globalMinPriceCar.getPrice() ) globalMinPriceCar = c;
      if(mil < globalMinMileageCar.getMileage() ) globalMinMileageCar = c;
    }

    //add car into respective MM list
    MakeAndModel mm = null;
    int mmIndex = -1;
    for(int i = 0; i < mmList.size(); i++) //iterate through mmList to see if there's a make&model match
    {
      MakeAndModel m = mmList.get(i);
      if(m.getMake().equals(make) && m.getModel().equals(model))
      {
        mm = m;
        mmIndex = i;
        break;
      }
    }
    if(mm == null)
    {
      mm = new MakeAndModel(make, model);
      mmList.add(mm);
      mmIndex = mmList.size() - 1;
    }

    mm.getPricePQ().addCar(icar); //add to pricePQ
    mm.getMileagePQ().addCar(icar); //add to mileagePQ
    icar.setMMListIndex(mmIndex); //update mmlist index
  }

  public void removeCar(String vin)
  {
    IndexableCar icar = inventory.get(vin);
    if (icar == null)
    {
      System.out.println("Car not found in inventory; try another VIN");
      return;
    }
    MakeAndModel mm = mmList.get(icar.getMMListIndex() );
    mm.getPricePQ().deleteCar(icar);
    mm.getMileagePQ().deleteCar(icar);
    inventory.remove(vin);

    //if the car that is deleted is a global min, need to find the new global min
    if(icar.getCar() == globalMinPriceCar) globalMinPriceCar = getGlobalMinValue(true);
    else if(icar.getCar() == globalMinMileageCar) globalMinMileageCar = getGlobalMinValue(false);

    System.out.println("Car successfully deleted.");
  }

  public void modifyCarValue(String vin, int v, boolean isPrice)
  {
    IndexableCar iCar = inventory.get(vin);
    if (iCar == null)
    {
      System.out.println("No such car exists");
      return;
    }

    MakeAndModel mm = mmList.get(iCar.getMMListIndex() );
    if(isPrice == true)
      mm.getPricePQ().updatePrice(iCar, v);
    else
      mm.getMileagePQ().updateMileage(iCar, v);

    //check with global min
    if(v < globalMinPriceCar.getPrice() ) globalMinPriceCar = iCar.getCar();
    else if( v < globalMinMileageCar.getMileage() ) globalMinMileageCar = iCar.getCar();
    else if(iCar.getCar() == globalMinPriceCar) globalMinPriceCar = getGlobalMinValue(true);
    else if(iCar.getCar() == globalMinMileageCar) globalMinMileageCar = getGlobalMinValue(false);
  }

  public void modifyCarColor(String vin, String newColor)
  {
    IndexableCar iCar = inventory.get(vin);
    if(iCar == null)
    {
      System.out.println("No such car exists");
      return;
    }
    iCar.getCar().setColor(newColor);
  }

  public void printGlobalMinValue(boolean isPrice) //prints global min (price or mileage)
  {
    Car globalMin = getGlobalMinValue(isPrice);
    System.out.println("\t" + globalMin);
  }

  private Car getGlobalMinValue(boolean isPrice) //iterates through mmList and compare's each MM minimum to find global min
  {
    int currMin = -1;
    Car globalMin = null;
    for(int i = 0; i < mmList.size(); i++)
    {
      MakeAndModel mm = mmList.get(i);
      Car currCar = getMinMMCar(mm, isPrice);
      int val = isPrice ? currCar.getPrice() : currCar.getMileage();
      if(val < currMin || currMin == -1)
      {
        currMin = val;
        globalMin = currCar;
      }
    }
    return globalMin;
  }

  public void printMinCarforMM(String make, String model, boolean isPrice) //if mm exists, and prints min car
  {
    MakeAndModel mm = null;
    for(int i = 0; i < mmList.size(); i++) //iterate to find a match
    {
      MakeAndModel m = mmList.get(i);
      if(m.getMake().equals(make) && m.getModel().equals(model))
      {
        mm = m;
        break;
      }
    }
    if(mm == null)
      System.out.println("\t A " + make + " " + model + " does not exist in your inventory");
    else
    {
      Car minCar = getMinMMCar(mm, isPrice);
      System.out.println("\t" + minCar);
    }
  }

  private Car getMinMMCar(MakeAndModel mm, boolean isPrice) //returns min car for specific m+m
  {
    if(isPrice == true)
      return mm.getPricePQ().retrieveMin();
    else
      return mm.getMileagePQ().retrieveMin();
  }

  public void printCar(String vin)
  {
    IndexableCar c = inventory.get(vin);
    if(c == null) System.out.println("\tCar does not exist");
    else System.out.println("\n\t" + c.getCar() );
  }

  public void printMMList(String make, String model, boolean isPrice)
  {
    MakeAndModel mm = null;
    System.out.println("searching for " + make + " " + model + " list");
    for(int i = 0; i < mmList.size(); i++) //find correct mmList
    {
      MakeAndModel m = mmList.get(i);
      if(m.getMake().equals(make) && m.getModel().equals(model))
      {
        mm = m;
        break;
      }
    }

    if(mm == null)
      System.out.println("\t" + make + " " + model + " cars do not exist in your inventory");
    else
    {
      if(isPrice == true) mm.printPricePQ();
      else mm.printMileagePQ();
    }
  }

}
