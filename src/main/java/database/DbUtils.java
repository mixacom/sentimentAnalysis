package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
	public static String getTweetContentById(int id) {
		String tweetContent = "Not available";
		
		MySqlConnection dbConnection = new MySqlConnection();
		Statement statement;
        try {
	        statement = dbConnection.getConnection().createStatement();
        
			ResultSet resultSet = statement.executeQuery("SELECT original_text FROM tweet_info WHERE id="+id);
			
			while (resultSet.next()) {
				tweetContent = resultSet.getString(1);
			}
        } catch (SQLException e) {
	        e.printStackTrace();
        }
    
		return tweetContent;
	}
}
