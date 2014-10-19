package languageDetection;

import java.util.List;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;

/*
 * Created by Emin 10.12.2014
 */

public class LanguageDetect {		
	
	public boolean checkEnglish(String query) throws APIError {
		boolean isEnglish= false;
		DetectLanguage.apiKey = "9b6e45d09178214b2e3779868da2c967";
		List<Result> results = DetectLanguage.detect(query);
		
		// Check if API could detect the language
		if (!results.isEmpty()) {
			Result result = results.get(0);
			
			// Check language of tweet
			if (result.language.equals("en")) {	
				isEnglish = true;
			}
		}
		return isEnglish;
	}
	
	public static void main(String[] args) {
	   LanguageDetect test = new LanguageDetect();
		try {
	        System.out.println(test.checkEnglish("Is dit engels?"));
        } catch (APIError e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }
}