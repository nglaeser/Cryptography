/***********************************************************************************
 * Cryptography (CSCE 557) – Spring 2016
 * Programming Assignment #2: One round simplified DES
 * Author/copyright: Noemi Glaeser
 * Created: 16 February 2016
 * Last modified: 26 February 2016
 *
 * This program implements one round of simplified DES (as presented in section 4.2
 * of the text for this course)
 * The algorithm is as follows:
 * (1)
 *
 * Input: Plaintext string of bits, inputted by user into console
 *
 * Output: Encrypted string of bits (after one round of simplified DES)
 *
 *//*


//questions:
//assume input length will be multiple of 12?

import java.util.*;

public class Glaeser_Program2bak {
    public static final int NUM_ROUNDS = 1;

    public static void main(String[] args)
    {
        Scanner keybd = new Scanner(System.in);
        String in = keybd.nextLine();
        //int length = keybd.nextLine().length();

        //BitSet plaintext = new BitSet(length);
        //BitSet plaintext = setBits(in, length);

        BitSet ciphertext = des(in, NUM_ROUNDS);
        String cipherstr = Integer.toBinaryString(ciphertext);
        System.out.println("After encryption with one round of simplified DES:\n" + cipherstr);
    }

    public static BitSet setBits(String s, int len)
    {
        BitSet bs = new BitSet(len);
        for(int i = 0; i < len; i++)
        {
            if(s.charAt(i) == '1')
                bs.set(i);
            else if(s.charAt(i) != '0')
            {
                //string was not in binary
                System.out.println("You must enter a binary string.");
                System.exit(0);
            }
        }
        return bs;
    }

    public static BitSet des(String bits, int rounds)
    {
        if(rounds < 1)
            return bits;

        //break into 12-bit units
        String s = bits.toString();
        String ans = "";
        for(int i = 0; i < s.length(); i += 12) //i is the left end of the 12-bit-long substring to encrypt
        {
            String curr;
            if(i+12  > s.length()) //less than 12 bits remaining to encrypt
            {
                //pad the end with 0's?
                //curr = ...
            }
            else
                curr = s.substring(i, i+12);
            BitSet left = setBits(curr.substring(0, 6), 6);
            BitSet right = setBits(curr.substring(6, 12), 6);

            //need to get new left and new right, then assign them appropriately to proceed to next round.
            ans += des(left, right).toString();
        }
        //split into left and right
        //new left, new right

        //calls a recursive method with left, right
    }
    public static BitSet des(BitSet left, BitSet right) //one round
    {

    }

}
*/
