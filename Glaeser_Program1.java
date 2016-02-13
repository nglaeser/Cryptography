package com.company;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;

import java.util.*;

//Noemi Glaeser
//Cryptography - Spring 2016

public class Glaeser_Program1 {
    public static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static void main(String[] args) {
        Scanner keybd = new Scanner(System.in);
        System.out.print("Type ciphertext here:");
        String str = keybd.nextLine().toUpperCase();

        char[] ciphertext = parse(str);

        System.out.println("\nWhat max length of key would you like to check?");
        int n = keybd.nextInt(); //max length of key
        System.out.print("Original: ");
        for(char c : ciphertext)
        System.out.print(c);
        System.out.println();

        double[] icavgs = new double[n];

        //find key length using index of coincidence method (http://practicalcryptography.com/cryptanalysis/stochastic-searching/cryptanalysis-vigenere-cipher/)
        for(int k = 2; k <= n; k++) //start guessing a key length 2 and go up to the inputted key length
        {
            System.out.println();
            System.out.println("\n***Key length: " + k + "***");

            double avg = 0.0;
            for(int i = 0; i < k; i++) //in order to do multiples and break up ciphertext (k is the current key length attempted)
            {
                System.out.print("\nSequence " + (i+1) + ": ");
                double ic = 0.0;
                int a = 0; //to count length of the sequence
                for(int x = i; x < ciphertext.length; x = x+k) //first, print out the sequence
                {
                    System.out.print(ciphertext[x]); //prints sequence
                    a++;
                }
                for(char c = 'A'; c <= 'Z'; c++) {
                    int fi = 0; //value fi for each character, to be summed
                    for (int x = i; x < ciphertext.length; x = x + k) //same loop but to find the i.c.
                    {
                        if(ciphertext[x] == c) //if the character is the one we're iterating for rn, incr count of that char
                            fi++;
                    }
                    ic += (fi * (fi - 1)); //sum up the expression for every char a through z
                }
                ic = ic/(a * (a-1)); //calculated i.c. for this sequence
                System.out.print("\tI.C.: " + ic);
                avg += ic;
            }
            avg = avg/k;
            System.out.println("\tAverage: " + avg);

            icavgs[k - 1] = avg; //put avg i.c.'s in a table (each at index key length - 1
        }

        printtable(icavgs); //print the avg i.c.'s for each key length in a table

        System.out.println("Max I.C. = " + max(icavgs));

        //now, decode ciphertext
        System.out.println("What key length would you like to attempt to crack with?");
        int keylength = keybd.nextInt();

        int[] key = new int[keylength];
        //get input from keyboard for key length to try
        //frequency analysis for each nth letter

        String seq = "";
        String curr = "";
        int k = 0;
        while(k < key.length)
        {
            if(k < key.length) {
                System.out.print("Sequence " + (k+1) + ": ");
                seq = "";
                int i = k;
                do {
                    System.out.print(ciphertext[i]);
                    seq += ciphertext[i];
                    i += key.length;
                } while (i < ciphertext.length);
            }
            printfreq(seq); //prints out char freq of the sequence

            System.out.println("Which character would you like to use as \'e\'?");
            try{
                char c = keybd.next().charAt(0);
                String ptxtseq = replaceShiftE(seq, c);

                //print the full ciphertext, replaced
                //when you hit a multiple of k, replace with the next char from the ptxtseq string
                curr = ""; //new partially decrypted ciphertext
                for(int m = 0; m < ciphertext.length; m++)
                {
                    if((m - k) % 5 == 0) {
                        curr += ptxtseq.charAt((m - k) / 5);
                    }
                    else
                        curr += ciphertext[m];
                }

                System.out.println(curr);

                    System.out.println("Accept replacement? y/n");
                    try {
                        if(keybd.next().equalsIgnoreCase("y")) {
                            ciphertext = curr.toCharArray();
                            //break;
                        }
                        //if no, it'll just loop through choosing an 'e' again
                        else {
                            System.out.println("Want to return to the previous sequence? y/n");
                            while(true) {
                                try {
                                    if (keybd.next().equalsIgnoreCase("y")) {
                                        //System.out.println("Enter sequence number (1-" + (k+1) + "): ");
                                                k--;
                                                    break;
                                        }
                                    break;
                                }
                                catch (InputMismatchException e) {
                                    System.out.println("enter y or n");
                                }
                            }
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("enter y or n");
                    }

            }
            catch(InputMismatchException e)
            {
                System.out.println("You must enter a character.");
            }
            k++;

            if(k == key.length)
            {
                System.out.println("\n*** You decrypted the cipher! ***\n");
                System.out.println("Plaintext: " + curr);
            }
        }
    }

    //replace a ciphertext sequence with the plaintext given a ciphertext character ch that translates to plaintext e
    public static String replaceShiftE(String s, char ch) //s is the sequence
    {
        int shift = 4 - ABC.indexOf((ch+"").toUpperCase());
        for(char c = 'A'; c <= 'Z'; c++){ //ciphertext chars to be replaced
            //index of E - ch determines shift
            //4 - index of ch = shift
            // ( index of currect letter c + shift ) %26 = plaintext
            //%26 might return a negative
            int i = (ABC.indexOf(c) + shift)%26; //index of the plaintext character
            if(i < 0)
                i += 26;
            s = s.replace(c + "", Character.toLowerCase(ABC.charAt(i)) + ""); //replace ciphertext char with plaintext
        }
        return s;
    }

    //print frequences of characters of each sequence
    public static void printfreq(String s)
    {
        int[] freq = new int[26]; //to store the letter frequencies of this sequence
        freq = clear(freq); //fill it with zeros

        for(char c = 'A'; c <= 'Z'; c++) { //count character frequencies
            for (int x = 0; x < s.length(); x++) //loop through the string
            {
                if(s.charAt(x) == c) //if the character is the one we're checking for rn, incr count of that char
                    freq[ABC.indexOf(c)]++;
            }
        }
        System.out.println();
        for(int i = 0; i < freq.length; i++)
        {
            System.out.print(ABC.charAt(i) + ":\t" + freq[i] + "\t");
            for(int k = 0; k < freq[i]; k++)
            {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    //print I.C. table
    public static void printtable(double[] d)
    {
        System.out.println("\nperiod \t\t avg I.C.");
        System.out.println("-----------------------------------");
        for(int i = 0; i < d.length; i++)
        {
            System.out.println(i+1 + ":\t\t" + d[i]);
        }
    }

    //break string into character array
    public static char[] parse(String s)
    {
        int n = 0; //integer to count the number of actual characters not including spaces
        char[] chars = new char[s.length()];

        for(int k = 0; k < s.length(); k++)
        {
            if(s.charAt(k) == ' ') //if the character is a space, don't include it
            {}
            else {
                chars[n]=s.charAt(k);
                n++;
            }
        }
        chars = shorten(chars, n);
        return chars;
    }

    //remove empty spots at end of array, after the n filled spots
    public static char[] shorten(char[] c, int n)
    {
        char[] ret = new char[n];
        for(int i = 0; i < n; i++) //copy every element from c into ret up to n
            ret[i] = c[i];

        return ret;
    }

    //find maximum I.C.
    public static String max(double[] d)
    {
        double max = 0.0;
        int index = 0;
        for(int i = 0; i < d.length; i++)
        {
            if(d[i] > max) {
                max = d[i];
                index = i;
            }
        }
        return max + " at keylength " + (index+1);
    }

    //fill an array x with zeros
    public static int[] clear(int[] x)
    {
        for(int i = 0; i < x.length; i++)
        {
            x[i] = 0;
        }
        return x;
    }
}
