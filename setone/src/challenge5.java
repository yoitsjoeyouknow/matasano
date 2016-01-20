/**
 * Created by jtresise on 1/8/2016.
 */

import org.apache.commons.lang3.StringUtils;
import static javax.xml.bind.DatatypeConverter.*;

public class challenge5 {

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

        //Convert String to hex
        String plaintext = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal";
        char[] plainChars = plaintext.toCharArray();

        StringBuffer plainHexConstruct = new StringBuffer();

        for (int i = 0; i < plainChars.length; i++){
            plainHexConstruct.append( Integer.toHexString(plainChars[i] | 0x100).substring(1) );
        }

        String plainHex = plainHexConstruct.toString();

        //Convert hex to byte stream
        byte[] plainBytes = parseHexBinary(plainHex);

        //Encrypt Vanilla Ice
        byte[] cypherBytes = new byte[plainBytes.length];
        byte I = 0x49;
        byte C = 0x43;
        byte E = 0x45;

        for(int i = 0; i < plainBytes.length; i=i+3){
            try {
                cypherBytes[i] = (byte) (plainBytes[i] ^ I);
                cypherBytes[i + 1] = (byte) (plainBytes[i + 1] ^ C);
                cypherBytes[i + 2] = (byte) (plainBytes[i + 2] ^ E);
            }
            catch(ArrayIndexOutOfBoundsException ex){
                break;
            }
        }

        String cypherHex = byteArrayToString(cypherBytes);
        String cyphertext = hexToString(cypherHex);

        System.out.println(cypherHex);
        System.out.println(cyphertext);
    }
}
