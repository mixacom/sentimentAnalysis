package mainApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import normalization.Stemmer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import database.DbUtils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import preprocessing.UserInputProcessing;
import vectorSpace.Main;


@RestController
public class TweetController {

	public static final MaxentTagger tagger = new MaxentTagger("res/gate-EN-twitter.model"); // should be in memory
	
	@RequestMapping("/")
    public String init(@RequestParam(value="query", required=false) String q, @RequestParam(value="type", required=false) String type) {
		String normolizedQuery = q;
		if (q != "" && q != null) {
			Stemmer stemmer = new Stemmer();
			normolizedQuery = stemmer.normalization(preprocessQuery(q));
		}
		if (q == null) {
			String fileContent = readFile("webFrondEnd/mainPage.html", new String[]{"", "checked", "", ""});
			return fileContent;
        } else {
        	if (type.equals("positive")) {
        		return getPositiveTweets(normolizedQuery, q);
        	} else {
        		return getNegativeTweets(normolizedQuery, q);
        	}
        }
    }
	
	
	/**
	 * Call-back method for positive tweets
	 * @param query The query from the user
	 * @return A string containing the HTML-code for the resulting page, which shows the most positive tweets related to the query
	 */
	private String getPositiveTweets(String normolizedQuery, String query) {
		ArrayList<Tweet> relavantTweets = new ArrayList<Tweet>();
		try {
			 HashMap<Integer, Float> similarityMap = Main.cosineSimilarity(normolizedQuery);
			 for (int id : similarityMap.keySet()) {
				 relavantTweets.add(new Tweet(DbUtils.getTweetContentById("original_text", id), "score: " + similarityMap.get(id), id));
		     }
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		//Tweet[] relavantTweets = {new Tweet(query, "user", "date")};//{new Tweet("phone calls from my Nonna are the best always when I need them most she calls", "RomanNegrette", "Mon Sep 29"), new Tweet("RT phone calls from my Nonna are the best always when I need them most she calls", "AshSoto105", "Mon Sep 29"), new Tweet("inlove with Sam smiths station on pandora", "stillakid99_", "Mon Sep 29"), new Tweet("I so badly want to have a normal conversation with you again", "kaitlynann2597", "Mon Sep 29")};
		//relavantTweets.add(new Tweet("","",""));
		// Show the tweets in html code
		// TODO: Refactor the view-components out of this controller
		
		ArrayList<ArrayList<Tweet>> tweetsBySentiment = DbUtils.getTweetsBySentiment(relavantTweets);
		ArrayList<Tweet> positiveTweets = tweetsBySentiment.get(2);
		ArrayList<Tweet> neutralTweets = tweetsBySentiment.get(1);
		ArrayList<Tweet> negativeTweets = tweetsBySentiment.get(0);
		
		String tweetHTML = "";
		for (Tweet tweet: positiveTweets) {
			tweetHTML += 
					"<tr>" +
						"<td>" +	
								"<table class='singleResult'>" +
									"<tr>" + 
										"<td class='user'>" +
										tweet.getUser() + 
										"</td><td class='date'>" +
										tweet.getId() + "</td>" +
									"</tr><tr class='content'><td>" +
										tweet.getContent() + "</td>" +
									"</tr>" +
								"</table>" +
						"</td>" +
					"</tr>"; 
		}
		
		// Insert the relevant components into the html
		String[] nested = {
				"value='"+query+"'",
				"checked", 
				"", 
				"<tr><th><h2>Positive tweets about: '"+ query +"'</h2></th></tr>" + tweetHTML
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
	}
	
	/**
	 * Call-back method for negative tweets
	 * @param query The query from the user
	 * @return A string containing the HTML-code for the resulting page, which shows the most negative tweets related to the query
	 */
	private String getNegativeTweets(String normolizedQuery, String query) {
		String[] nested = {
				"value="+query,
				"", 
				"checked", 
				"<p>Negative tweets about: '"+ query +"'</p>"
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
	}
	
	private String preprocessQuery(String query) {
		// Preprocessing
		String preProcessedQuery = UserInputProcessing.parsing(query);
		
		// Stemmer
		//Stemmer s = new Stemmer();
		//String stemmedQuery = s.normalization(preProcessedQuery);
		
		return preProcessedQuery;//stemmedQuery;
	}
	
	private String readFile(String file, String[] args) {
		String result = "";
        int arg = 0;
        try {
        	BufferedReader bf = new BufferedReader(new FileReader(new File("src/main/java/" + file)));
	        String line;
	        while((line = bf.readLine()) != null) {
	        	if (line.contains("[#nested]")) {
	        		result += replaceNestedContent(args[arg], line);
	        		arg++;	
	        	} else {
	        		result += line;
	        	}
	        }
	        bf.close();
        } catch (FileNotFoundException e) {
	        result = "<html><body><h1>Error while reading html mainpage</h1></body></html>";
        } catch (IOException e) {
        	result = "<html><body><h1>Error while closing the stream of the mainpage</h1></body></html>";
        }
        
        return result;
	}
	
	/**
	 * Replace the {#nested]-block with the actual content
	 * Method is public for testing
	 * @param content The actual content
	 * @param line The line containing the [#nested]-block
	 * @return The whole line with the content inserted at the [#nested]-block
	 */
	public String replaceNestedContent(String content, String line) {
		String result = "";
		
		int pos = line.indexOf("[#nested]");
		result += line.substring(0, pos);
		result += content;
		result += line.substring(pos+9, line.length());
		
		return result;
	}
}
