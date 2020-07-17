I have 6 relevant files:

Car.java
- This file holds my Car class and getters/setters for its
  members(vin, make, model, price, mileage, color)

IndexableCar.java
- This file holds my IndexableCar class. This class' members are Car,
  price priority index, mileage priority queue index, and the index of which
  MakeAndModel list the car belongs to.

CarPQ.java
- This file is my priority queue, and deals with all PQ add, delete, and
  retrieval operations. I chose to use an ArrayList as my underlying priority
  queue data structure, as its get, set, and size functions run in constant time
  and it is resizable. I used the parent, leftChild, rightChild index pattern
  given in lecture to ensure logN addition, removal, and update of cars.
- The PQ holds IndexableCars, instead of just my Car class, so that I can easily
  access and change the indices of a Car if I need to change its position within
  the PQ.
- This file also holds methods of addCar, deleteCar, heapify, modifyPrice/Mileage
  and other helper methods. I pass around a boolean value to specify whether the
  PQ I'm dealing with is for price or for mileage.

MakeAndModel.java
- This file holds my MakeAndModel class. Each time a unique make-model is added,
  a unique pricePQ and mileagePQ is created for that make-model.

CarDealer.java
- This file holds my CarDealer class. A CarDealer contains:
        - an inventory of all cars. This inventory serves as my auxillary data
        structure that holds the index keys of each car, thus making my PQ
        indexable. I decided to use a TreeMap<VIN, IndexableCar> because
        TreeMap uses a Red-Black tree data structure and ensures logN time for
        containsKey, get, put and remove operations.

        - an ArrayList<MakeAndModel> that stores all the unique MakeAndModel classes
        I decided to use an ArrayList, instead of something like a HashMap,
        because I needed a data structure that I could iterate through.

        - globalMinPrice and globalMinMileage Car members. Instead of creating
        a price and mileage PQ for all cars, or iterating through the entire
        ArrayList of MakeAndModels, and pulling each minimum car value to find the
        global minimum (which would be O(n) time) each time I wanted to retrieve
        a global minimum, I decided to just keep two variables that stored those
        global minimum cars. I kept track of these global minimum cars when
        adding, deleting, or updating a car. When adding or updating a car, if
        the added/updated car's value is less than that of the current
        minimum's, I set the new/updated car as the new global minimum. If the user
        updated the current global minimum's value to a larger value (thus no longer
        making it the current global minimum) I have to iterate and access each
        MakeAndModel's minimum to find the new global minimum. I have to do a
        similar operation if the user specify's to delete a car that is the globalMin.
        I found that this optimization allows me to save memory (not create and maintain
        a global price and mileage priority queue) and time (have to iterate across
        the MakeAndModel ArrayList less).

CarTracker.java
- My main class. This deals creates a CarDealer variable, reads in cars.txt, and
  reads in inputs from the user. 
