import java.io.*;
import java.util.*;

public class Car
{
  private String VIN;
  private String color;
  private String make;
  private String model;
  private int mileage;
  private int price;

  //# VIN:Make:Model:Price:Mileage:Color
  public Car (String v, String ma, String mo, int p, int mil, String c)
  {
    VIN = v;
    make = ma;
    model = mo;
    price = p;
    mileage = mil;
    color = c;
  }

  public String getVin()
  {
    return VIN;
  }

  public String getColor()
  {
    return color;
  }

  public String getMake()
  {
    return make;
  }

  public String getModel()
  {
    return model;
  }

  public int getMileage()
  {
    return mileage;
  }

  public int getPrice()
  {
    return price;
  }

/*
  public void setVin(String v)
  {
    this.VIN = v;
  } */

  public void setColor(String c)
  {
    this.color = c;
  }

/*
  public void setMake(String m)
  {
    this.make = m;
  }

  public void setModel(String m)
  {
    this.model = m;
  } */

  public void setMileage(int m)
  {
    this.mileage = m;
  }

  public void setPrice(int p)
  {
    this.price = p;
  }

  public String toString() {
    return getVin() + "\t" + getMake() + ", " + getModel() + "\t$" + getPrice() + " \t" + getMileage() + " miles\t" + getColor();
  }

  public void printCar()
  {
    System.out.println(this);
  }
}
