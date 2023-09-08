/* Jennifer Ortega, Harjot Gill, Nihal Gunukula & Huaxiao Luo
 *
 * This program will use the Huffman tree algorithm to
 * compress a String of text from a source file. After
 * calculating the frequency of characters in the file,
 * they will be sorted in order of frequency. A heap will aid in
 * creating the tree and a final huffman tree will be created of
 * all characters. The path to the character will determine its code
 * and this code will then be stored in a string array to aid in
 * creating the compressed text. The compressed text is built in a loop and
 * the contents are sent to an output file which will contain the huffman
 * codes, the size (int) of the compressed Text into the file (for part 2)
 * and the coded/compressed text into the file */

/** Imports */

import java.io.*;
import java.util.*;

public class Compress_a_File {

    public static void main(String[] args) throws IOException {

        String nameOfSourceFile;
        String nameOfTargetFile;

        /*Have user enter file names if the
        arguments are not entered properly on the
        command prompt */
        if (args.length == 2) {
            nameOfSourceFile = args[0];
            nameOfTargetFile = args[1];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter Source file name: ");

            nameOfSourceFile = sc.next();
            System.out.print("\nEnter Target file name: ");
            nameOfTargetFile = sc.next();
        }

        File fileOfSource = new File(nameOfSourceFile);     //open source file

        if (!fileOfSource.exists()) {
            System.out.println("File " + nameOfSourceFile + " does not exist! Please enter a valid file name.");
            System.exit(2);
        }

        byte[] bytes = null;    //create byte array to store bytes from file

        /* try to create an iterator for the source file and store its bytes
        into the bytes array. Display any exceptions*/
        try {
            DataInputStream inputStreamFileSource = new DataInputStream(new FileInputStream(fileOfSource));

            int amount = inputStreamFileSource.available();
            bytes = new byte[amount];

            inputStreamFileSource.read(bytes);     //inputs asci codes of characters into array
            inputStreamFileSource.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String byteString = new String(bytes);    /*Converts contents of byte array to a single String
                                                    containing all the characters in the file in order */

        int[] current = getCharacterFrequency(byteString);

        //creates a huffman tree after determining the ammount of character occurences
        Tree huffmanTree = getHuffmanTree(current);

        //creates a binary code (huffman code) for each character based on # of occurences/position on the huffman tree
        String[] stringCode = getCode(huffmanTree.rootOfNode);

        StringBuilder compressedText = new StringBuilder();

        //add to a string which will consist of the coded message/file contents
        for (int x = 0; x < byteString.length(); x++) {
            compressedText.append(stringCode[byteString.charAt(x)]);
        }

        /*1.try to open a new file and send the huffman codes into the file first.
          2. send the size (int) of the compressed Text into the file (for part 2)
          3. send the actual coded/compressed text into the file */
        try {
            ObjectOutputStream codesOutput = new ObjectOutputStream(new FileOutputStream(nameOfTargetFile));
            codesOutput.writeObject(stringCode);
            codesOutput.writeInt(compressedText.length());
            codesOutput.close();

            BitOutputStream bitOutputStream = new BitOutputStream(new File(nameOfTargetFile));
            bitOutputStream.writeBit(compressedText.toString());
            bitOutputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    static class BitOutputStream {

        private final ArrayList<Integer> arrayList = new ArrayList<>();
        private final DataOutputStream dataOutputStream;

        public BitOutputStream(File file) throws FileNotFoundException {
            dataOutputStream = new DataOutputStream(new FileOutputStream(file, true));
        }

        public void writeBit(char bit) throws IOException {

            if (bit == '0') {
                arrayList.add(0);
            } else {
                arrayList.add(1);
            }

            if (arrayList.size() == 8) {
                dataOutputStream.writeByte(getByte());
                arrayList.clear();
            }
        }

        public void writeBit(String bit) throws IOException {

            for (int x = 0; x < bit.length(); x++) {
                writeBit(bit.charAt(x));
            }
        }

        public void close() throws IOException {

            while (!arrayList.isEmpty()) {
                writeBit('0');
            }
            dataOutputStream.close();
        }

        private byte getByte() {

            int amount = 0;

            for (int x = 7, number = 1; x >= 0; x--, number *= 2) {
                amount = amount + arrayList.get(x) * number;
            }
            return (byte) amount;
        }
    }

    /** Get Huffman codes for the characters
     * This method is called once after a Huffman tree is built
     */
    public static String[] getCode(Tree.Node root) {

        if (root == null) {
            return null;
        }
        String[] stringCode = new String[256];
        assignCode(root, stringCode);

        return stringCode;
    }

    /* Recursively get codes to the leaf node */
    private static void assignCode(Tree.Node root, String[] codes) {

        if (root.left != null) {
            root.left.code = root.code + "0";
            assignCode(root.left, codes);

            root.right.code = root.code + "1";
            assignCode(root.right, codes);
        } else {
            codes[(int) root.element] = root.code;
        }
    }

    /** Get a Huffman tree from the codes */
    public static Tree getHuffmanTree(int[] counts) {
        // Create a heap to hold trees
        Heap<Tree> treeHeap = new Heap<Tree>();
        for (int x = 0; x < counts.length; x++) {
            if (counts[x] > 0) {
                treeHeap.add(new Tree(counts[x], (char) x));  // A leaf node tree
            }
        }

        while (treeHeap.getSize() > 1) {
            Tree t1 = treeHeap.remove();    // Remove the smallest weight tree
            Tree t2 = treeHeap.remove();    // Remove the next smallest weight
            treeHeap.add(new Tree(t1, t2));  // Combine two trees
        }

        return treeHeap.remove();   // The final tree
    }

    /*Goes thru text and increments an array at the index of the ASCI number
    to keep track of occurences */
    public static int[] getCharacterFrequency(String text) {

        int[] curr = new int[256];  //256 ASCII characters

        for (int x = 0; x < text.length(); x++) {
            curr[(int) text.charAt(x)]++;   // Count the character in text
        }
        return curr;
    }

    /** Define a Huffman coding tree */
    public static class Tree implements Comparable<Tree> {

        Node rootOfNode;    // The root of the tree

        /** Create a tree with two subtrees */
        public Tree(Tree treeOne, Tree treeTwo) {

            rootOfNode = new Node();
            rootOfNode.left = treeOne.rootOfNode;
            rootOfNode.right = treeTwo.rootOfNode;
            rootOfNode.weight = treeOne.rootOfNode.weight + treeTwo.rootOfNode.weight;
        }

        /** Create a tree containing a leaf node */
        public Tree(int weight, char element) {

            rootOfNode = new Node(weight, element);
        }

        @Override   /** Compare trees based on their weights */
        public int compareTo(Tree tree) {
            return Integer.compare(tree.rootOfNode.weight, rootOfNode.weight);
        }

        public class Node {

            char element; // Stores the character for a leaf node
            int weight; // weight of the subtree rooted at this node
            Node left; // Reference to the left subtree
            Node right; // Reference to the right subtree
            String code = ""; // The code of this node from the root


            /** Create an empty node */
            public Node() {
            }

            /** Create a node with the specified weight and character */
            public Node(int weight, char element) {
                this.weight = weight;
                this.element = element;
            }
        }
    }

    static class Heap<E extends Comparable<E>> {

        private final java.util.ArrayList<E> list = new java.util.ArrayList<E>();

        /** Create an empty heap */
        public Heap() {
        }

        /** Create a heap from an array of objects */
        public Heap(E[] objects) {
            for (E object : objects) {
                add(object);
            }
        }

        /** Add a new object into the heap */
        public void add(E newObject) {

            list.add(newObject);    // Append to the heap
            int currIn = list.size() - 1;   // The index of the last node

            while (currIn > 0) {
                int parentIndex = (currIn - 1) / 2;

                // Swap if the current object is greater than its parent
                if (list.get(currIn).compareTo(list.get(parentIndex)) > 0) {
                    E temp = list.get(currIn);
                    list.set(currIn, list.get(parentIndex));
                    list.set(parentIndex, temp);

                } else {
                    break;  // the tree is a heap now
                }
                currIn = parentIndex;
            }
        }

        /** Remove the root from the heap */
        public E remove() {
            if (list.size() == 0) {
                return null;
            }

            E objRem = list.get(0);
            list.set(0, list.get(list.size() - 1));
            list.remove(list.size() - 1);

            int specificElem = 0;
            while (specificElem < list.size()) {
                int leftChildIndex = 2 * specificElem + 1;
                int rightChildIndex = 2 * specificElem + 2;

                // Find the maximum between two children
                if (leftChildIndex >= list.size()) {
                    break;
                }
                int maxIndex = leftChildIndex;
                if (rightChildIndex < list.size()) {
                    if (list.get(maxIndex).compareTo(list.get(rightChildIndex)) < 0) {
                        maxIndex = rightChildIndex;
                    }
                }

                // Swap if the current node is less than the maximum
                if (list.get(specificElem).compareTo(list.get(maxIndex)) < 0) {
                    E temp = list.get(maxIndex);
                    list.set(maxIndex, list.get(specificElem));
                    list.set(specificElem, temp);
                    specificElem = maxIndex;
                } else {
                    break;      // The tree is a heap
                }
            }

            return objRem;
        }

        public int getSize() {
            return list.size();
        }
    }
}
