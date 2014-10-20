package vectorSpace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;



import java.util.Set;


//import jeasy.analysis.MMAnalyzer;

public class ReadFiles {

    private static List<Integer> fileList = new ArrayList<Integer>();
    private static HashMap<Integer, HashMap<String, Float>> allTheTf = new HashMap<Integer, HashMap<String, Float>>();
    private static HashMap<Integer, HashMap<String, Float>> allTheTfIdf = new HashMap<Integer, HashMap<String, Float>>();
    private static HashMap<String, Float> allTheIdf = new HashMap<String, Float>();
    public static HashMap<String, Float> allTheDf = new HashMap<String, Float>();

    public static List<Integer> readDirs(HashMap<Integer, String> tweets) throws FileNotFoundException, IOException {
//        try {
//            File file = new File(filepath);
//            if (!file.isDirectory()) {
//                System.out.println("杈撳叆鐨勫弬鏁板簲璇ヤ负[鏂囦欢澶瑰悕]");
//                System.out.println("filepath: " + file.getAbsolutePath());
//            } else if (file.isDirectory()) {
//                String[] filelist = file.list();
//                for (int i = 0; i < filelist.length; i++) {
//                    File readfile = new File(filepath + "\\" + filelist[i]);
//                    if (!readfile.isDirectory()) {
//                        //System.out.println("filepath: " + readfile.getAbsolutePath());
//                        fileList.add(readfile.getAbsolutePath());
//                    } else if (readfile.isDirectory()) {
//                        readDirs(filepath + "\\" + filelist[i]);
//                    }
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
    	Set<Integer> j = tweets.keySet();
		Iterator<Integer> tweetsID = j.iterator();
		while (tweetsID.hasNext()) {
			fileList.add(tweetsID.next());
		}
        return fileList;
    }

    public static String readFiles(int file,HashMap<Integer, String> tweets) throws FileNotFoundException, IOException {
//        StringBuffer sb = new StringBuffer();
//        InputStreamReader is = new InputStreamReader(new FileInputStream(file), "gbk");
//        BufferedReader br = new BufferedReader(is);
//
//        String line = br.readLine();
//        while (line != null) {
//            sb.append(line).append("\r\n");
//            line = br.readLine();
//        }
//        br.close();
        return tweets.get(file);
    }

    public static String[] cutWord(int file, HashMap<Integer, String> tweets) throws IOException {
        String[] cutWordResult = null;
        String text = ReadFiles.readFiles(file,tweets);
//        MMAnalyzer analyzer = new MMAnalyzer();
        //System.out.println("file content: "+text);
        //System.out.println("cutWordResult: "+analyzer.segment(text, " "));
//        String tempCutWordResult = analyzer.segment(text, " ");
        cutWordResult = text.split(" ");
        return cutWordResult;
    }
    
    
    public static String[] cutQuery(String query) throws IOException {
        String[] cutWordResult = null;
//        MMAnalyzer analyzer = new MMAnalyzer();
        //System.out.println("file content: "+text);
        //System.out.println("cutWordResult: "+analyzer.segment(text, " "));
//        String tempCutWordResult = analyzer.segment(query, " ");
        cutWordResult = query.split(" ");
        return cutWordResult;
    }
    
    
    public static HashMap<String, Float> tf(String[] cutWordResult) {
        HashMap<String, Float> tf = new HashMap<String, Float>();//姝ｈ鍖�
        int wordNum = cutWordResult.length;
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            for (int j = 0; j < wordNum; j++) {
                if (cutWordResult[i] != " " && i != j) {
                    if (cutWordResult[i].equals(cutWordResult[j])) {
                        cutWordResult[j] = " ";
                        wordtf++;
                    }
                }
            }
            if (cutWordResult[i] != " ") {
                tf.put(cutWordResult[i], (new Float(++wordtf)) / wordNum);
                cutWordResult[i] = " ";
            }
        }
        return tf;
    
    }
    
    public static HashMap<String, Float> tfForQuery(String[] cutWordResult) {
        HashMap<String, Float> tf = new HashMap<String, Float>();//姝ｈ鍖�
        int wordNum = cutWordResult.length;
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            for (int j = 0; j < wordNum; j++) {
                if (cutWordResult[i] != " " && i != j) {
                    if (cutWordResult[i].equals(cutWordResult[j])) {
                        cutWordResult[j] = " ";
                        wordtf++;
                    }
                }
            }
            if (cutWordResult[i] != " ") {
                tf.put(cutWordResult[i], (new Float(++wordtf)) / wordNum);
                cutWordResult[i] = " ";
            }
        }
        return tf;
    
    }

