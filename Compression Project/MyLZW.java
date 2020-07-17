/*************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - < input.txt   (compress)
 *  Execution:    java MyLZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *************************************************************************/
 import java.io.*;
 import java.util.*;

public class MyLZW {
    private static final int R = 256;                          // number of input chars
    private static final int startingL = 512;                 // number of codewords = 2^W
    private static final int startingW = 9;                  // codeword width
    private static final int maxL = 65536;                  // max # of codewords = 2^16
    private static final int maxW = 16;                    //codewords cannot exceed 16 bits
    private static final double MonitorRatioRate = 1.1;   // magic monitor ratio


    public static void compress(String mode) throws IOException
    {
        String input = BinaryStdIn.readString();

        //make default symbol table with 256 ASCII
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        int runningL = startingL;  //changes # of codeword
        int runningW = startingW; // changes codeword bit size

        double numBitsRead = 0.0;      //uncompressed data
        double numBitsWritten = 0.0;  //compressed data
        double currRatio = 0.0;      // numBitsRead / numBitsWritten
        double prevRatio = -1.0;  // -1 indicates unititliazed value

        // get mode and write to file as first byte
        char M = mode.charAt(0);
        if (M != 'n' && M!= 'r' && M != 'm') {
          System.err.println("Illegal mode: " + M);
          return;
        }
        BinaryStdOut.write(M); //write to compressed file so expansion can know which mode
        numBitsWritten += 8;

        while (input.length() > 0)
        {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), runningW);      // write s's encoding.
            int t = s.length();

            int cword = st.get(s);

            // update counts and ratios
            numBitsRead += t*8;
            numBitsWritten += runningW;

            if (t < input.length() && code < runningL)    // Add s to symbol table.
            {
                st.put(input.substring(0, t + 1), code++);
                currRatio = numBitsRead / numBitsWritten;  // compute ratios as we are adding new symbols
            }
            else if (t < input.length() && runningW < maxW && code == runningL) //increase runningW & add s to ST
            {
              runningW++;
              runningL *= 2;
              st.put(input.substring(0, t + 1), code++);
            }
            else if( t < input.length() && runningW == maxW && code == runningL) //codebook is full
            {
              if(M == 'r') //reset mode
              {
                System.err.println("\tReset mode");

                runningL = startingL;
                runningW = startingW;

                st = new TST<Integer>(); //reset TST
                for (int i = 0; i < R; i++)
                    st.put("" + (char) i, i);
                code = R+1;
              }
              else if(M == 'm') //monitor mode
              {
                // set prevratio if not done already
                if (prevRatio < 0.0) {
                  prevRatio = currRatio;
                  System.err.println("\tMonitor mode");
                }
                // compute running ratios from this point on
                currRatio = numBitsRead / numBitsWritten;
                double ratioOfRatios = prevRatio / currRatio;

                // reset if compression degrades above certian limit
                if(ratioOfRatios > MonitorRatioRate)
                {
                  System.err.println("\t\tcRatio: "  + currRatio + " pRatio: " + prevRatio);
                  System.err.println("\tRatio of ratios is above " + MonitorRatioRate);
                  runningL = startingL;
                  runningW = startingW;

                  st = new TST<Integer>(); //reset TST
                  for (int i = 0; i < R; i++)
                      st.put("" + (char) i, i);
                  code = R+1;
                  prevRatio = -1.0;     //unset the prevRatio
                }
              }
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, runningW);  // write EoF as last code
        numBitsWritten += runningW;
        BinaryStdOut.close();
        System.err.println("BitsRead: " + numBitsRead + " BitsWritten: " + numBitsWritten + " CurrRatio:" + (numBitsRead / numBitsWritten));

    }


    public static void expand() throws IOException
    {
        String[] st = new String[maxL];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int runningL = startingL;
        int runningW = startingW;

        double numBitsRead = 0.0;  // compressed bits
        double numBitsWritten = 0.0; // uncompressed bits
        double currRatio = 0.0;  // numBitsWritten / numBitsRead
        double prevRatio = -1.0;  // -1 means not set

        // Get mode from the first byte
        char M = (char) BinaryStdIn.readByte();
        if (M != 'n' && M!= 'r' && M != 'm')
        {
          System.err.println("Illegal mode: " + M);
          return;
        }
        numBitsRead += 8;

        int codeword = BinaryStdIn.readInt(runningW);  //read in first codeword
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];          //get corresponding value
        numBitsRead += runningW;

        while (true)
        {
            BinaryStdOut.write(val);      //write value to expanded file
            numBitsWritten += 8 * val.length();

            boolean reset = false; // did we reset the code book?
            if(runningW < maxW && i == runningL) //increase by runningW by 1 bit
            {
              runningW++;
              runningL *= 2;
            }
            else if(runningW == maxW && i == runningL) //codebook is Full
            {
              if(M == 'r')
              {
                System.err.println("\tReset mode");

                runningL = startingL;
                runningW = startingW;
                st = new String[maxL];  //reset table
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";                        // (unused) lookahead for EOF
                reset = true;
              }
              else if(M == 'm') //monitor mode
              {
                // set prevratio if not done already
                if (prevRatio < 0) {
                  prevRatio = currRatio;
                  System.err.println("\tMonitor mode");
                }
                // continue computing new ratios
                currRatio = numBitsWritten / numBitsRead;
                double ratioOfRatios = prevRatio / currRatio;

                // reset if compression degrades above certian limit
                if(ratioOfRatios > MonitorRatioRate)
                {
                  System.err.println("\tRatio of ratios over " + MonitorRatioRate);
                  System.err.println("\t\tprev Ratio: "  + prevRatio + " curr Ratio: " + currRatio);
                  runningL = startingL;
                  runningW = startingW;
                  st = new String[maxL];  //reset table
                  for (i = 0; i < R; i++)
                      st[i] = "" + (char) i;
                  st[i++] = "";                        // (unused) lookahead for EOF
                  reset = true;
                  prevRatio = -1.0;                   //unset the prevRatio
                }
              }
            }
            codeword = BinaryStdIn.readInt(runningW);
            numBitsRead += runningW;
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < runningL && !reset) {
              st[i++] = val + s.charAt(0);
              currRatio = numBitsWritten / numBitsRead; // update ratios as new code entries added
            }
            val = s;
        }
        BinaryStdOut.close();
        System.err.println("BitsRead: " + numBitsRead + " BitsWritten: " + numBitsWritten + " CurrRatio:" + (numBitsRead / numBitsWritten));

    }


    public static void main(String[] args) throws IOException {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
