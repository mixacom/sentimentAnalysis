package classification;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import mainApp.Tweet;
import mainApp.Tweet.sentimentClass;
import ScoreSWN.Score;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DecissionTreeClassification {
	public final ArrayList<Tweet> tweets;
	
	
	public DecissionTreeClassification(ArrayList<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	private SplitCondition getBestSplitForScore2() {
		SplitCondition result = new SplitCondition(0.0, 0.0, new int[][]{{1,1,1},{1,1,1},{1,1,1}});
		
		for (double lower=0; lower<=0; lower+=0.1) {
			for (double upper=0.9; upper<=0.9; upper+=0.1) {
				System.out.println(lower + " " + upper);
				int[][] errorMatrix = new int[3][3];
				for (Tweet tweet : tweets) {
					if (tweet.getScore() < lower) {
						if(tweet.getSentimentClass().equals(sentimentClass.NEGATIVE)) {
							errorMatrix[0][0]++;
						} else if(tweet.getSentimentClass().equals(sentimentClass.NEUTRAL)) {
							errorMatrix[1][0]++;
						} else {
							errorMatrix[2][0]++;
						}
					} else if (tweet.getScore() > upper) {
						if(tweet.getSentimentClass().equals(sentimentClass.NEGATIVE)) {
							errorMatrix[0][2]++;
						} else if(tweet.getSentimentClass().equals(sentimentClass.NEUTRAL)) {
							errorMatrix[1][2]++;
						} else {
							errorMatrix[2][2]++;
						}
					} else { // Neutral
						if(tweet.getSentimentClass().equals(sentimentClass.NEGATIVE)) {
							errorMatrix[0][1]++;
						} else if(tweet.getSentimentClass().equals(sentimentClass.NEUTRAL)) {
							errorMatrix[1][1]++;
						} else {
							errorMatrix[2][1]++;
						}
					}
				}
				SplitCondition current = new SplitCondition(lower, upper, errorMatrix);
				double error = current.getError();
				if (error < result.getError()) {
					result = current;
				}
				for (int i=0; i<errorMatrix.length; i++) {
					for (int j=0; j<errorMatrix[i].length; j++) {
						System.out.print(errorMatrix[i][j] + " ");
					}
					System.out.println();
				}
				System.out.println("Error: " + error);
				System.out.println();
				System.out.println("Progress: " + current.getLower() + ", " + current.getUpper());
			}
		}
		return result;
	}
	
	
	
	
	public static void main(String[] args) {
		try {
	        ArrayList<Tweet> annotatedSet = readTweetsFromCSV("/Users/wilcovanleeuwen/Github/sentimentAnalysis/src/main/java/backEnd/testSetManyWP.csv");
	        ArrayList<Tweet> posAnnotated = new ArrayList<Tweet>();
	        ArrayList<Tweet> negAnnotated = new ArrayList<Tweet>();
	        ArrayList<Tweet> neuAnnotated = new ArrayList<Tweet>();
	        
	        ArrayList<Tweet> testSet = new ArrayList<Tweet>();
	        ArrayList<Tweet> verificationSet = new ArrayList<Tweet>();
	        
	       for (int i=0 ; i<annotatedSet.size(); i++) {
				if (annotatedSet.get(i).getSentimentClass() == sentimentClass.POSITIVE) {
					posAnnotated.add(annotatedSet.get(i));
				} else if (annotatedSet.get(i).getSentimentClass() == sentimentClass.NEUTRAL) {
					neuAnnotated.add(annotatedSet.get(i));
				} else if (annotatedSet.get(i).getSentimentClass() == sentimentClass.NEGATIVE) {
					negAnnotated.add(annotatedSet.get(i));
				}
			}
			
			
			for (int i=0; i< 33; i++) {
				testSet.add(posAnnotated.get(i));
				testSet.add(neuAnnotated.get(i));
				testSet.add(negAnnotated.get(i));
			}
			
			for (int i=33; i< 99; i++) {
				verificationSet.add(posAnnotated.get(i));
				verificationSet.add(neuAnnotated.get(i));
				verificationSet.add(negAnnotated.get(i));
			}
			
			for (Tweet t : testSet) {
				System.out.println(t.getContent());
			}
			//DecissionTreeClassification dt = new DecissionTreeClassification(testSet);
			//SplitCondition sc = dt.getBestSplitForScore2();
			
	       //System.out.println("lower: " + sc.getLower() + "; upper: " + sc.getUpper() + "; error: " + sc.getError());
        } catch (IOException e) {
	        e.printStackTrace();
        }
		
		
//		try {
//	        writeScore("/Users/wilcovanleeuwen/Github/sentimentAnalysis/src/main/java/backEnd/testSetManyWithPoints.csv", "/Users/wilcovanleeuwen/Github/sentimentAnalysis/src/main/java/backEnd/testSetMany.csv");
//        } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
	}
	
	private static ArrayList<Tweet> readTweetsFromCSV(String fileName) throws IOException {
		ArrayList<Tweet> testSet = new ArrayList<Tweet>();
		CSVReader testSetFile = new CSVReader(new FileReader(fileName), ',' , '"' , 0);
	      
	
		//Read CSV line by line and use the string array as you want
		String[] nextLine;
		while ((nextLine = testSetFile.readNext()) != null) {
			if (nextLine[0] != null) {
				Tweet tweet = new Tweet(nextLine[0], "", "");
				if (nextLine[1].equals("3")) {
					tweet.setSentimentClass(sentimentClass.POSITIVE);
				} else if (nextLine[1].equals("2")) {
					tweet.setSentimentClass(sentimentClass.NEUTRAL);
				} else if (nextLine[1].equals("1")){
					tweet.setSentimentClass(sentimentClass.NEGATIVE);
				}
				tweet.setScore(Double.parseDouble(nextLine[2]));
				testSet.add(tweet);
			}
		 }
		testSetFile.close();
		return testSet;
	}
	
	private static void writeScore(String fileNameWrite, String fileNameRead) throws IOException {
		CSVReader testSetFile = new CSVReader(new FileReader(fileNameRead), ',' , '"' , 0);
		CSVWriter csvWriter = new CSVWriter(new FileWriter(fileNameWrite));
	      
		//Read CSV line by line and use the string array as you want
		String[] nextLine;
		while ((nextLine = testSetFile.readNext()) != null) {
			if (nextLine[0] != null) {

				double score = Score.calculate(nextLine[0]);
				String[] write = new String[nextLine.length + 1];
				for (int j=0; j<nextLine.length; j++) {
					write[j] = nextLine[j];
				}
				write[nextLine.length] = "" + score;
				for (String e : write) {
					System.out.print(e + " ");
				}
				System.out.println();
				csvWriter.writeNext(write);
			}
		 }
		testSetFile.close();
		csvWriter.close();
	}
	
}
