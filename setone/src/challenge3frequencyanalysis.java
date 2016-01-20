/**
 * Created by jtresise on 1/5/2016.
 */

import static javax.xml.bind.DatatypeConverter.*;
import org.apache.commons.lang3.*;

public class challenge3frequencyanalysis {

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

    public static void main(String[] args) {

        String cipherstring = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        byte[] ciphertext = parseHexBinary(cipherstring);

        System.out.println(ciphertext);

        String[] hexChars = new String[] {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        for (int i = 0; i < hexChars.length; i++){
            int count = StringUtils.countMatches(cipherstring, hexChars[i]);
            System.out.println(hexChars[i] + "\t" + count);
        }

    }
}
