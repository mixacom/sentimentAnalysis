package database;

import java.sql.*;
import java.util.HashMap;

public class MySqlConnection {
	
	Connection connection;
	public final static int LIMIT = 15000;
	public MySqlConnection() {
		try {
	        Class.forName("com.mysql.jdbc.Driver");
		// Class.forName("org.gjt.mm.mysql.Driver");
		
		this.connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/opinius?useUnicode=true&characterEncoding=utf8", "root", "");
//		StackTraceElement[] stackTraceElements =Thread.currentThread().getStackTrace();
//		for (StackTraceElement el : stackTraceElements) {
//			System.out.println(el.toString());
//		}
		System.out.println("Database connection made");
		//System.out.println("Database connection is made");
		} catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	public HashMap<Integer , String> mySqlConnection() {
		HashMap<Integer, String> tweets = new HashMap<Integer, String>();
		
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from tweet_info LIMIT "+LIMIT);
			
			//get tweets from database and put into 
			while (resultSet.next()) {
				tweets.put(resultSet.getInt(11), resultSet.getString(8));
			}
			
		} catch(SQLException e) {
			System.out.println("SQLException while making the HashMap");
			e.printStackTrace();
		}
		return tweets;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void closeConnection() {
		try {
	        connection.close();
	        System.out.println("Database connection is closed");
	    } catch (SQLException e) {
	        e.printStackTrace();
        }
	}
}
