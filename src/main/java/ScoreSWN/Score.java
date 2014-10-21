package ScoreSWN;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import constants.Components;
import database.MySqlConnection;

public class Score {   	
private String pathToSWN = Components.getBaseFilePath() + "src/main/java/ScoreSWN/SWNdata.txt";
private HashMap<String, Double> _dict;

public Score(){

    _dict = new HashMap<String, Double>();
    HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
    try{
        BufferedReader csv =  new BufferedReader(new FileReader(pathToSWN));
        String line = "";           
        while((line = csv.readLine()) != null)
        {
            String[] data = line.split("\t");
            //System.out.println(data.length);
            
            if(data.length > 3 && !(data[0].charAt(0) == '#')) {
               // System.out.println(data[0]);
            	Double score = Double.parseDouble(data[2])-Double.parseDouble(data[3]);
                String[] words = data[4].split(" ");
                
                for(String w:words)
                {
                    String[] w_n = w.split("#");
                    w_n[0] += "#"+data[0];
                    int index = Integer.parseInt(w_n[1])-1;
                    if(_temp.containsKey(w_n[0]))
                    {
                        Vector<Double> v = _temp.get(w_n[0]);
                        if(index>v.size())
                            for(int i = v.size();i<index; i++)
                                v.add(0.0);
                        v.add(index, score);
                        _temp.put(w_n[0], v);
                    }
                    else
                    {
                        Vector<Double> v = new Vector<Double>();
                        for(int i = 0;i<index; i++)
                            v.add(0.0);
                        v.add(index, score);
                        _temp.put(w_n[0], v);
                    }
                }
            }
        }
        Set<String> temp = _temp.keySet();
        for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
            String word = (String) iterator.next();
            Vector<Double> v = _temp.get(word);
            double score = 0.0;
            double sum = 0.0;
            for(int i = 0; i < v.size(); i++)
                score += ((double)1/(double)(i+1))*v.get(i);
            for(int i = 1; i<=v.size(); i++)
                sum += (double)1/(double)i;
            score /= sum;
            _dict.put(word, score);
        }
        csv.close();
    }
    catch(Exception e){e.printStackTrace();}        
}

public Double extract(String word)
{
    Double total = new Double(0);
    if(_dict.get(word+"#n") != null)
         total = _dict.get(word+"#n") + total;
    if(_dict.get(word+"#a") != null)
        total = _dict.get(word+"#a") + total;
    if(_dict.get(word+"#r") != null)
        total = _dict.get(word+"#r") + total;
    if(_dict.get(word+"#v") != null)
        total = _dict.get(word+"#v") + total;
    return total;
}
//calculate the totalscore of the  
public double calculate(String lineTxt){

	String[] words = lineTxt.split("\\s+");
    double totalScore = 0;
    for(String word : words) {

    	word = word.substring(0, word.indexOf("_"));

        word = word.replaceAll("([^a-zA-Z\\s])", "");
        if (extract(word) == null)
            continue;
        totalScore += extract(word);
    }
    //System.out.println();
	return totalScore;
}

public static void main(String[] args) {
	Score s= new Score();
	
	MySqlConnection mySql = new MySqlConnection();
    Connection dbConnection = mySql.getConnection();
    int id;
    double score_stw;
    try {
    	Statement tweetsFromDb = dbConnection.createStatement();
		ResultSet resultSetTweets = tweetsFromDb.executeQuery("select text, id from tweet_info WHERE id>1300000");
		
		//get tweets from database and put into 
		while (resultSetTweets.next()) {
			String content = resultSetTweets.getString(1);
			score_stw = s.calculate(content);
			id = resultSetTweets.getInt(2);
			
			if ((id % 100000) == 0)
				System.out.println(new Date() + " UPDATE tweet_info SET score_stw=" + score_stw + " WHERE id=" + id + ";");
			
			Statement update = dbConnection.createStatement();
			update.executeUpdate("UPDATE tweet_info SET score_stw=" + score_stw + " WHERE id=" + id + ";");
		}
    } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    mySql.closeConnection();
//    String path = "/home/jonas/workspace_1/sentimentAnalysis/src/main/java/classification/";
//    try {
//		FileReader reader = new FileReader(path+"humanLabeled.csv");
//		BufferedReader br = new BufferedReader(reader);
//		FileWriter writer = new FileWriter(path+"humanLabeled_with_score.csv");
//		String tweet;
//		String content, classes;
//		double score;
//		while ((tweet = br.readLine()) != null) {
//			content = tweet.substring(0, tweet.length()-2);
//			classes = tweet.substring(tweet.length()-1, tweet.length());
//			score = s.calculate(content);
//			writer.write("\"" + content + "\",\"" + classes + "\",\"" + score + "\"\n");
//		}
//		writer.close();
//		br.close();
//		reader.close();
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}		
    
//	String[] ss = {"i will be stun if we beat the pack",
//			"thei did decent with meredith baxter gina t and chri mc kenna thei do have it in them",
//			"media how jeff zucker is seek to reshap cnn ",
//			"we 're both walk disast so we might as well just hold each other 's hand and be them togeth",
//			"i knew my feed wa miss someth"};
//	for (String s1 : ss)
//		System.out.println(s.calculate(s1));
}

}