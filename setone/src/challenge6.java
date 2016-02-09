package src;

/**
 * Created by Joseph on 009 9 Jan.
 */

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import static javax.xml.bind.DatatypeConverter.*;

public class challenge6 {
    public static String textToHex(String text){

        char[] plainChars = text.toCharArray();

        StringBuffer hexConstruct = new StringBuffer();

        for (int i = 0; i < plainChars.length; i++){
            hexConstruct.append( Integer.toHexString(plainChars[i] | 0x100).substring(1) );
        }

        return hexConstruct.toString();
    }

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

    //Sets a BitSet to hold a byte. Never got used. Awkward...
    public static BitSet byteToBitSet(byte input) {
        BitSet bitSet = new BitSet(Byte.SIZE);
        for (int i = 0; i < Byte.SIZE; i++) {

            //Set the BitSet to the byte of 'one'
            if (((input >> i) & 1) == 1) {
                bitSet.set(i);
            } else {
                bitSet.set(i, false);
            }
        }

        return bitSet;
    }

    public static int calcHammingByte(byte one, byte two) {
        int differences = 0;

        for (int i = 0; i < Byte.SIZE; i++){
            if( ( (one >> i) & 1) != ( (two >> i) & 1) ){
                differences++;
            }
        }

        return differences;
    }

    /**Calculates Hamming dist of two strings
     * Splits into bytes and passes to calcHammingByte
     */
    public static int calcHamming(String one, String two){

        //Convert strings to bytes[]
        byte[] oneBytes = parseHexBinary(textToHex(one));
        byte[] twoBytes = parseHexBinary(textToHex(two));

        //Byte-wise, confirm if one[i] === two[i]
        //Check the strings are identical in length
        int differences = -1;
        if (oneBytes.length == twoBytes.length) {
            differences = 0;

            //For each byte in string...
            for (int i = 0; i < oneBytes.length; i++) {

                differences += calcHammingByte(oneBytes[i], twoBytes[i]);
            }
        }

        //Return number of differences
        return differences;
    }

    public static int calcHammingByteArray(byte[] one, byte[] two){

        //Byte-wise, confirm if one[i] === two[i]
        //Check the strings are identical in length
        int differences = -1;
        if (one.length == two.length) {
            differences = 0;

            //For each byte in string...
            for (int i = 0; i < one.length; i++) {

                differences += calcHammingByte(one[i], two[i]);
            }
        }

        //Return number of differences
        return differences;
    }

