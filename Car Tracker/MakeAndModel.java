import java.io.*;
import java.util.*;

public class MakeAndModel
{
  private String make;
  private String model;
  private CarPQ pricePQ;
  private CarPQ mileagePQ;

  public MakeAndModel(String ma, String mo)
  {
    make = ma;
    model = mo;
    pricePQ = new CarPQ(true);
    mileagePQ = new CarPQ(false);
  }

  public String getMake()
  {
    return make;
  }

  public String getModel()
  {
    return model;
  }

  public CarPQ getPricePQ()
  {
    return pricePQ;
  }

  public CarPQ getMileagePQ()
  {
    return mileagePQ;
  }

  public void printPricePQ()
  {
    getPricePQ().printPQ();
  }

  public void printMileagePQ()
  {
    getMileagePQ().printPQ();
  }
}
