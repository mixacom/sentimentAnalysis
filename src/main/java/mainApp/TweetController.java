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
			String fileContent = readFile("webFrondEnd/mainPage.html", new String[]{"", "", "", "checked", ""});
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
		String[] nested = {
				"value="+query,
				"checked", 
				"", 
				"",
				"<p>Positive tweets about: '"+ query +"'</p>"
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
	}
	
	private String getNegativeTweets(String query) {
		String[] nested = {
				"value="+query,
				"", 
				"checked", 
				"",
				"<p>Negative tweets about: '"+ query +"'</p>"
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
	}
	
	private String statistics(String query) {
		String[] nested = {
				"value="+query,
				"", 
				"", 
				"checked",
				"<p>Statistics about: '"+ query +"'</p>"
		};
		String page = readFile("webFrondEnd/mainPage.html", nested);
		return page;
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
