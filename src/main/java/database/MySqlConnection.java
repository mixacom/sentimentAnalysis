package database;

import java.sql.*;
import java.util.HashMap;

public class MySqlConnection {
	
	Connection connection;
	
	public MySqlConnection() {
		try {
	        Class.forName("com.mysql.jdbc.Driver");
		// Class.forName("org.gjt.mm.mysql.Driver");
		System.out.println("Success loading Mysql Driver!");
		
		this.connection = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/opinius?useUnicode=true&characterEncoding=utf8", "root", "");
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
			ResultSet resultSet = statement.executeQuery("select * from tweet_info LIMIT 4000");
			
			//get tweets from database and put into 
			while (resultSet.next()) {
				tweets.put(resultSet.getInt(11), resultSet.getString(8));
			}
	
			System.out.println("Success connect Mysql server!");
			connection.close();
			
		} catch(SQLException e) {
			System.out.println("SQLException while making the HashMap");
			e.printStackTrace();
		}
		return tweets;
	}
	
	public Connection getConnection() {
		return connection;
	}
}
