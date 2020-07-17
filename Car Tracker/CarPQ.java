import java.io.*;
import java.util.*;
//don't create new copies of indexable cars !!!!!!!

//the priority queue has an underlying array structure and holds the cars
public class CarPQ 
{
  private boolean priceOrMileage; //if true => PQ is price; if false => PQ is mileage
  private ArrayList<IndexableCar> CarPQ; //underlying array structure that holds the cars

  public CarPQ(boolean pOrM) //constructor
  {
    CarPQ = new ArrayList<IndexableCar>();
    priceOrMileage = pOrM;
  }

  public void addCar(IndexableCar newCar) //add a car
  {
    CarPQ.add(CarPQ.size() , newCar);

    if(CarPQ.size() == 1) //added first element to PQ; nothing to reorder
    {
      updateIndex(newCar, CarPQ.size() - 1);
      return;
    }
    updateIndex(newCar, CarPQ.size() - 1);
    heapify(newCar);
  }

  public void deleteCar(IndexableCar toBeDeletedCar)
  {
    if(toBeDeletedCar == null) return;

    int lastIndex = CarPQ.size() - 1;
    IndexableCar lastIndexCar = CarPQ.get(lastIndex);

    if(lastIndex != getIndex(toBeDeletedCar))
    {
      swapPlaces(toBeDeletedCar, lastIndexCar);
      CarPQ.remove(lastIndex);
      heapify(lastIndexCar);
    } else CarPQ.remove(lastIndex); //if toBeDeletedCar == last car in PQ
  }

  public void updatePrice(IndexableCar car, int newPrice)
  {
    if(car == null) return;
    car.getCar().setPrice(newPrice);
    heapify(car);
  }

  public void updateMileage(IndexableCar car, int newMileage)
  {
    if(car == null) return;
    car.getCar().setMileage(newMileage);
    heapify(car);
  }

  public Car retrieveMin()
  {
    if (CarPQ.isEmpty() ) return null;
    return (CarPQ.get(0)).getCar();
  }

  private void heapify(IndexableCar car)
  {
    int currIndex = getIndex(car);
    int currValue = getComparatorValue(car);

    if(!validateIndex(currIndex) ) return; //illegal index

    int parentIndex = parentIndex(currIndex);
    int leftChildIndex = leftChildIndex(currIndex);
    int rightChildIndex = rightChildIndex(currIndex);

    if(currIndex != 0) //if has a parent
    {
      IndexableCar pCar = CarPQ.get(parentIndex);
      int pValue = getComparatorValue(pCar);

      if(currValue < pValue)  //if car needs to switch with parent
      {
        swapPlaces(car, pCar);
        heapify(car);
      }
    }

    if(validateIndex(leftChildIndex) || validateIndex(rightChildIndex)) //if car has children
    {
      IndexableCar leftChildCar = validateIndex(leftChildIndex) ? CarPQ.get(leftChildIndex) : null;
      int leftChildValue = (leftChildCar != null) ? getComparatorValue(leftChildCar) : currValue + 1; //if a left child exists, set it to its value; if not set it to > currValue

      IndexableCar rightChildCar = validateIndex(rightChildIndex) ? CarPQ.get(rightChildIndex) : null;
      int rightChildValue = (rightChildCar != null) ? getComparatorValue(rightChildCar) : currValue + 1;

      if(leftChildValue < rightChildValue && leftChildValue < currValue) //switch with left
      {
        swapPlaces(car, leftChildCar);
        heapify(car);
      } else if (rightChildValue <= leftChildValue && rightChildValue < currValue) //switch with right
      {
        swapPlaces(car, rightChildCar);
        heapify(car);
      }
    }
  }

  private int parentIndex(int i)
  {
    return (i - 1) / 2;
  }

  private int leftChildIndex(int i)
  {
    return (2 * i) + 1;
  }

  private int rightChildIndex(int i)
  {
    return (2 * i) + 2;
  }

  private void swapPlaces(IndexableCar car1, IndexableCar car2) //swaps car1 and car2 spots & updates indices of the cars
  {
    int index1 = getIndex(car1);
    int index2 = getIndex(car2);

    IndexableCar temp = car1;
    CarPQ.set(index1, car2);
    updateIndex(car2, index1);

    CarPQ.set(index2, temp);
    updateIndex(car1, index2); //or should be updateIndex(temp, index2) ??
  }

  private int getComparatorValue(IndexableCar car) //returns price or mileage of that car
  {
    if(priceOrMileage == true) return car.getCar().getPrice();
    else return car.getCar().getMileage();
  }

  private int getIndex(IndexableCar car) //returns index of price or mileage priority queue
  {
    if(priceOrMileage == true) return car.getPricePQIndex();
    else return car.getMileagePQIndex();
  }

  private void updateIndex(IndexableCar car, int newIndex) //updates index stored in IndexableCar
  {
    if(priceOrMileage == true) car.setPricePQIndex(newIndex);
    else car.setMileagePQIndex(newIndex);
  }

  private boolean validateIndex(int i)
  {
    return (i >= 0 && i < CarPQ.size() );
  }

  public void printPQ() //prints priority queue
  {
    for(int i = 0; i < CarPQ.size(); i++)
    {
      IndexableCar iCar = CarPQ.get(i);
      System.out.println("(" + i + ") " + iCar.getCar() );
    }
    System.out.println();
  }

}
