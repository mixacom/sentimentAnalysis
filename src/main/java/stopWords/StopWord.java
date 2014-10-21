package stopWords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import constants.Components;
import au.com.bytecode.opencsv.CSVWriter;

public class StopWord {
	
	HashMap<String, Integer> map;

	public StopWord() {
	    try {
	        map = getTweetContents();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
	
	private static HashMap<String, Integer> getTweetContents() throws IOException {
		HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(Components.getBaseFilePath() + "src/main/java/stopWords/tweetContentt.txt"));
		
		String line;
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			for (String word : words) {
				word = word.toLowerCase();
				if (wordCountMap.containsKey(word)) {
					wordCountMap.put(word, wordCountMap.get(word) + 1);
				} else {
					wordCountMap.put(word, 1);
				}
			}
		}
		br.close();
		return sortByValues(wordCountMap);
	}
	
	public HashMap<String, Integer> getWords() {
		return map;
	}
	
	private static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	                  .compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });

	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	}
	
	public void writeFile() throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(Components.getBaseFilePath() + "src/main/java/stopWords/stopWordVeri2.csv"));
		for (String key : map.keySet()) {
	    	String[] write = new String[2];
	    	write[0] = key;
	    	write[1] = "" + map.get(key);
			csvWriter.writeNext(write);
	    }
		csvWriter.close();
	}
	
	public static void main(String[] args) {
	    StopWord swv = new StopWord();
//	    try {
	        //swv.writeFile();
	        System.out.println(swv.getWords().toString());
//        } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
	    
    }
}
