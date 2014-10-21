package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import mainApp.Tweet;

public class DbUtils {
	private Connection c;
	public DbUtils(Connection c) {
	    this.c = c;
    }
	
	public String getTweetContentById(String content, int id) {
		String tweetContent = "Not available";
		
		Statement statement;
        try {
	        statement = c.createStatement();
        
			ResultSet resultSet = statement.executeQuery("SELECT " + content + " FROM tweet_info WHERE id="+id + " LIMIT " + MySqlConnection.LIMIT);
			
			while (resultSet.next()) {
				tweetContent = resultSet.getString(1);
			}
        } catch (SQLException e) {
	        e.printStackTrace();
        }
        return tweetContent;
	}
	
	public ArrayList<ArrayList<Tweet>> getTweetsBySentiment(ArrayList<Tweet> tweets) {
		ArrayList<Tweet> negativeTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> neutralTweets = new ArrayList<Tweet>();
		ArrayList<Tweet> positiveTweets = new ArrayList<Tweet>();
		
		for (Tweet tweet : tweets) {
			String classified = this.getTweetContentById("final_res", tweet.getId());
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
