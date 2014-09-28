package mainApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

	@RequestMapping("/")
    public String init(@RequestParam(value="query", required=false) String q, @RequestParam(value="type", required=false) String type) {
        if (q == null) {
			String fileContent = readFile("webFrondEnd/mainPage.html");
			return fileContent;
        } else {
        	if (type.equals("positive")) {
        		return getPositiveTweets(q);
        	} else if (type.equals("negative")) {
        		return getNegativeTweets(q);
        	} else {
        		return statistics(q);
        	}
        }
    }
	
	
	private String getPositiveTweets(String query) {
		return "<html><body><h1>Positive sentiment tweets about: " + query + "</h1></body></html>";
	}
	
	private String getNegativeTweets(String query) {
		return "<html><body><h1>Negative sentiment tweets about: " + query + "</h1></body></html>";
	}
	
	private String statistics(String query) {
		return "<html><body><h1>Sentiment analyzis of tweets about: " + query + "</h1></body></html>";
	}
	
	private String readFile(String file) {
		String result = "";
        
        try {
        	BufferedReader bf = new BufferedReader(new FileReader(new File("src/main/java/" + file)));
	        String line;
	        while((line = bf.readLine()) != null) {
	        	result += line.toString();
	        }
	        bf.close();
        } catch (FileNotFoundException e) {
	        result = "<html><body><h1>Error while reading html mainpage</h1></body></html>";
        } catch (IOException e) {
        	result = "<html><body><h1>Error while closing the stream of the mainpage</h1></body></html>";
        }
        
        return result;
	}
}