//    public static HashMap<String, Integer> normalTF(String[] cutWordResult) {
//        HashMap<String, Integer> tfNormal = new HashMap<String, Integer>();//娌℃湁姝ｈ鍖�
//        int wordNum = cutWordResult.length;
//        int wordtf = 0;
//        for (int i = 0; i < wordNum; i++) {
//            wordtf = 0;
//            if (cutWordResult[i] != " ") {
//                for (int j = 0; j < wordNum; j++) {
//                    if (i != j) {
//                        if (cutWordResult[i].equals(cutWordResult[j])) {
//                            cutWordResult[j] = " ";
//                            wordtf++;
//
//                        }
//                    }
//                }
//                tfNormal.put(cutWordResult[i], ++wordtf);
//                cutWordResult[i] = " ";
//            }
//        }
//        return tfNormal;
//    }

    public static HashMap<Integer, HashMap<String, Float>> tfOfAll(HashMap<Integer, String> tweets) throws IOException {
        List<Integer> fileList = ReadFiles.readDirs(tweets);
        //for fetching the tweetsID
        //        List<Integer> tweetsID =  
        for (int file : fileList) {
            HashMap<String, Float> dict = new HashMap<String, Float>();
            dict = ReadFiles.tf(ReadFiles.cutWord(file, tweets));
            allTheTf.put(file, dict);
        }
        return allTheTf;
    }

