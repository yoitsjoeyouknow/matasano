/**
 * Created by jtresise on 1/5/2016.
 */

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

public class challenge4 {

    public static String byteArrayToString(byte[] array) {

        char out[] = new char[array.length * 2];

        for (int i = 0; i < array.length; i++){
            out[i * 2] = "0123456789ABCDEF".charAt( (array[i] >> 4) & 15);
            out[i * 2 + 1] = "0123456789ABCDEF".charAt( array[i] & 15);
        }

        return new String(out);
    }

    public static String hexToString(String input) {

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i+=2) {
            String str = input.substring(i, i+2);
            output.append( (char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    //No lies, the english mode is a bit crap
    public static int frequencyAnalyse(String cypherstring, String mode) {

        //Setting mode of operation. Hex or English?
        String[] charsSet = new String[0];
        if (mode == "hex"){
            charsSet = new String[] {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        }
        if (mode == "english") {
            charsSet = new String[] {"e","t","a"};
        }

        //Init vars
        int[] frequency = new int[charsSet.length];
        int maximum = 0;

        for (int i = 0; i < charsSet.length; i++){
            //Check frequency and store the most likely result
            frequency[i] = StringUtils.countMatches(cypherstring, charsSet[i]);
            if (frequency[i] > maximum){ maximum = frequency[i]; }
        }

        return maximum;

    }

    public static String bruteForce(String cypherstring) {

        byte[] ciphertext = parseHexBinary(cypherstring);

        //Variable will contain most probable solution
        int maximum = 0;
        String candidate = null;

        //Char key and start brute force
        char key = 0;
        for (int i = 0; i < 256; i++) {

            byte[] output = new byte[ciphertext.length];

            //Byte-wise xor'ing
            for (int j = 0; j < ciphertext.length; j++) {
                output[j] = (byte)(ciphertext[j] ^ key);
            }

            //Analyse xoredOuput
            String xoredOutput = hexToString(byteArrayToString(output));
            int round = frequencyAnalyse(xoredOutput, "english");

            //I tried to cut out the obvious crap with StringUtils.isAlphanumericSpace but no success
            if (round > maximum && true ) {
                maximum = round;
                candidate = xoredOutput;
            }
            //System.out.println(xoredOutput);


            key = (char)(key + 0b0000000000000001);
        }

        return candidate;
    }

    public static void fileReader (String fileName) {

        String line;

        try {
            //Open file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Init vars
            int maximum = 0;
            int frequency = 0;
            String maxline = null;

            //Read each line and check freq
            while ( ( line = bufferedReader.readLine() ) != null ) {
                frequency = frequencyAnalyse(line, "hex");

                //See if most likely candidate
                if (frequency > maximum ) {
                    maximum = frequency;
                    maxline = line;
                }
            }

            System.out.println(maxline);
            System.out.println(maximum);
            System.out.println(bruteForce(maxline));

            bufferedReader.close();
        }

        catch(FileNotFoundException ex){
            System.out.println("Can't find file " + fileName);
        }

        catch(IOException ex){
            System.out.println("Error!");
        }


    }

    public static void main(String[] args){
        String fileName = "D:\\Users\\jtresise\\matasano\\setone\\src\\4.txt";
        fileReader(fileName);

    }
}
