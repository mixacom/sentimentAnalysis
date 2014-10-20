package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import mainApp.Tweet;

public class DbUtils {
	public static String getTweetContentById(String content, int id) {
		String tweetContent = "Not available";
		
		MySqlConnection dbConnection = new MySqlConnection();
		Statement statement;
        try {
	        statement = dbConnection.getConnection().createStatement();
        
			ResultSet resultSet = statement.executeQuery("SELECT " + content + " FROM tweet_info WHERE id="+id + " LIMIT " + dbConnection.LIMIT);
			
			while (resultSet.next()) {
				tweetContent = resultSet.getString(1);
			}
        } catch (SQLException e) {
	        e.printStackTrace();
        }
        dbConnection.closeConnection();
		return tweetContent;
	}
	
	public static ArrayList<ArrayList<Tweet>> getTweetsBySentiment(ArrayList<Tweet> tweets) {
		ArrayList<Tweet> negativeTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> neutralTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> positiveTweets = new ArrayList<Tweet>();
		
		for (Tweet tweet : tweets) {
			String classified = DbUtils.getTweetContentById("naiveBayes", tweet.getId());
			try {
				int sentimentInt = Integer.parseInt(classified);
				if (sentimentInt == 0) {
					negativeTweets.add(tweet);
				} else if (sentimentInt == 1) {
					neutralTweets.add(tweet);
				} else if (sentimentInt == 2) {
					positiveTweets.add(tweet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<ArrayList<Tweet>> result = new ArrayList<ArrayList<Tweet>>();
		result.add(negativeTweets);
		result.add(neutralTweets);
		result.add(positiveTweets);
		
		return result;
	}
}
