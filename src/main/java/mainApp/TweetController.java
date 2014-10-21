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
import vectorSpace.ReadFiles;


@RestController
public class TweetController {

	public static final MaxentTagger tagger = new MaxentTagger("res/gate-EN-twitter.model"); // should be in memory
	public final Main vectorSpace = new Main();
	
	@RequestMapping("/")
    public String init(@RequestParam(value="query", required=false) String q, @RequestParam(value="type", required=false) String type) {
		String normolizedQuery = q;
		if (q != "" && q != null) {
			Stemmer stemmer = new Stemmer();
			normolizedQuery = stemmer.normalization(preprocessQuery(q));
		}
		if (q == null) {
			String fileContent = readFile("webFrondEnd/mainPage.html", new String[]{"", "checked", "", "", "", "", "", "", ""});
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
		String queryExpantion = "";
		try {
			 HashMap<Integer, Float> similarityMap = vectorSpace.cosineSimilarity(normolizedQuery);
			 for (int id : similarityMap.keySet()) {
				 relavantTweets.add(new Tweet(DbUtils.getTweetContentById("original_text", id), "Cosine similarity score: " + similarityMap.get(id), id));
		     }
			 queryExpantion = addingQueryExpantion(similarityMap, "positive", query);
        } catch (IOException e) {
	         e.printStackTrace();
        }
		
		
		ArrayList<ArrayList<Tweet>> tweetsBySentiment = DbUtils.getTweetsBySentiment(relavantTweets);
		ArrayList<Tweet> positiveTweets = tweetsBySentiment.get(2);
		ArrayList<Tweet> neutralTweets = tweetsBySentiment.get(1);
		ArrayList<Tweet> negativeTweets = tweetsBySentiment.get(0);
		int totalRelevantTweets = positiveTweets.size() + neutralTweets.size() + negativeTweets.size();
		
		String posTweetHTML = "";
		for (Tweet tweet: positiveTweets) {
			posTweetHTML += 
					"<tr>" +
						"<td>" +	
								"<table class='singleResult'>" +
									"<tr>" + 
										"<td class='user'>" +
										tweet.getUser() + 
										"</td><td class='date'>" +
										"Tweet-id" + tweet.getId() + "</td>" +
									"</tr><tr class='content'><td>" +
										tweet.getContent() + "</td>" +
									"</tr>" +
								"</table>" +
						"</td>" +
					"</tr>"; 
		}
		
		String neuTweetHTML = "";
		int num = 0;
		for (Tweet tweet: neutralTweets) {
			if (num > 9) {
				break;
			}
			num++;
			neuTweetHTML += 
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
				queryExpantion,
				"" + totalRelevantTweets,
				"" + positiveTweets.size(),
				"" + negativeTweets.size(),
				"<tr><th><h2>Positive tweets about: '"+ query +"'</h2></th></tr>" + posTweetHTML,
				"<tr><th><h2>Top 10 relevant neutral tweets about: '"+ query +"'</h2></th></tr>" + neuTweetHTML
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
		ArrayList<Tweet> relavantTweets = new ArrayList<Tweet>();
		String queryExpantion = "";
		try {
			 HashMap<Integer, Float> similarityMap = vectorSpace.cosineSimilarity(normolizedQuery);
			 for (int id : similarityMap.keySet()) {
				 relavantTweets.add(new Tweet(DbUtils.getTweetContentById("original_text", id), "Cosine similarity score: " + similarityMap.get(id), id));
		     }
			 queryExpantion = addingQueryExpantion(similarityMap, "negative", query);
        } catch (IOException e) {
	        e.printStackTrace();
        }
		
		ArrayList<ArrayList<Tweet>> tweetsBySentiment = DbUtils.getTweetsBySentiment(relavantTweets);
		ArrayList<Tweet> positiveTweets = tweetsBySentiment.get(2);
		ArrayList<Tweet> neutralTweets = tweetsBySentiment.get(1);
		ArrayList<Tweet> negativeTweets = tweetsBySentiment.get(0);
		int totalRelevantTweets = positiveTweets.size() + neutralTweets.size() + negativeTweets.size();
		
		String negTweetHTML = "";
		for (Tweet tweet: negativeTweets) {
			negTweetHTML += 
					"<tr>" +
						"<td>" +	
								"<table class='singleResult'>" +
									"<tr>" + 
										"<td class='user'>" +
										tweet.getUser() + 
										"</td><td class='date'>" +
										"Tweet-id" + tweet.getId() + "</td>" +
									"</tr><tr class='content'><td>" +
										tweet.getContent() + "</td>" +
									"</tr>" +
								"</table>" +
						"</td>" +
					"</tr>"; 
		}
		
		String neuTweetHTML = "";
		int num = 0;
		for (Tweet tweet: neutralTweets) {
			if (num > 9) {
				break;
			}
			num++;
			neuTweetHTML += 
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
				"", 
				"checked",
				queryExpantion,
				"" + totalRelevantTweets,
				"" + positiveTweets.size(),
				"" + negativeTweets.size(),
				"<tr><th><h2>Negative tweets about: '"+ query +"'</h2></th></tr>" + negTweetHTML,
				"<tr><th><h2>Top 10 relevant neutral tweets about: '"+ query +"'</h2></th></tr>" + neuTweetHTML
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
	}
	
	
	private String addingQueryExpantion(HashMap<Integer, Float> similarityMap, String posOrNeg, String query) {
		String qExp = "";
		try {
			HashMap<String, Float> expantionWordsMap = ReadFiles.queryExpansion(vectorSpace.getTweetsMap(), similarityMap);
			int number = 0;
			for (String expantionWords : expantionWordsMap.keySet()) {
				if (number > 5) {
					break;
				}
				number++;
				String subStringWord = expantionWords.substring(0, expantionWords.indexOf("_"));
				qExp += 
						
							"<p>" +
								"<a href='http://localhost:8080/?query=" + query + " "+subStringWord +"&type="+ posOrNeg +"'>"+subStringWord+"</a>" +
							"</p>";
			}
        } catch (IOException e) {
	        e.printStackTrace();
        }
		return qExp;
	}
	
	private String preprocessQuery(String query) {
		// Preprocessing
		String preProcessedQuery = UserInputProcessing.parsing(query);
		
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
