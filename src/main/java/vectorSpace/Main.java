package vectorSpace;

import java.io.IOException;
import java.util.HashMap;

import database.MySqlConnection;

public class Main {

    public static  HashMap<Integer, Float> cosineSimilarity(String normorizedQuery) throws IOException {

        String query = normorizedQuery;
        
        MySqlConnection dbConnection = new MySqlConnection();
        HashMap<Integer, String> map = dbConnection.mySqlConnection();
        HashMap<Integer, Float> similarity = ReadFiles.similarity(map, query);
        for (int word : similarity.keySet()) {
            System.out.println("Document " + word + "'s similarity with query is " + similarity.get(word));
        }
        return similarity;
//    
        
       
    }
    public static void main(String[] args) throws IOException {
	    Main.cosineSimilarity("people_NNS");
    }
}