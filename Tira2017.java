import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//Java version 1.8.0_60-b27.
//Data structures 2017 course work, Tampere University by Juho Torkkeli (jt422421).
//Class Tira2017 represents our implementation of hash table with the help of HashNode.java.
//Creates sparse table with 10000 storing places of null values.
//Missing: no remove key from table function, not adjusting table load factor (constant 10000).
public class Tira2017<K, V> {
	
	//File names for input files setA & setB.
	private String inputFileA = "setA.txt";
	private String inputFileB = "setB.txt";
	
	//File names for or, and & xor output files.
	private String orFileName = "or.txt";
	private String andFileName = "and.txt";
	private String xorFileName = "xor.txt";
	
	//ArrayLists setA & setB contain data from inputFileA & inputFileB respectively.
	private ArrayList<Integer> setA = new ArrayList<Integer>();
	private ArrayList<Integer> setB = new ArrayList<Integer>();
	
	//ArrayList (table) to have keys and values.
	private ArrayList<HashNode<K, V>> table;
	
	//Initial capacity of table.
	private int initialTableCapacity;
	
	//Constructor
	//Initialize variables table & initialTableCapacity.
	public Tira2017() {
		table = new ArrayList<>();
		initialTableCapacity = 10000;
		
		//Creates an empty table.
		for (int i = 0; i < initialTableCapacity; i++) {
			table.add(null);
		}
	}
	
	//Simple hash code (not sure if hashCode is allowed in this exercise).
	private int getTableIndex(K key) {
		int hashCode = key.hashCode();
		int index = hashCode % initialTableCapacity;
		return index;
	}
	
	//Returns value for a key. Returns null if no key present in table.
	private V getValue(K key) {
		int tableIndex = getTableIndex(key);
		HashNode<K, V> head = table.get(tableIndex);
		
		while (head != null) {
			if (head.key.equals(key)) {
				return head.value;
			}
			head = head.next;
		}
		return null;
	}
	
	//Adds keys and values to a table. If key already present, updates value.
	private void add(K key, V value) {
		int tableIndex = getTableIndex(key);
		HashNode<K, V> head = table.get(tableIndex);
		
		while (head != null) {
			if (head.key.equals(key)) {
				head.value = value;
			}
			head = head.next;
		}
		head = table.get(tableIndex);
		HashNode<K, V> newNode = new HashNode<K, V>(key, value);
		newNode.next = head;
		table.set(tableIndex, newNode);
	}
	
	//Reads both files inputFileA & inputFileB line by line into ArrayLists setA & setB.
	private void readFiles() {
		String readLine;
		
		try {
			BufferedReader brA = new BufferedReader(new FileReader(inputFileA));
			while ((readLine = brA.readLine()) != null) {			
				setA.add(Integer.parseInt(readLine.replaceAll("\\s+","")));
			}
			brA.close();
			BufferedReader brB = new BufferedReader(new FileReader(inputFileB));
			while ((readLine = brB.readLine()) != null) {			
				setB.add(Integer.parseInt(readLine.replaceAll("\\s+","")));
			}
			brB.close();
		}
		
		//Print error message if files are not found.
		catch(IOException e) {
		    System.out.println("Files not found!");
		}
	}
	
	//Writes table to a given outputFileName. Skips null keys.
	private void writeToFile(String outputFileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
			Iterator<HashNode<K, V>> iterator = table.iterator();
			HashNode<K, V> lineToWrite;
			
			while (iterator.hasNext()) {
				lineToWrite = iterator.next();
				if (lineToWrite != null) {
				String formatStr = "%4s %4s%n";
				bw.write(String.format(formatStr, lineToWrite.key, lineToWrite.value));
				}
			}
			bw.close();
		}
		
		//Print error if file cannot be written!
		catch(IOException e) {
			System.out.println("Couldn't write to a file!");
		}
		
		System.out.println("Successfully wrote to " + outputFileName + "!");
	}
	
	//Tira2017 coursework instructions:
	//OR operation second column contains the information how many times an integer appears in input files.
	private void or() {
		//Create a new instance of class inside or-method to store union of setA & setB in table.
		Tira2017<Integer, Integer> tiraOr = new Tira2017<>();
		
		//Traverse through setA.
		for (int i = 0; i < setA.size(); i++) {
			Integer setAKey = setA.get(i);
			
			//Add key and adjust value to 1 if key = null.
			if(tiraOr.getValue(setAKey) == null) {
				tiraOr.add(setAKey, 1);
			}
			
			//Else adjust value to previous value + 1.
			else {
				Integer tableValue = tiraOr.getValue(setAKey);
				tiraOr.add(setAKey, tableValue + 1);
			}
		}
		
		//Traverse through setB.
		for (int i = 0; i < setB.size(); i++) {
			Integer setBKey = setB.get(i);
			
			//Add key and adjust value to 1 if key = null.
			if (tiraOr.getValue(setBKey) == null) {
			tiraOr.add(setBKey, 1);
			}
			
			//Else adjust value to previous value +1.
			else {
				Integer tableValue = tiraOr.getValue(setBKey);
				tiraOr.add(setBKey, tableValue + 1);
			}
		}
		
		//Write table to or.txt.
		tiraOr.writeToFile(orFileName);
	}
	
	//Tira2017 coursework instructions:
	//AND operation second column contain the row number of setA.txt where an integer appears first time.
	private void and(){
		//Create a new instance of class inside and-method to store intersection of setA & setB in table.
		Tira2017<Integer, Integer> tiraAnd = new Tira2017<>();
		Integer rowNumber = 0;
		
		//Traverse through setA. Take note of rowNumber.
		for (Integer num:setA) {
			rowNumber = rowNumber + 1;
			
			//If key found in both setA & setB and value not yet modified.
			if (setB.contains(num) && tiraAnd.getValue(num) == null) {
				
				//Add key and rowNumber as value.
				tiraAnd.add(num, rowNumber);
			}
		}
		
		//Write table to and.txt.
		tiraAnd.writeToFile(andFileName);
	}
	
	//Tira2017 coursework instructions:
	//XOR operation second column contain number 1 or 2 according to which of the files setA.txt
	//or setB.txt contain the integer in question.
	private void xor(){
		//Create a new instance of class inside xor-method to store XOR of setA & setB in table.
		Tira2017<Integer, Integer> tiraXor = new Tira2017<>();
		
		//setA = 1, setB = 2.
		Integer fileA = 1;
		Integer fileB = 2;
		
		//Traverse through setA.
		for (Integer num:setA) {
			
			//If setA key not in setB.
			if (!setB.contains(num)) {
				
				//Add key and filenumber as value.
				tiraXor.add(num, fileA);
			}
		}
		
		//Traverse through setB.
		for (Integer num:setB) {
			
			//If setB key not in setA.
			if (!setA.contains(num)) {
				
				//Add key and filenumber as value.
				tiraXor.add(num, fileB);
			}
		}
		
		//Write table to xor.txt.
		tiraXor.writeToFile(xorFileName);
	}
	
	//Main method. Running all the stuff inside methods.
	public static void main(String[]args) {
		Tira2017<Integer, Integer> tira = new Tira2017<>();
		System.out.println("Info: Sparse table. 10000 storing spaces. Not tested with large integers (>9999)");
		tira.readFiles();
		tira.or();
		tira.and();
		tira.xor();
		System.out.println("DONE!");
	}
}