//    public static Map<String, HashMap<String, Integer>> NormalTFOfAll(HashMap<Integer, String> tweets) throws IOException {
//        List<String> fileList = ReadFiles.readDirs(dir);
//        for (int i = 0; i < fileList.size(); i++) {
//            HashMap<String, Integer> dict = new HashMap<String, Integer>();
//            dict = ReadFiles.normalTF(ReadFiles.cutWord(fileList.get(i)));
//            allTheNormalTF.put(fileList.get(i), dict);
//        }
//        return allTheNormalTF;
//    }

    public static Map<String, Float> idf(HashMap<Integer, String> tweets) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //公式IDF＝log((1+|D|)/|Dt|)，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
        HashMap<String, Float> idf = new HashMap<String, Float>();
        List<String> located = new ArrayList<String>();

        float Dt = 1;
        float D = allTheTf.size();//文档总数
        List<Integer> key = fileList;//存储各个文档名的List
        Map<Integer, HashMap<String, Float>> tfInIdf = allTheTf;//存储各个文档tf 

        for (int i = 0; i < D; i++) {
            HashMap<String, Float> temp = tfInIdf.get(key.get(i));//get(i)get the name of the file in key, and get(key(i))means find this key's value in hashmap1(file system) the value is still a hash map which is the hashmap2 of a file
            for (String word : temp.keySet()) {	//traverse the temp.keySet() 遍历此文件每个词
                Dt = 1;//calculator
                if (!(located.contains(word))) {
                    for (int k = 0; k < D; k++) {
                        if (k != i) {//不是同一文件
                            HashMap<String, Float> temp2 = tfInIdf.get(key.get(k));//get this file
                            if (temp2.keySet().contains(word)) {
                                located.add(word);
                                Dt = Dt + 1;
                                continue;
                            }
                        }
                    }
                    idf.put(word, Log.log((1 + D) / Dt, 10));
                    allTheIdf.put(word, Log.log((1 + D+1) / (Dt+1), 10)); //加一是因为这个IDF只有query会用到，而一旦用到，说明这个词包含在query中，应当加一
                }
               
            }
        }
        return idf;
    }
    
 

    public static HashMap<Integer, HashMap<String, Float>> tfidf(HashMap<Integer, String> tweets) throws IOException {
    	 HashMap<Integer, HashMap<String, Float>> tf = ReadFiles.tfOfAll(tweets);	
        Map<String, Float> idf = ReadFiles.idf(tweets);	//get idf hashmap 
      	//get tf hashmap, this is a double layer hashmap because each word has a tf value for each file

        for (int file : tf.keySet()) {	//traverse tf hashmap
            Map<String, Float> singelFile = tf.get(file);	//new a hashmap for a file
            for (String word : singelFile.keySet()) {	//traverse the word of this file(use set is consedered of the case that there might be same words)
                singelFile.put(word, (idf.get(word)) * singelFile.get(word));	//calculate tfidf and put it into the tf hashmap
            }
        }
        allTheTfIdf = tf;
        return tf;	
    }
       
    public static HashMap<Integer , Float> similarity(HashMap<Integer, String> tweets, String query) throws IOException{ 
    	//try to put query in to hashmap
//    	tweets.put(0, query);
    	
    	HashMap<Integer, HashMap<String, Float>> tfidf = new HashMap<Integer, HashMap<String, Float>>();
    	
    	if (allTheTfIdf.isEmpty()) {
    		System.out.print("allTheTfIdf is empty");
        	tfidf = ReadFiles.tfidf(tweets);
		}
        else{
        	tfidf = allTheTfIdf;
        }
    	System.out.println("test");
        
    	int tfhashSize=tfidf.size();
        List<Integer> key = fileList;//存储各个文档名的List
        HashMap<Integer, Float> similarity = new HashMap<Integer, Float>();
        
        
		HashMap<String, Float> fileQ = new HashMap<String, Float>();
		HashMap<String, Float> Qtf = new HashMap<String, Float>();
		// 创建新的问题的tfidf hashmap
		String[] wordsOfQueryStrings = ReadFiles.cutQuery(query);
		Qtf = ReadFiles.tfForQuery(wordsOfQueryStrings);
		for (String word: Qtf.keySet()) {
			if (allTheIdf.containsKey(word)) {fileQ.put(word,allTheIdf.get(word)* Qtf.get(word));		
			}
       }
        

 
        for(int i=0 ; i<tfhashSize ;i++){
        	HashMap<String, Float> fileDoc = tfidf.get(key.get(i));	// a hashmap of specific file get by i
//        	HashMap< String, Float> fileQ = tfidf.get(key.get(0));  	// a hashmap of query
        	int similarityFile = key.get(i);
        	HashMap<String, Float> NewQ = new HashMap<String, Float>();			// a hashmap to store new query
        		// a hashmap to store similarity
        	for(String word:fileDoc.keySet()){						// traverse the word in fileDoc
        			if (fileQ.containsKey(word)) {					
						NewQ.put(word, fileDoc.get(word));			// put the word in key and the tfidf in the value
					} else {
						NewQ.put(word, (float) 0.0);				// put the work in key and 0.0 in value
					}
			}
			double dotProduct = 0.0;
			double magnitude1 = 0.0;
			double magnitude2 = 0.0;
			double cosineSimilarity = 0.0;
			for (String word : fileDoc.keySet()) {

				dotProduct += fileDoc.get(word) * NewQ.get(word); // a.b
				magnitude1 += Math.pow(fileDoc.get(word), 2); // (a^2)
				magnitude2 += Math.pow(NewQ.get(word), 2); // (b^2)
			}
			magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
			magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)
			if (magnitude1 != 0.0 && magnitude2 != 0.0) {
				cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
				similarity.put(similarityFile, (float) cosineSimilarity);
			}
			// ranking documents according to similarity >>>added by Emin<<<
			similarity = sortByComparator(similarity, false);

		}
		return similarity;
        
        
        
        }
        //end of similarity
    
    // Sort the HashMap of similarities in descending order "RANK OF DOCUMENTS IN RELATION TO QUERY"
    public static HashMap<Integer, Float> sortByComparator(HashMap<Integer, Float> unsortMap, final boolean order)
    {

        List<Entry<Integer, Float>> list = new LinkedList<Entry<Integer, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, Float>>()
        {
            public int compare(Entry<Integer, Float> o1,
                    Entry<Integer, Float> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
        
        for (Entry<Integer, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    
    public static  HashMap<String, Float>  queryExpansion(HashMap<Integer, String> tweets, HashMap<Integer, Float> similarity ) throws IOException {
		List<String[]> tweetsContentList = new ArrayList<String[]>();
		HashMap<String, Float> wordAndDf = new HashMap<String, Float>();
		//traverse the similarity to get the tweets of a relevent
    	for(Integer tweetsID: similarity.keySet()){
    	tweetsContentList.add(cutQuery(tweets.get(tweetsID)));	
    	}
    	//traverse these tweets and find their words' DF
    	for(String[] Content: tweetsContentList){
    		for(String word: Content){
    			
    			if (allTheDf.get(word)==null) {
					continue;
				}
    			wordAndDf.put(word, allTheDf.get(word));
    			
    			wordAndDf = sortByComparatorQE(wordAndDf, false);
    		}
    	}
    	
    	for (String word: wordAndDf.keySet()) {
			System.out.println(word+"\n");
			System.out.println(wordAndDf.get(word));
		}
    	
    	
    	
    	
    	return wordAndDf;
		
	}
        
    
    public static HashMap<String, Float> sortByComparatorQE(HashMap<String, Float> unsortMap, final boolean order)
    {
    	
        List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Float>>()
        {
            public int compare(Entry<String, Float> o1,
                    Entry<String, Float> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        
        for (Entry<String, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }
    
    public static HashMap<String, Float> df(HashMap<Integer, String> tweets)
    {
    	HashMap<String, Float> df = new HashMap<String, Float>();
        List<String> located = new ArrayList<String>();
        
        float Dt = 1;
        float D = allTheTf.size();
        List<Integer> key = fileList;
        Map<Integer, HashMap<String, Float>> tfInIdf = allTheTf;

        for (int i = 0; i < D; i++) 
        {
            HashMap<String, Float> temp = tfInIdf.get(key.get(i));
            
            for (String word : temp.keySet()) 
            {	
                Dt = 1; //calculator
                
                if (!(located.contains(word))) 
                {
                    for (int k = 0; k < D; k++) 
                    {
                        if (k != i) 
                        {
                            HashMap<String, Float> temp2 = tfInIdf.get(key.get(k)); //get this file
                            if (temp2.keySet().contains(word)) 
                            {
                                located.add(word);
                                Dt = Dt + 1;
                                continue;
                            }
                        }
                    }
                    df.put(word, Dt);
//                    System.out.print(df.get(word));
//                    System.out.print(word);
                }
            }
        }
        allTheDf = df;
        return df;	     
    }
    
    }