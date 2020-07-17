import java.io.*;
import java.util.*;

public class IndexableCar {
  private Car car;
  private int pricePQIndex;  // price pq index within makemodel
  private int mileagePQIndex;  // milegae pq index within makemodel
  private int makeModelListIndex;  // makemodel index --> set once and then don't change

  public IndexableCar (Car c)
  {
    car = c;
    pricePQIndex = -1;  // -1 means not valid; here and below
    mileagePQIndex = -1;
    makeModelListIndex = -1;
  }

  public IndexableCar (Car c, int priceIndex, int mileIndex)
  {
    car = c;
    pricePQIndex = priceIndex;
    mileagePQIndex = mileIndex;
    makeModelListIndex = -1;
  }

  public Car getCar()
  {
    return car;
  }

  public int getPricePQIndex()
  {
    return pricePQIndex;
  }

  public int getMileagePQIndex()
  {
    return mileagePQIndex;
  }

  public int getMMListIndex()
  {
    return makeModelListIndex;
  }

  public void setPricePQIndex(int newIndex)
  {
     pricePQIndex = newIndex;
  }

  public void setMileagePQIndex(int newIndex)
  {
     mileagePQIndex = newIndex;
  }

  public void setMMListIndex(int newIndex)
  {
     makeModelListIndex = newIndex;
  }
  public String toString()
  {
    return getCar().toString() + "\n\t\t" + "MMIdx: " + getMMListIndex() + "\tpriceIdx: " + getPricePQIndex() + "\t milIdx: " + getMileagePQIndex();
  }
}
