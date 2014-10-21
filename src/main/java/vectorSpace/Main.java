package vectorSpace;

import java.io.IOException;
import java.util.HashMap;

import database.MySqlConnection;

public class Main {

    public final HashMap<Integer, String> tweetsMap;
    private final MySqlConnection dbConnection;
    public Main(MySqlConnection db) {
    	dbConnection = db;
    	tweetsMap = dbConnection.mySqlConnection();
    }
    
	public  HashMap<Integer, Float> cosineSimilarity(String normorizedQuery) throws IOException {
        String query = normorizedQuery;
        
        HashMap<Integer, Float> similarity = ReadFiles.similarity(tweetsMap, query);
        
        
        if (ReadFiles.allTheDf.isEmpty()) {
        	ReadFiles.df(tweetsMap);
		}
        
        HashMap<String, Float>  wordForQuery = ReadFiles.queryExpansion(tweetsMap,  similarity);
            
        return similarity;  
    }
	
	public void closeConn() {
		dbConnection.closeConnection();
	}
	
	public HashMap<Integer, String> getTweetsMap() {
		return tweetsMap;
	}
    
    
}