    /**No lies, the english mode is a bit crap
     * Takes in a string and returns an integer representing the number of hits for a given charSet
     * Can be set to hex or english -- Doesn't return the character it matches
     * Used in hex mode to detect un-uniformity of character frequency
     * @param cypherstring - string to check
     * @param mode - "hex" or "english"
     * @return - number of matches of a certain character
     */
    public static int frequencyAnalyse(String cypherstring, String mode) {

        //Setting mode of operation. Hex or English?
        String[] charsSet = new String[0];
        if (mode == "hex"){
            charsSet = new String[] {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        }
        if (mode == "english") {
            charsSet = new String[] {" ","e","t","a","j","x","q","z"};
        }

        //Init vars
        int[] frequency = new int[charsSet.length];
        int maximum = 0;

        for (int i = 0; i < charsSet.length; i++){
            //Check frequency and store the most likely result
            frequency[i] = StringUtils.countMatches(cypherstring, charsSet[i]);
            if (frequency[i] > maximum){ maximum = frequency[i]; }
        }

        if (mode == "english") {
            int score = (int) ((frequency[0]*3)+(frequency[1]*2.5)+(frequency[2]*2)+(frequency[3]*1.5)+
                    (frequency[4]*0.5)+(frequency[5]*0.4)+(frequency[6]*0.3)+(frequency[7]*0.2));
            if (score > 120 && StringUtils.isAsciiPrintable(cypherstring)) {
                System.out.println(score);
            }
            return score;
        }


        return maximum;

    }

    /**Iterates over keyspace and analyses frequency of english letters to brute force a solution
     * Could be improved by working addition of StringUtils.isAlphaNumericSpace() to check legibility
     * Could be improved by setting key to Byte.LOWEST_VALUE and ++'ing until Byte.GREATEST_VALUE
     * @param cypherstring String of either english or hex
     * @return Returns String of hex
     */
    public static String bruteForce(String cypherstring) {

        byte[] ciphertext = parseHexBinary(cypherstring);

        //Variable will contain most probable solution
        int maximum = 0;
        String candidate = null;

        //Char key and start brute force
        byte key;
        for (key = Byte.MIN_VALUE; key < Byte.MAX_VALUE; key++) {

            byte[] output = new byte[ciphertext.length];

            //Byte-wise xor'ing
            for (int j = 0; j < ciphertext.length; j++) {
                output[j] = (byte)(ciphertext[j] ^ key);
            }

            //Analyse xoredOuput
            String xoredOutput = hexToString(byteArrayToString(output));
            int round = frequencyAnalyse(xoredOutput, "english");

            //I tried to cut out the obvious crap with StringUtils.isAlphanumericSpace but no success
            //StringUtils.isAsciiPrintable(xoredOutput)
            if (round > maximum && true ) {
                maximum = round;
                candidate = xoredOutput;
            }
            //System.out.println(xoredOutput);

        }

        return candidate;
    }

    //Reads in a file and returns a string of the contents
    public static String fileReader (String fileName) {

        String line;

        try {
            //Open file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //cypherTextConstruct because in this example I'm constructing a string from cypher text
            StringBuilder cypherTextConstruct = new StringBuilder();

            //Init vars
            int maximum = 0;
            int frequency = 0;

            //Read each line (freq was checked here in challenge 4) and check freq
            while ( ( line = bufferedReader.readLine() ) != null ) {

                cypherTextConstruct.append(line);
            }

            bufferedReader.close();

            return cypherTextConstruct.toString();
        }

        catch(FileNotFoundException ex){
            System.out.println("Can't find file " + fileName);
        }

        catch(IOException ex){
            System.out.println("Error!");
        }

        return new String();
    }

    public static float analyseXOR(String XORedHex, int keysize){
        //Get HD for 4 substrings of XORedHex
        int sum = calcHamming(XORedHex.substring(0, keysize),XORedHex.substring(keysize, 2 * keysize));
        sum += calcHamming(XORedHex.substring(2 * keysize, 3 * keysize),XORedHex.substring(3 * keysize, 4 * keysize));
        sum += calcHamming(XORedHex.substring(4 * keysize, 5 * keysize),XORedHex.substring(5 * keysize, 6 * keysize));
        sum += calcHamming(XORedHex.substring(6 * keysize, 7 * keysize),XORedHex.substring(7 * keysize, 8 * keysize));

        //Average them
        //Divide by keysize and 4 as we took 4 blocks
        float average = (float) sum / (keysize * 4);

        //Return them
        return average;
    }

    public static void main(String[] args){

        //Confirm that the Hamming Dist works... it do!
        String first = "this is a test";
        String second = "wokka wokka!!!";
        System.out.println("Hamming Distance between \"" + first + "\" and \"" + second + "\" is " + calcHamming(first, second));

        //Read in the file
        String dir = System.getProperty("user.dir") + "\\setone\\src\\6.txt";
        String cypherText = fileReader(dir);

        //Un Base64 it to get XOR'd hex
        String XORedHex = printHexBinary(parseBase64Binary(cypherText));
        byte[] XORedBytes = parseBase64Binary(cypherText);

        //Calc Hamming Dist of first keysize bytes and second keysize bytes
        //Probably easier to use substrings to pass than byte[]
        int maxKeysize = 40;
        ArrayList<Integer> keysizeCandidates = new ArrayList<>();

        //Generate array of Hamming Distances
        for (int keysize = 2; keysize < maxKeysize; keysize++) {

            float avgHD = analyseXOR(XORedHex, keysize);

            //If the hamm dist is less than one, add the keysize as a candidate
            if ( avgHD < 2.0f ){
                System.out.println("Added! " + avgHD);
                keysizeCandidates.add(keysize);
            }


        }

        //Un petit interlude while I test the brute-forcer...
        //String copypasta = "211d523b551f5213521f17111a131c1b115213065213521617131e1700011a1b0252131c165205175201171752011d1f175205171b001652061a1b1c150152111d1f17521b1c5c523a1d0016170052111300015e521f1701010b52111300015e52140711191b1c155210001d19171c52130101521113000152061a130652011a1d071e161c5506521017521d1c52061a1752001d13165e52100706521c1d061a1b1c1552111d1f021300171652061d52061a1752011a1b065201061d001f521d1452135211130052061a1b015201061d000b521b015213101d07065c523b551f521f1b1c161b1c15521f0b521d051c521007011b1c17010152051d00191b1c15521d1c521352110701061d1f17005204171a1b111e1752051a171c523b52011717521d1c17521d14521d070052021d00061700015210001b1c151b1c1552011d1f175219170b015213001d071c165e52131c16521a17550152151d0652061a1b0152191b1c16521d1452161b01150701061716521e1d1d19521d1c521a1b0152141311175c523b5e5210171b1c15521d1c17521d1452061a17521f1d00175201171c1b1d005215070b01521b1c52061a1752011a1d025e5213011952051a130655015207025c523a175201130b0152061a1752111300521a175216001d04175213001d071c165218070106521c1d05520513015202001706060b521f1701010b521007065205130152061a1752051d00010652011f171e1e1b1c1552111300521a17521a13165217041700521017171c521b1c5c523b521e1307151a521306521a1b1f5e5206171e1e521a1b1f52061d52151706521013111952061d52051d001952131c16520200130b523b551f521c1d0652061a175215070b52051a1d521a130152061d52051d0019521d1c521b065c523e0711190b521f175e523b52151706521b06521301521f0b521c170a0652181d105c523c1d52101b15521617131e5e523b550417521617131e0652051b061a521113000152051b061a521f1d1e160b52111300021706015e521113000152051b061a52001d0606171c521f1b1e1952131c165217151501521b1c52061a171f5e521113000152051b061a521013100b5202071917521d1c52061a1752011713060149521a1d05521013165211131c521b065210174d523b52151706521b1c52061a175211130052131c16520613191752135210001713061a5e521513155e52131c16521c1d0217521d0706521d1452061a1700175c5235001310521f0b52071c161700111d13061b1c1552001701021b0013061d0052131c165200170607001c5c523704171c52061a1b015202001d141701011b1d1c131e5215001316175210001713061a1b1c155213020213001306070152161d1701521c1d065214071e1e0b52101e1d1119521d070652061a1752011f171e1e5c52261a1752111300521b015218070106521b1c52141d005213521013011b1152011700041b11175e52100706523b55041752151d0652061d52191c1d055e521f131c5c52261a17521b1c011b1617521b0152191b1c16521d14521f1701010b52100706521c1d061a1b1c15523b5516520017131e1e0b5211131e1e5213101c1d001f131e5c52371f02060b523f11361d1c131e160152101315521a1700175e52011d1f17521d1e1652111d14141717521107020152061a1700175e521352150b1f521013155e52011d1f1752161d15520600171306015e52131c1652011d1f17521d1e1652001711171b02060152131c165202130217000152131c165218071c195c523c1d061a1b1c15520017131e1e0b521d1414171c011b0417521a1700175c5c5c52211d523b52151706521b06521b1c52061a1752011a1d0252131c1652161d5213521e1b151a0652111a1711195e52131c165215170652061d1e1652061a1752111300521a13015213521007001c1716521d0706521000131917521e1b151a065c523b52021d0252061a17520600071c1952131c1652151706521d0706521d1452061a17521113005e5205131e195213001d071c16521b0652131c16521d02171c52061a17520600071c1952061d52111a131c151752061a175210071e105c52271c141d0006071c1306171e0b5e523b52141d071c1652061a1752011d07001117521d1452061a1752011f171e1e5c52361d1552011a1b065c52261a17520600071c19521b0152001d07151a1e0b521a131e145214071e1e52051b061a52180701065200130552161d1552060700165c523b065501521e1b191752061a1752111300521d051c1700521807010652021b1119171652070252061a171b0052161d150152021d1d52131c1652061d01011716521b06521b1c52061a17520600071c195e52071c1013151517165c52211e131f1f171652061a17520600071c1952111e1d01171652131c165216001d041752061a1752111300521d0706011b16175c523f131c1315171652061d52151706521d0706521d14521b0652131c1652061a1752011700041b1117521f131c13151700520017131f1716521d070652061a1752110701061d1f170052141d00521704171c5210001b1c151b1c1552061a1752111300521b1c521b1c52061a130652111d1c161b061b1d1c5c52251d0001065c5236130b5c52370417005c";
        //System.out.println(bruteForce(copypasta));
        //Gonna test the hexToText module
        //System.out.println(hexToString("556e69742074657374696e67"));
        //It works like a fucking dream!

        //Now brute-force each keyspace
        //First separate into byte sized blocks for per-byte brute forcing

        //For each keysize candidate
        for (int i : keysizeCandidates){

            //Let's get an array going to hold the blocks...
            ArrayList<String> cypherBlocks = new ArrayList<>();

            //For each byte of the keysize
            for (int j = 0; j < i; j=j+2){

                StringBuilder block = new StringBuilder();

                //For every 'i'th byte to form the block
                for (int k = j; k < XORedHex.length(); k = k + i){
                    try {
                        block.append(XORedHex.substring(k ,(k + 2)));
                    }
                    catch (StringIndexOutOfBoundsException UpperLimitEx){
                        //end of string reached. Need to avoid odd-number length blocks.
                    }
                }
                cypherBlocks.add(block.toString());
                //System.out.println(block.toString());
            }

            //Now let's brute force each block.
            //Remember, each block has it's own key...

            StringBuilder plaintext = new StringBuilder();
            for (String block : cypherBlocks){

                plaintext.append(bruteForce(block));
            }
            System.out.println((plaintext.toString()));

        }

    }
}
/**Problems:
 *The plaintext was base64'd after encryption. I'm not looking for English...
 * Need to come up with a new way to measure the bruteforces for correctness
 * Although it easier to filter out by AlphaNumeric now
 * "It's [Plaintext] been base64'd after being encrypted with repeating-key XOR."
 * Plaintext > XOR'd > base64'd
 * I need to unBase64 and then un XOR. Right?
 *The plaintext blocks aren't the right size at larger keys... and I've just figured out why! No I haven't :( Sorted!!
 * Getting errors in my hex to String - Write some unit tests. Fun times...
 */