import javax.print.attribute.standard.Compression;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Compression("trace06.txt","output.txt");
        Decompression("output.txt","test.txt");
    }
    private static void Compression(String inputFile,String outputFile){
        try{
            FileInputStream fileInputStream =new FileInputStream(inputFile);
            byte[] bytes = fileInputStream.readAllBytes();
            int[] freq = new int[256];
            for(byte b : bytes)
                freq[b]++;

            Tree tree= new Tree(freq);
            Bit bits=new Bit();
            for(byte b : bytes)
                bits.Append(tree.codes.getCode((char)b));
            byte[] result = bits.toByteArray();
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(bytes.length>>24);
            fileOutputStream.write(bytes.length>>16);
            fileOutputStream.write(bytes.length>>8);
            fileOutputStream.write(bytes.length);
            fileOutputStream.write('|');
            fileOutputStream.write(tree.getLeafCount());
            for(int i=0 ; i<256 ; i++)
                if(freq[i]!=0){
                    fileOutputStream.write(i);
                    fileOutputStream.write(tree.codes.getCode((char)i).length());
                    fileOutputStream.write(tree.codes.getCode((char)i).toByteArray());
                }
            fileOutputStream.write('|');
            fileOutputStream.write(result);
        }catch (FileNotFoundException e){
            System.out.println(e.toString());
            return;
        }catch (IOException e){
            System.out.println(e.toString());
            return;
        }
    }
    private static void Decompression(String inputFile, String outputFile) {
        try{
            FileInputStream fileInputStream =new FileInputStream(inputFile);
            int byteCount = fileInputStream.read();
            byteCount=(byteCount<<8)+fileInputStream.read();
            byteCount=(byteCount<<8)+fileInputStream.read();
            byteCount=(byteCount<<8)+fileInputStream.read();
            int flag1 =fileInputStream.read();
            if(flag1!='|') throw new Exception("Decompression file have a wrong format");
            int charCount =fileInputStream.read();

            ArrayList<Boolean>[] codelist = new ArrayList[256];
            for(int i=0; i<charCount; i++){
                char ch=(char) fileInputStream.read();
                int length = fileInputStream.read();
                byte[] code =new byte[length];
                fileInputStream.read(code,0,(length+7)/8);
                Bit bit = new Bit();
                bit.Add(code,length);
                codelist[ch] =bit.Get();
            }
            int flag2 =fileInputStream.read();
            if(flag2!='|') throw new Exception("Decompression file have a wrong format");

            Tree tree= new Tree(codelist);
            Bit bits = new Bit();
            byte[] bytes = fileInputStream.readAllBytes();
            bits.Add(bytes,bytes.length*8);

            byte[] result = tree.decodes.getChar(bits,byteCount);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(result);
        }catch (FileNotFoundException e){
            System.out.println(e.toString());
            return;
        }catch (IOException e){
            System.out.println(e.toString());
            return;
        }catch (Exception e){
            System.out.println(e.toString());
            return;
        }
    }
}
class Tree {
    private enum Position{LEFT_CHILD,RIGHT_CHILD,TOP};
    private class Node implements Comparable<Node>{
        private char val;
        private int frequency;
        Node left = null;
        Node right = null;
        Node top = null;
        Position position=null;
        public Node(char val, int frequency){
            this.val=val;
            this.frequency=frequency;
            this.position=Position.TOP;
        }
        public Node(char val, int frequency,Node left,Node right){
            this.val=val;
            this.frequency=frequency;
            this.left=left;
            this.right=right;
            this.position=Position.TOP;
            this.left.position=Position.LEFT_CHILD;
            this.left.top=this;
            this.right.position=Position.RIGHT_CHILD;
            this.right.top=this;
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency-o.frequency;
        }

        public ArrayList<Boolean> getCode(){
            ArrayList<Boolean> code=new ArrayList<>();

            Node temp =this;
            while(temp.position!=Position.TOP){
                if(temp.position == Position.LEFT_CHILD)
                    code.add(false);
                else if(temp.position==Position.RIGHT_CHILD)
                    code.add(true);
                temp=temp.top;
            }
            Collections.reverse(code);
            return code;
        }
    }
    public class Code{
        private ArrayList<Boolean>[] codes=new ArrayList[256];
        public void Add(char character, ArrayList<Boolean> code){
            codes[character]=code;
        }
        public Bit getCode(char character){
            Bit bits=new Bit();
            if(codes[character]!=null)
                for(Boolean b :codes[character]){
                    bits.Add(b);
                }
            return bits;
        }
    }
    public class DeCode{
        private HashMap<ArrayList<Boolean>,Character> map=new HashMap<>();
        public void Add(char character, ArrayList<Boolean> code){
            map.put(code,character);
        }
        public byte[] getChar(Bit bit, int length){
            byte[] bytes = new byte[length];
            int count=0;
            ArrayList<Boolean> booleans= bit.Get();
            ArrayList<Boolean> temp = new ArrayList<>();
            while(count<length){
                temp.add(booleans.get(0));
                booleans.remove(0);
                if(map.containsKey(temp)){
                    bytes[count]= (byte) (char)map.get(temp);
                    count++;
                    temp.clear();
                }
            }
            return bytes;
        }
    }
    private ArrayList<Node> leafNodes= new ArrayList<>();
    private Node treeRoot;
    private int nodeCount = 0;
    public Code codes =new Code();
    public DeCode decodes =new DeCode();
    public Tree(int[] freq){
        for(int i=0; i<freq.length;i++)
            if(freq[i]!=0){
                leafNodes.add(new Node((char)i,freq[i]));
                nodeCount++;
            }
        BuildTree();
        Encode();
    }
    public Tree(ArrayList<Boolean>[] codelist){
        for(int i=0; i<codelist.length;i++)
            if(codelist[i]!=null)
                decodes.Add((char)i,codelist[i]);
    }
    private void BuildTree(){
        ArrayList<Node> treeNodes= new ArrayList<>();
        treeNodes.addAll(leafNodes);
        while(treeNodes.size()>1){
            Collections.sort(treeNodes);
            treeNodes.add(new Node('0', treeNodes.get(0).frequency+treeNodes.get(1).frequency,treeNodes.get(0),treeNodes.get(1)));
            nodeCount++;
            treeNodes.remove(1);
            treeNodes.remove(0);
        }
        treeRoot = treeNodes.get(0);
    }
    private void Encode(){
        for(Node n : leafNodes)
            if(n.left==null&&n.right==null)
                codes.Add(n.val,n.getCode());
    }
    public int getLeafCount(){
        return leafNodes.size();
    }
}

class Bit{
    private ArrayList<Boolean> bits;
    public Bit(){
        bits=new ArrayList<>();
    }
    public void Add(Boolean bit){
        bits.add(bit);
    }
    public void Add(byte[] bytes, int length){
        for(byte b : bytes){
            for(int i=0; i<8; i++){
                byte mark = (byte) (0x01<<(7-i));
                if((b&mark)==mark)
                    bits.add(true);
                else
                    bits.add(false);
                length--;
                if(length<=0)
                    return;
            }
        }
    }
    public ArrayList<Boolean> Get(){
        return bits;
    }
    public ArrayList<Boolean> Append(Bit bit){
        bits.addAll(bit.bits);
        return bits;
    }
    public int length(){
        return bits.size();
    }
    public byte[] toByteArray(){
        byte[] bytes =new byte[(bits.size()+7)/8];
        for (int i=0;i<bits.size();i++){
            if(bits.get(i)){
                if(i==1012)
                    i=i;
                bytes[i/8]|=1<<(7-i%8);
            }

        }
        return bytes;
    }
}