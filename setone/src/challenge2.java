/**
 * Created by jtresise on 1/5/2016.
 */

import static javax.xml.bind.DatatypeConverter.*;

public class challenge2 {

    public static String hexToBase64 (String hex) {

        byte[] hexBytes = parseHexBinary(hex);
        String base64 = printBase64Binary(hexBytes);

        System.out.println(base64);
        return base64;

    }

    public static String xor (String one, String two) {

        byte[] array1 = parseHexBinary(one);
        byte[] array2 = parseHexBinary(two);

        byte[] output = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            output[i] = (byte) (array1[i] ^ array2[i]);
        }

        return printHexBinary(output);

    }

    public static void main (String[] args){

        String one = "1c0111001f010100061a024b53535009181c";
        String two = "686974207468652062756c6c277320657965";

        System.out.println(xor(one, two));
    }
}
