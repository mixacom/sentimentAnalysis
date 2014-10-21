package classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import constants.Components;
import database.MySqlConnection;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class SentimentAnalysisUsingNaiveBayes {
	private FastVector attributeList;
	private HashMap<String, Integer> featureMap;
	private FastVector sentimentClassList;
	private final String rootpath = Components.getBaseFilePath() + "src/main/java/classification/";
	private NaiveBayes classifier;
	private int trainingDataSize;
	private Instances trainingInstances;
	
	private void buildAttributeList(final String featureword_file) {
		try {
			FileReader input = new FileReader(featureword_file);
			BufferedReader buffer = new BufferedReader(input);
			String wd;
			attributeList = new FastVector(); // read all feature word building feature vector
			featureMap = new HashMap<String, Integer>();
			int n = 0;
			while ((wd = buffer.readLine()) != null) {
				if (!featureMap.containsKey(wd)) {
					attributeList.addElement(new Attribute(wd));
					featureMap.put(wd, n++);
				}
			}
			// add classes
			sentimentClassList = new FastVector(3);
			sentimentClassList.addElement("Negative");
			sentimentClassList.addElement("Neutral");
			sentimentClassList.addElement("Positive");
			attributeList.addElement(new Attribute("Sentiment", sentimentClassList));
			
			buffer.close();
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SentimentAnalysisUsingNaiveBayes(final int dataSize) {
		buildAttributeList(rootpath + "Feature_Map_8309.csv");
		trainingDataSize = dataSize;
	}
	
	private Instance extractFeature(String tweet) {
		String[] tokens = tweet.split(" ");
		Map<Integer, Double> featureInTweet = new HashMap<Integer, Double>();
		int idx;
		for (String token : tokens) {
			if (featureMap.containsKey(token)) {
				idx = featureMap.get(token);
				if (!featureInTweet.containsKey(idx)) {
					featureInTweet.put(idx, 1.0);
				} else {
					featureInTweet.put(idx, featureInTweet.get(idx) + 1.0);
				}
			}
		}
		int[] indices = new int[featureInTweet.size()+1];
		double[] values = new double[featureInTweet.size()+1];
		int i=0;
		//System.out.println("feature word in tweet: " + featureInTweet.size());
        for(Map.Entry<Integer,Double> entry : featureInTweet.entrySet())
        {
        	//System.out.println("get in?");
            indices[i] = entry.getKey();
            values[i] = entry.getValue();
            //System.out.println(indices[i] + " " + values[i]);
            i++;
        }
        indices[i] = featureMap.size();
        values[i] = (double)(Double.parseDouble(tokens[tokens.length-1]) - 1);
		return new SparseInstance(1.0,values,indices,featureMap.size()+1);
	}
	
	public void trainClassifier(final String input_file) {
		FileReader input = null;
		try {
			input = new FileReader(rootpath + input_file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader buffer = new BufferedReader(input);
		String tweet;
		//trainingInstances consists of feature vector of every input
        trainingInstances = new Instances("traning_dataset",attributeList,0);
        trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
        int i = 0;

        try {
        	for (; i < trainingDataSize; i++) {
        		tweet = buffer.readLine();
			    //extractFeature method returns the feature vector for the current input
			    Instance tweetFeatureVector = extractFeature(tweet);
			    //Make the currentFeatureVector to be added to the trainingInstances
			    tweetFeatureVector.setDataset(trainingInstances);
			    trainingInstances.add(tweetFeatureVector);
			}
        	
			buffer.close();
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	    //You can create the classifier that you want. In this tutorial we use NaiveBayes Classifier
	    //For instance classifier = new SMO;
	    classifier = new NaiveBayes();
	    //System.out.println(trainingInstances.numAttributes() + " " + trainingInstances.numInstances());
	    //System.out.println(trainingInstances.instance(0));
	    try {
	        //classifier training code
	        classifier.buildClassifier(trainingInstances);
	    } catch (Exception ex) {
	    	System.out.println("Exception in training the classifier.");
	        ex.printStackTrace();
	    }
	}
	
	public int classify(String rawTweet) {
		Instance tweetFeatureVector = extractFeature(rawTweet + " -2");
		tweetFeatureVector.setDataset(trainingInstances);
		int res = -1;
	    try {
	        res = (int) classifier.classifyInstance(tweetFeatureVector);
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	    return res;
	}
	
	
	// for the test
	public void Display(int value) {
		//System.out.println(fea tureMap.toString());
		//System.out.println(sentimentClassList.elementAt(1));
		if (value >= 0 && value < 3)
			System.out.println(trainingInstances.attribute("Sentiment").value(value));
		else
			System.out.println("WRONG");
	}
	
	public static void main(String[] args) {
	    SentimentAnalysisUsingNaiveBayes naiveBayes = new SentimentAnalysisUsingNaiveBayes(1000);
	    naiveBayes.trainClassifier("humanLabeled.csv");
		
		MySqlConnection mySql = new MySqlConnection();
	    Connection dbConnection = mySql.getConnection();
        int classified, id;
	    try {
        	Statement tweetsFromDb = dbConnection.createStatement();
			ResultSet resultSetTweets = tweetsFromDb.executeQuery("select text, id from tweet_info WHERE id > 333157 LIMIT " + mySql.LIMIT);
			
			//get tweets from database and put into 
			while (resultSetTweets.next()) {
				String content = resultSetTweets.getString(1);
				classified = naiveBayes.classify(content);
				id = resultSetTweets.getInt(2);
				
				System.out.println("UPDATE tweet_info SET naiveBayes=" + classified + " WHERE id=" + id + ";");
				
				Statement update = dbConnection.createStatement();
				update.executeUpdate("UPDATE tweet_info SET naiveBayes=" + classified + " WHERE id=" + id + ";");
			}
        } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        mySql.closeConnection();

    }
}