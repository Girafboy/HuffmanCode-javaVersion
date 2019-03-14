import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void main() {
        ArrayList<String> testDemo = new ArrayList<>();
        testDemo.add("trace00.txt");
        testDemo.add("trace01.txt");
        testDemo.add("trace02.txt");
        testDemo.add("trace03.txt");
        testDemo.add("trace04.txt");
        testDemo.add("trace05.txt");
        testDemo.add("trace06.txt");
        testDemo.add("trace07.bin");
        testDemo.add("trace08.bmp");
        testDemo.add("trace09.mid");
        testDemo.add("shakespeare.txt");

        String[] compression=new String[2];
        String[] decompression=new String[3];
        compression[1]="output.txt";
        decompression[0]="-d";
        decompression[1]="output.txt";
        decompression[2]="test.txt";

        for(String temp : testDemo){
            compression[0]=temp;
            Main.main(compression);
            Main.main(decompression);
            try{
                byte[] before= new FileInputStream(temp).readAllBytes();
                byte[] after= new FileInputStream("test.txt").readAllBytes();
                assertArrayEquals(before,after);
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }

    }
}