/**
 * Created by jtresise on 1/5/2016.
 */

import static javax.xml.bind.DatatypeConverter.*;

public class challenge1 {

    public static void main (String[] args) {

        byte[] hexBytes = parseHexBinary("49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d");
        String base64 = printBase64Binary(hexBytes);

        System.out.println(base64);

    }
}
