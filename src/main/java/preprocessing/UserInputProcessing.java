package preprocessing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import mainApp.TweetController;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Mikhail on 19.10.2014.
 */
public class UserInputProcessing {
    static JLanguageTool langTool;

    static {
        try {
            langTool = new JLanguageTool(new AmericanEnglish());
            langTool.activateDefaultPatternRules();
        }
        catch (Exception e) {
        }
    }

    public static String parsing(String query) {
        query = cleanString(query); // Make some preprocessing
        if (query.equals("")) throw new IllegalArgumentException(); // User input can be empty after normalization

        query = tokenize(query);
        query = TweetController.tagger.tagString(query).trim();

        return query;
    }

    public static String cleanString(String rawString) { // Clean string from unnecessary symbols
        String rawText = rawString.replaceAll("\\n", " "); // Replace all line breaks with spaces
        String[] oldArray = rawText.split(" "); // Split string in array

        String newString = ""; // Processed string

        for (String s: oldArray) {
            if (!s.contains("@") && !s.contains("http") && !s.equals("") && !s.equals("RT")) { // Don't need to save words with usernames, links, retweet mark or spend resources on empty instances
                String resultString = s.replaceAll("[^\\p{L}']+", ""); // Remove all non-latin characters and digits and save apostrophes
                resultString = resultString.trim();
                resultString = splitCamelCase(resultString); // Split camel style word to different words

                if (resultString.contains(" '")) { // Try to restore possessive pronoun
                    int i = resultString.indexOf(" '");
                    resultString = resultString.substring(0, i) + resultString.substring(i+1);
                }

                if (!resultString.equals("") && !resultString.equals(" ")) {
                    newString += resultString + " "; // Add information to the processed string
                }
            }
        }

        newString = spellCheck(newString); // Check correctness of user input
        newString = newString.toLowerCase(); // Force all letters to the lower case
        newString = newString.trim(); // Remove lead and end spaces

        return newString;
    }

    public static String splitCamelCase(String camelString) { // Split camel style word to different words
        return camelString.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z],[a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static String spellCheck(String unCheckedString) {

        boolean needRepeat = false;
        List<RuleMatch> matches;
        String s = unCheckedString;

        do {
            try {
                matches = langTool.check(s);

                for (RuleMatch match : matches) {

                    List<String> ls = match.getSuggestedReplacements();

                    int begin = match.getFromPos();
                    int end = match.getToPos();

                    if (ls.size() == 1 && match.getMessage().contains("spelling mistake found")) {
                        if (ls.get(0).equals(s.substring(begin, end))) break;
                        s = s.substring(0, begin) + ls.get(0).trim() + " " + s.substring(end + 1);
                        needRepeat = true;
                        break;
                    } else {
                        needRepeat = false;
                    }
                }
                if (matches.size() == 0) needRepeat = false;
            } catch (IOException e) {
            }
        }
        while (needRepeat);

        return s;
    }

    public static String tokenize(String s) {
        StringReader sr = new StringReader(s);

        String tokenizedString = "";

        PTBTokenizer ptbt = new PTBTokenizer(sr, new CoreLabelTokenFactory(), "");
        for (CoreLabel label; ptbt.hasNext(); ) {
            label = (CoreLabel) ptbt.next();
            tokenizedString += label.toString() + " ";
        }

        tokenizedString = tokenizedString.trim();
        return tokenizedString;
    }

    public static void main(String[] args) throws IOException {
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //String s = br.readLine();
    	String s= "test tets ets etjhj";
        System.out.println(parsing(s));
    }
}