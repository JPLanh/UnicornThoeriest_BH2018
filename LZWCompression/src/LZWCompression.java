import java.io.*;
import java.util.*;

public class LZWCompression {

    public HashMap<String, Integer> table = new HashMap<String, Integer>();
    private String[] arrayChar;
    private int count;

    // Default Constructor
    public LZWCompression() {}

    // Compress File method: Creates compressed file.
    // Para: compressFileName, compressedFileName.
    // The method reads a byte and then it is converted to a 12 byte.
    // Store the 2 (12bits) in an array of 3 byte length and the same is written to the file.
    public void compress(String compressFileName, String compressedFileName) throws IOException {

        // Initialization hashmap table.
        arrayChar = new String[4096];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            arrayChar[i] = Character.toString((char) i);
        }
        count = 256;

        // Stream pointer to read and write file.
        DataInputStream read = new DataInputStream(new BufferedInputStream(new FileInputStream(compressFileName)));
        DataOutputStream write = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(compressedFileName)));

        byte inputByte;
        String temp = "";
        byte[] buffer = new byte[3];
        boolean onleft = true;

        try {
            // Read the First Character from input file into the String
            inputByte = read.readByte();
            int i = new Byte(inputByte).intValue();
            if (i < 0) {
                i += 256;
            }
            char c = (char) i;
            temp = "" + c;

            // Read Character by Character
            while (true) {
                inputByte = read.readByte();
                i = new Byte(inputByte).intValue();

                if (i < 0) {
                    i += 256;
                }
                c = (char) i;

                if (table.containsKey(temp + c)) {
                    temp = temp + c;
                } else {
                    String s12 = to12bit(table.get(temp));

                    // Store the 12 bits into an array and then write it to the output file
                    if (onleft) {
                        buffer[0] = (byte) Integer.parseInt(
                                s12.substring(0, 8), 2);
                        buffer[1] = (byte) Integer.parseInt(
                                s12.substring(8, 12) + "0000", 2);
                    } else {
                        buffer[1] += (byte) Integer.parseInt(
                                s12.substring(0, 4), 2);
                        buffer[2] = (byte) Integer.parseInt(
                                s12.substring(4, 12), 2);
                        for (int b = 0; b < buffer.length; b++) {
                            write.writeByte(buffer[b]);
                            buffer[b] = 0;
                        }
                    }
                    onleft = !onleft;
                    if (count < 4096) {
                        table.put(temp + c, count++);
                    }
                    temp = "" + c;
                }
            }

        } catch (EOFException e) {
            String temp12bit = to12bit(table.get(temp));
            if (onleft) {
                buffer[0] = (byte) Integer.parseInt(temp12bit.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(temp12bit.substring(8, 12) + "0000", 2);
                write.writeByte(buffer[0]);
                write.writeByte(buffer[1]);
            } else {
                buffer[1] += (byte) Integer.parseInt(temp12bit.substring(0, 4), 2);
                buffer[2] = (byte) Integer.parseInt(temp12bit.substring(4, 12), 2);
                for (int b = 0; b < buffer.length; b++) {
                    write.writeByte(buffer[b]);
                    buffer[b] = 0;
                }
            }
            read.close();
            write.close();
        }

    }

    // Convert 8 bit to 12 bit
    public String to12bit(int i) {
        String temp = Integer.toBinaryString(i);
        while (temp.length() < 12) {
            temp = "0" + temp;
        }
        return temp;
    }

    // Extract the 12 bit key from 2 bytes and get the int value of the key
    // return an Integer which holds the value of the key
    public int getValue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);
        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        // On left being true
        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }

    }

    // Decompress File method: Creates decompressed file.
    // Para: decompressFileName, decompressedFileName.
    // In the decompression method it reads in 3 bytes of information and 
    // write 2 characters corresponding to the bits read.
    public void decompress(String decompressFileName, String decompressedFileName) throws IOException {
        
        // Initialize hashmap table.
        arrayChar = new String[4096];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            arrayChar[i] = Character.toString((char) i);
        }
        count = 256;

        // Stream pointer to read and write file.
        DataInputStream read = new DataInputStream(new BufferedInputStream(new FileInputStream(decompressFileName)));
        DataOutputStream write = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(decompressedFileName)));

        int currword, priorword;
        byte[] buffer = new byte[3];
        boolean onleft = true;
        try {

            // Get the first word in code and output its corresponding character
            buffer[0] = read.readByte();
            buffer[1] = read.readByte();

            priorword = getValue(buffer[0], buffer[1], onleft);
            onleft = !onleft;
            write.writeBytes(arrayChar[priorword]);

            // Read every 3 bytes and generate a corresponding characters - 2 character
            while (true) {

                if (onleft) {
                    buffer[0] = read.readByte();
                    buffer[1] = read.readByte();
                    currword = getValue(buffer[0], buffer[1], onleft);
                } else {
                    buffer[2] = read.readByte();
                    currword = getValue(buffer[1], buffer[2], onleft);
                }
                onleft = !onleft;
                if (currword >= count) {

                    if (count < 4096) {
                        arrayChar[count] = arrayChar[priorword] + arrayChar[priorword].charAt(0);
                    }
                    count++;
                    write.writeBytes(arrayChar[priorword] + arrayChar[priorword].charAt(0));
                } else {

                    if (count < 4096) {
                        arrayChar[count] = arrayChar[priorword] + arrayChar[currword].charAt(0);
                    }
                    count++;
                    write.writeBytes(arrayChar[currword]);
                }
                priorword = currword;
            }

        } catch (EOFException e) {
            read.close();
            write.close();
        }
    }

    public static void main(String[] args) throws IOException {
        
        LZWCompression lzw = new LZWCompression();

        System.out.println("Please enter 'c' to compress or 'd' to decompress file.\n");

        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        switch(userInput){
            case "c": case "C": 
                System.out.println("Please enter target file name to compress:\n");
                String compressFileName = in.nextLine();
                System.out.println("Please enter file name to save the compressed file:\n");
                String compressedFileName = in.nextLine();
                lzw.compress(compressFileName, compressedFileName);
                System.out.println(compressedFileName + " has been successfully created.");
                break;
                
            case "d": case "D":
                System.out.println("Please enter target file name to decompress:\n");
                String decompressFileName = in.nextLine();
                System.out.println("Please enter file name to save the decompressed file:\n");
                String decompressedFileName = in.nextLine();
                lzw.decompress(decompressFileName, decompressedFileName);
                System.out.println(decompressedFileName + " has been successfully created.");
                break;
            default:
                System.out.println("Invalid input has entered. Please try again.");
                break;
        }
        in.close();
    }
}
