/***********************************************************************************
 * Cryptography (CSCE 557) – Spring 2016
 * Programming Assignment #1: Vigenère cipher
 * Author/copyright: Noemi Glaeser
 * Last modified: 19 February 2016
 *
 * This program analyzes a ciphertext, assuming it is encrypted using a Vigenère,
 * in two steps:
 * (1) First the program determines the probable key length using the index of
 *     coincidence method (http://practicalcryptography.com/cryptanalysis/
 *     stochastic-searching/cryptanalysis-vigenere-cipher/)
 * (2) Using a key length inputted by the user, each monoalphabetic sequence
 *     in the ciphertext is analyzed separately. Frequencies of each ciphertext
 *     letter are outputted and the user can test different shifts before accepting
 *     one and moving on to the next sequence in the ciphertext.
 *
 * Input: Ciphertext, inputted by user into the console
 *
 * Output: Index of coincidence (I.C.), calculated for every key length (up to a
 *            user-specified max)
 *         Letter frequencies for every monoalphabetic sequence in the ciphertext
 *         Partially decrypted ciphertext after each monoalph sequence substitution
 *            (following convention, ciphertext letters are uppercase, plaintext
 *            lowercase)
 *
 *         Missing (possibly to add in future revisions):
 *            Vigenère key the ciphertext employed
 *
 * Note: In this program, the word 'sequence' refers to the multiple monoalphabetic
 *       components of the ciphertext.
 *       i.e., for a key length of 3, the ciphertext consists of 3 monoalphabetic
 *       sequences of characters at positions
 *       1, 4, 7, 10, 13, 16, ...
 *       2, 5, 8, 11, 14, 17, ...
 *       3, 6, 9, 12, 15, 18, ...
 */

// to do: "which character would you like to use as 'e'?" should only take one character, not a whole word
//        organize code with more methods
//        print key

// Ciphertext provided for this assignment:
// xkjurowmllpxwznpimbvbqjcnowxpcchhvvfvsllfvxhazityxohulxqojaxelxzxmyjaqfstsrulhhucdskbxknjqidallpqslluhiaqfpbpcidsvcihwhwewthbtxrljnrsncihuvffuxvoukjljswmaqfvjwjsdyljogjxdboxajultucpzmpliwmlubzxvoodybafdskxgqfadshxnxehsaruojaqfpfkndhsaafvulluwtaqfrupwjrszxgpfutjqiynrxnyntwmhcukjfbirzsmehhsjshyonddzzntzmplilrwnmwmlvuryonthuhabwnvw

import java.util.*;

public class Glaeser_Program1 {
    public static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static void main(String[] args)
    {
        Scanner keybd = new Scanner(System.in);
        System.out.print("Type ciphertext here:");
        String str = keybd.nextLine().toUpperCase();

        //break ciphertext into characters in a character array
        char[] ciphertext = parse(str);

        System.out.println("\nWhat max length of key would you like to check?");
        int n = keybd.nextInt();

        System.out.print("\nOriginal: ");
        for(char c : ciphertext)
            System.out.print(c);
        System.out.println();

        /****** PART 1: FINDING THE KEY LENGTH *******/

        //create array to store I.C. for every key length up to max
        double[] icavgs = new double[n];

        //calculate I.C. for each key length – start at 2 and go up to max
        //see "Glaeser_homework1.pdf" for the formula used
        for(int k = 2; k <= n; k++)
        {
            double avg = 0.0;
            //variable i is used to iterate through the ciphertext in multiples
            //to break the ciphertext into sequences
            for(int i = 0; i < k; i++)
            {
                double ic = 0.0;
                int a = 0; //length of the sequence

                //first, we count the number of letters in the sequence
                for(int x = i; x < ciphertext.length; x = x+k)
                    a++;

                for(char c = 'A'; c <= 'Z'; c++) {
                    int fi = 0; //value fi for each character, to be summed
                    //loop to calculate the I.C.
                    for (int x = i; x < ciphertext.length; x = x + k)
                    {
                        //if the current character is the one we're iterating for, increase its count
                        if(ciphertext[x] == c)
                            fi++;
                    }
                    //I.C. = sum of this expression for every char A through Z
                    ic += (fi * (fi - 1));
                }
                //calculated I.C. for this sequence
                ic = ic/(a * (a-1));
                avg += ic;
            }
            //Average the I.C. of every sequence at this key length
            avg = avg/k;

            //put avg I.C.'s in a table (each at index (key length-1))
            icavgs[k - 1] = avg;
        }

        printtable(icavgs);

        //Tell the user at which key length the maximum I.C. occurs
        System.out.println("Max I.C. = " + max(icavgs));

        /****** PART 2: DECODING THE CIPHERTEXT *******/

        System.out.println("What key length would you like to attempt to crack with?");
        int keylength = keybd.nextInt();

        int[] key = new int[keylength];

        String seq = "";
        String curr = "";
        int k = 0;
        //work with each sequence in turn
        while(k < key.length)
        {
            //build sequence to analyze
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
            //print letter frequencies in the sequence
            printfreq(seq);

            System.out.println("Which character would you like to use as \'e\'?");
            try
            {
                char c = Character.toLowerCase(keybd.next().charAt(0));
                //plaintext version of the current sequence
                String ptxtseq = replaceShiftE(seq, c);

                //print the partially decrypted ciphertext (named "curr") with this sequence decrypted
                //when you hit a multiple of k, replace with the next char from the ptxtseq string
                curr = "";
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
                try
                {
                    if(keybd.next().equalsIgnoreCase("y"))
                        ciphertext = curr.toCharArray();
                    //if no, loop through choosing an 'e' again
                    else
                        k--;
                }
                catch (InputMismatchException e)
                {
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
            // ( index of current letter 'c' + shift ) %26 = plaintext index
            //%26 might return a negative
            int i = (ABC.indexOf(c) + shift)%26; //index of the plaintext character
            if(i < 0)
                i += 26;
            s = s.replace(c + "", Character.toLowerCase(ABC.charAt(i)) + ""); //replace ciphertext char with plaintext
        }
        return s;
    }

    //print frequencies of characters of each sequence
    public static void printfreq(String s)
    {
        //store letter frequencies
        int[] freq = new int[26];
        freq = clear(freq);

        for(char c = 'A'; c <= 'Z'; c++)
        {
            for (int x = 0; x < s.length(); x++)
            {
                if(s.charAt(x) == c)
                    freq[ABC.indexOf(c)]++;
            }
        }
        System.out.println();
        for(int i = 0; i < freq.length; i++)
        {
            System.out.print(ABC.charAt(i) + ":\t" + freq[i] + "\t");
            //create a bar-graph-like representation of frequency with asterisks
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
        for(int i = 1; i < d.length; i++) //don't print I.C. for period 1
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
        for(int i = 0; i < n; i++)
            ret[i] = c[i];
        return ret;
    }

    //find and print maximum I.C.
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