import java.io.*;
import java.util.*;
import java.util.Scanner;

public class ac_test {
  public static void main (String[] args) throws Exception
  {
    File fileHistory = new File("/Users/zeynepmarasli/Downloads/CS1501/P1/userHistory.txt");

    String dictFile = "dictionary.txt";
    String userFile = "userHistory.txt";
    MyDLB<Character> dictionaryDLB = new MyDLB<Character>(dictFile);
    MyDLB<Character> userHistoryDLB = new MyDLB<Character>(userFile);

    String userWord = "";
    String userPrefix = "";
    boolean firstSearch = true;
    boolean newSearch = false;
    ArrayList<String> matchesList = new ArrayList<String>();
    ArrayList<Double> searchTimes = new ArrayList<Double>();

    Scanner scanner = new Scanner(System.in);
    String input = "";

    do {
      if(firstSearch) firstSearch = firstSearch();
      if(scanner.hasNextInt() )
      {
        int index = scanner.nextInt();
        userHistoryDLB = intInput(index, matchesList, userHistoryDLB, userFile);
        newSearch = newSearch();
        userPrefix = "";
      }

      input = scanner.next();
      if(input.equals("$"))
      {
        userWord = userPrefix;
        userHistoryDLB = updateUserHistory(userWord, userFile, userHistoryDLB);
        System.out.println("\n\n  WORD ADDED:  " + userWord + "\n");
        newSearch = true;
      }
      else if(input.equals("!")) exitProgram(searchTimes);
      else if(input.equals("*")) newSearch = true;
      else //update matchesList
      {
        userPrefix += input;
        double startTime = System.nanoTime();
        matchesList = userHistoryDLB.prefixRecs(userPrefix, new ArrayList<String>() );
        if(matchesList.size() < 5) matchesList = dictionaryDLB.prefixRecs(userPrefix, matchesList);
        double finishTime = System.nanoTime();
        double actualTime = (finishTime - startTime) / 1000000000.0;
        printList(matchesList, actualTime);
        searchTimes.add(actualTime);
      }
      if(newSearch)
      {
        newSearch = newSearch();
        userPrefix = "";
      }
      else System.out.print("\nEnter the next character: ");

    } while (scanner.hasNext() );
    exitProgram(searchTimes);
  }

  //outputs first character search && sets firstSearch to false
  public static boolean firstSearch()
  {
    System.out.print("Enter your first character: ");
    return false;
  }

  public static boolean newSearch()
  {
    System.out.print("\nEnter first character of next word: ");
    return false;
  }

  public static MyDLB<Character> intInput(int index, ArrayList<String> list, MyDLB<Character> dlb, String file) throws IOException
  {
    String word = list.get(index - 1);
    System.out.println("\n\n  WORD COMPLETED:  " + word + "\n");
    return updateUserHistory(word, file, dlb);

  }

  public static void printList(ArrayList<String> list, double time)
  {
    int outCount = 1;
    System.out.format("\n(%.6fs)", time);
    System.out.println("\nPredictions: ");
    if(list.isEmpty() )
    {
      System.out.println("\tNo predictions found");
      return;
    }
    for(int i = 0; i < list.size(); i++)
    {
      System.out.format("(" + outCount + ") " + list.get(i) + "   ");
      outCount++;
    }
    System.out.println();
  }

  //user input = $
  public static MyDLB<Character> updateUserHistory(String word, String file, MyDLB<Character> dlb) throws IOException
  {
    dlb.insertWord(word);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    writer.write(word);
    writer.write("\n");
    writer.close();
    return dlb;
  }

  //user input = !
  public static void exitProgram(ArrayList<Double> times)
  {
    double total = 0.00;
    double avg = 0.00;
    for(double time: times)
    {
      total += time;
    }
    avg = total / times.size();
    System.out.format("\nAverage time: (%.6fs)", avg);
    System.out.println("\nBye!");
    System.exit(0);
  }


}
