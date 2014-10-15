import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.*;
import java.util.*;


/**
 * Created by Mikhail on 30.09.2014.
 */
public class DataPreProcessing {

    static final MaxentTagger tagger = new MaxentTagger("taggers/gate-EN-twitter.model");
    static JLanguageTool langTool;

    static BufferedWriter bfm;
    static Map<String, Integer> attributesbfm;

    static
    {
        try {
            bfm = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\bfm.txt")));
            attributesbfm = new HashMap<>();
        }
        catch (Exception e) {

        }
    }


    static {
        try {
            langTool  = new JLanguageTool(new AmericanEnglish());
            langTool.activateDefaultPatternRules();
        } catch (Exception e) {

        } // end try-catch
    } // end static init block

    public static void main(String[] args) throws IOException{
        BufferedReader br = null;
        try {

            String filePath     = "C:\\Data\\";                   // Path to the folder
            String fileRead     = "*";                            // File to read
            String fileWrite    = "Big_Feature_Map.txt";          // File to write

            String[] files;
            if (fileRead.equals("*")) {
                files = dirFiles(filePath);
            }
            else {
                files = new String[1];
                files[0] = fileRead;
            }

//            System.out.println(stemmer(new StringReader("Very Proud and really motivating video about Indians Innovation Not quite sure about me, but YES i will try to be parts of it")));

            for (String file : files) {
                int mode = 5; // select appropriate mode

                String fileName = filePath + file;
                br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

                switch (mode) {
                    case 0:
                        formatData(br, filePath, fileWrite); // get data in required format
                        break;
                    case 1:
                        extractText(br, filePath, fileWrite, true); // get only text with or without POS tagging
                        break;
                    case 2:
                        getStat(br); // get statistical information
                        break;
                    case 3:
                        sliceFile(br); // divide big file in small parts
                        break;
                    case 4:
                        makeAttributesFile(br, filePath, fileWrite, true); // for single file
                        break;
                    case 5:
                        makeBigAttributes(br, true); // for group files
                        break;
                    default:
                        System.out.println("Task is interrupted");
                        return;
                }
            }

            br.close();
            System.out.println("Task is completed");
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getStackTrace());
            try {
                br.close();
                System.out.println("Task is interrupted");
            }
            catch (IOException eSmall) { }
        }
        finally {
            bfm.close();
        }

    }

    public static void extractText(BufferedReader br, String filePath, String fileWrite, boolean tagging) throws IOException { // Get only text from messages
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileWrite)));

        int enMessages = 0; // English messages
        int processedMessages = 0; // All messages

        while (br.ready()) {
            String tweet = br.readLine(); // Read message
            String updatedTweet = "";

            try {
                JSONParser parser = new JSONParser(); // Create parser
                Object obj = parser.parse(tweet);  // Pack tweet to Object
                JSONObject raw = (JSONObject) obj; // Unpack Object to JSON Object

                if (raw.get("lang").equals("en")) { // If tweet language is English process it
                    updatedTweet = checkForNull(raw.get("text")); // Get text
                    updatedTweet = cleanString(updatedTweet); // Clean text form unnecessary characters
                    updatedTweet = tokenize(updatedTweet); // Tokenize tweet
                    if (tagging) updatedTweet = taggingPOS(updatedTweet);
                }
            }
            catch (Exception e) {
//                System.out.println(e.getMessage());
            }

            processedMessages++; // Increase number of processed messages

            if (updatedTweet != "") { // Check whether text is not empty
                bw.write("\"" +  updatedTweet + "\"\r\n"); // Write text to a file
                enMessages++; // Increase number of processed English messages
            }

            if (processedMessages % 10000 == 0) { // Output message to command line per every 10000 processed tweets
                System.out.println(new Date() + " " + processedMessages + " | " + enMessages + " | "
                        + ((double) enMessages) / processedMessages);
            }
        }

        bw.close(); // Close a file
    }

    public static HashSet<String> loadStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("stop-words_english.txt")));
        HashSet<String> stopWords = new HashSet<String>();

        while (br.ready()) {
            String stopWord = br.readLine();
            if (!stopWords.contains(stopWord)) {
                stopWord = taggingPOS(stopWord);
                stopWords.add(stopWord);
            }
        }
        br.close();

/*        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("debug.txt")));
        for (String s : stopWords ) {
            bw.write(s + "\r\n");
        }
        bw.close();*/

        return stopWords;
    }

    public static void makeAttributesFile (BufferedReader br, String filePath, String fileWrite, boolean tagging) throws IOException { // Get only text from messages
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileWrite)));
        Map<String, Integer> attributes = new HashMap<>();

        int enMessages = 0; // English messages
        int processedMessages = 0; // All messages

        HashSet<String> stopWords = loadStopWords();

        while (br.ready() && processedMessages <= 1000000) {
            String tweet = br.readLine(); // Read message
            String updatedTweet = "";

            try {
                JSONParser parser = new JSONParser(); // Create parser
                Object obj = parser.parse(tweet);  // Pack tweet to Object
                JSONObject raw = (JSONObject) obj; // Unpack Object to JSON Object

                if (raw.get("lang").equals("en")) { // If tweet language is English process it
                    updatedTweet = checkForNull(raw.get("text")); // Get text
                    updatedTweet = cleanString(updatedTweet); // Clean text form unnecessary characters
                    updatedTweet = tokenize(updatedTweet); // Tokenize tweet
                    if (tagging) updatedTweet = taggingPOS(updatedTweet);

                    String[] candidates = updatedTweet.split(" ");
                    for (String candidate : candidates) {
                        if (!attributes.containsKey(candidate)) attributes.put(candidate, 1);
                        else {
                            int rateOfWord = attributes.get(candidate);
                            rateOfWord++;
                            attributes.put(candidate, rateOfWord);
                        }
                    }

                    enMessages++; // Increase number of processed English messages
                }
            }
            catch (Exception e) {
//                System.out.println(e.getMessage());
            }

            processedMessages++; // Increase number of processed messages

            if (processedMessages % 10000 == 0) { // Output message to command line per every 10000 processed tweets
                System.out.println(new Date() + " " + processedMessages + " | " + enMessages + " | "
                        + ((double) enMessages) / processedMessages);
            }
        }

/*        if (!attributes.isEmpty()) { // Calculate average of words use
            double arate = 0;
            for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
                arate += attribute.getValue();
            }
            System.out.println(arate /= attributes.size());
        }*/

        if (!attributes.isEmpty()) { // Check whether text is not empty
            for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
                if (attribute.getValue() >20 && !stopWords.contains(attribute.getKey())) {
                    bw.write(attribute.getKey() + "\r\n" ); // Write text to a file
                }
            }
        }

        bw.close(); // Close a file
    }

    public static void makeBigAttributes (BufferedReader br, boolean tagging) throws IOException { // Get only text from messages

        int enMessages = 0; // English messages
        int processedMessages = 0; // All messages

        HashSet<String> stopWords = loadStopWords();

        while (br.ready()) {
            String tweet = br.readLine(); // Read message
            String updatedTweet = "";

            try {
                JSONParser parser = new JSONParser(); // Create parser
                Object obj = parser.parse(tweet);  // Pack tweet to Object
                JSONObject raw = (JSONObject) obj; // Unpack Object to JSON Object

                if (raw.get("lang").equals("en")) { // If tweet language is English process it
                    updatedTweet = checkForNull(raw.get("text")); // Get text
                    updatedTweet = cleanString(updatedTweet); // Clean text form unnecessary characters
                    updatedTweet = tokenize(updatedTweet); // Tokenize tweet
                    if (tagging) updatedTweet = taggingPOS(updatedTweet);

                    String[] candidates = updatedTweet.split(" ");
                    for (String candidate : candidates) {
                        if (!attributesbfm.containsKey(candidate)) attributesbfm.put(candidate, 1);
                        else {
                            int rateOfWord = attributesbfm.get(candidate);
                            rateOfWord++;
                            attributesbfm.put(candidate, rateOfWord);
                        }
                    }

                    enMessages++; // Increase number of processed English messages
                }
            }
            catch (Exception e) {
//                System.out.println(e.getMessage());
            }

            processedMessages++; // Increase number of processed messages

            if (processedMessages % 10000 == 0) { // Output message to command line per every 10000 processed tweets
                System.out.println(new Date() + " " + processedMessages + " | " + enMessages + " | "
                        + ((double) enMessages) / processedMessages);
            }
        }

/*        if (!attributes.isEmpty()) { // Calculate average of words use
            double arate = 0;
            for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
                arate += attribute.getValue();
            }
            System.out.println(arate /= attributes.size());
        }*/

        if (!attributesbfm.isEmpty()) { // Check whether text is not empty
            for (Map.Entry<String, Integer> attribute : attributesbfm.entrySet()) {
                if (attribute.getValue() > 25 && !stopWords.contains(attribute.getKey())) {
                    bfm.write(attribute.getKey() + "\r\n"); // Write text to a file
                }
            }
        }
    }

    public static String spellCheck(String unCheckedString) throws IOException {

        boolean needRepeat = false;
        List<RuleMatch> matches;
        String s = unCheckedString;

        do {
            matches = langTool.check(s);
            for (RuleMatch match : matches) {

 /*               System.out.println("Potential error at line " +
                        match.getLine() + ", column " +
                        match.getColumn() + ": " + match.getMessage());
                System.out.println("Suggested correction: " +
                        match.getSuggestedReplacements());*/

                List<String> ls = match.getSuggestedReplacements();

                int begin = match.getFromPos();
                int end = match.getToPos();

                if (ls.size() == 1 && match.getMessage().contains("spelling mistake found"))  {
                    if (ls.get(0).equals(s.substring(begin, end))) break;
                    s = s.substring(0, begin) + ls.get(0).trim() + " " + s.substring(end + 1);
//                    System.out.println(s);
                    needRepeat = true;
                    break;
                }
                else {
                    needRepeat = false;
                }
            }
            if (matches.size() == 0) needRepeat = false;
        }

        while (needRepeat);

        return s;
    }

    public static String stemmer(StringReader fullWord) {
        KrovetzStemmer stemmer = new KrovetzStemmer();
        String line = null;

        // If we get here, we are about to process a file

        try {
            LineNumberReader reader = new LineNumberReader(fullWord);

            line = reader.readLine();

            line = line.trim();
            line = stemmer.stem(line);
            reader.close();

        } catch (Exception e) {
            System.out.println("Exception while processing term [" + line + "]");
            e.printStackTrace();
        }

        return line;
    }

    public static String[] dirFiles (String path) {
        File f = new File(path);
        String[] names = f.list(); // Get list files in directory

        return names;
    }

    public static void formatData(BufferedReader br, String filePath, String fileWrite) throws IOException { // process data from raw file to the suitable JSON format
        PrintWriter bw = new PrintWriter(new FileWriter((filePath + fileWrite), true));

        int enMessages = 0; // English messages
        int processedMessages = 0; // All messages
        bw.write("["); // Leading [ for JSON array

        while (br.ready()) {
            String tweet = br.readLine(); // Read next file
            String updatedTweet = "";

            try {
                updatedTweet = JSONparse(tweet); // Parse tweet
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            processedMessages++; // Increase number of processed messages

            if (updatedTweet != "")  { // Check whether tweet is not empty
                if (enMessages > 0) { // Is it the first English message in out file, need to correct JSON format
                    bw.write(",\r\n" + updatedTweet); // Write message with EOL symbols
                }
                else {
                    bw.write(updatedTweet); // Write message without EOL symbols
                }
                enMessages++;
            }

            if (processedMessages % 10000 == 0) { // Output message to command line per every 10000 processed tweets
                System.out.println(
                        new Date() + " " + processedMessages + " | " + enMessages + " | "
                        + ((double) enMessages) / processedMessages);
            }
        }

        bw.write("]"); // Final ] for JSON array

        bw.close(); // Close a file
    }

    public static String tokenize(String s) { // Tokenazation of tweet
        String processed = Tokenizer.tokenize(s);

        return processed;
    }

    public static String tokenizePOS(String s) { // Tokenization and Part Of Speech Tagging
        String processed = Tokenizer.tokenize(s);
        processed = tagger.tagString(processed).trim();

        return processed;
    }

    public static String taggingPOS(String s) { // Part Of Speech Tagging
        String processed = tagger.tagString(s).trim();

        return processed;
    }

    public static void sliceFile(BufferedReader br) { // Split big files in a small ones for better performance
        try {
            int messageNum = 0;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\tweet_" + 1 + ".json")));

            while (br.ready()) {
                String message = br.readLine();
                bw.write(message + "\r\n");
                ++messageNum;

                if (messageNum % 10000 == 0) {
                    System.out.println(new Date() + " " + messageNum);
                }

                if (messageNum % 50000 == 0) {
                    bw.close();
                    int fileNum = messageNum / 50000 + 1;
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\tweet_" + fileNum + ".json")));
                }
            }

            bw.close();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    public static void getStat(BufferedReader br) { // Statistical info about texts
        Map<String, Integer> languages = new HashMap<String, Integer>();
        int messages = 0;
        int words = 0;
        int specific = 0;
        ArrayList<String> specifics = new ArrayList<String>();

        try {
            while (br.ready()) {
                String newMessage = br.readLine();

                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(newMessage);
                    JSONObject raw = (JSONObject) obj;

                    String lang = checkForNull(raw.get("lang"));

                    String laugh = checkForNull((raw.get("text")));
                    String[] terms = laugh.split(" ");
                    words += terms.length;

                    if (lang.equals("en")) {
                        for (String s : terms) {                             // find a term and output a message
                            if (s.toLowerCase().contains("iph")) {
                                specific++;
                                specifics.add(laugh);
                            }
                        }
                    }

                    if (!languages.containsKey(lang)) {
                        languages.put(lang, 1);
                    } else {
                        int frequency = languages.get(lang);
                        frequency++;
                        languages.put(lang, frequency);
                    }

                    messages++;
                    if (messages % 50000 == 0) System.out.println(new Date() + ", " + messages); // notifier

//          if (messages == 200000) break; // too big queries


/*      for (Map.Entry<String, Integer> entry : languages.entrySet()) { // info about languages
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }*/

                    System.out.println(words + ", " + specifics.size());

                    for (String s : specifics) {
                        System.out.println(s);
                    }
                } catch (ParseException e) { }
            }
        }
        catch (IOException e) { }
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

    public static String cleanString(String rawString) throws IOException { // Clean string from unnecessary symbols
        String rawText = rawString.replaceAll("\\n", " "); // Replace all line breaks with spaces
        String[] oldArray = rawText.split(" "); // Split string in array

        String newString = ""; // Processed string

        for (String s: oldArray) {
            if (!s.contains("@") && !s.contains("http") && !s.equals("") && !s.equals("RT")) { // Don't need to save words with usernames, links, retweet mark or spend resources on empty instances
                String resultString = s.replaceAll("[^\\p{L}']+", ""); // Remove all non-latin characters and digits and save apostrophes
//                String resultString = s.replaceAll("[^\\p{L}\\p{Nd}]+", " "); // Remove all non-latin characters
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

        newString = spellCheck(newString);
        newString = newString.toLowerCase(); // Force all letters to the lower case
        newString = newString.trim(); // Remove lead and end spaces
        if (newString.equals("")) newString = "?"; // If proicessed string is empty return ? as an empty sign

        return newString;
    }

    public static String JSONparse(String rawString) throws ParseException, IOException {

        /*
        https://code.google.com/p/json-simple/wiki/DecodingExamples
        */

        JSONParser parser = new JSONParser(); // Create new parser
        Object obj = parser.parse(rawString); // Parse string and pack it in Object
        JSONObject raw = (JSONObject) obj; // Unpack from Object to JSON Object

        JSONObject formatted = new JSONObject(); // Create new empty JSON object
        formatted.put("lang", checkForNull(raw.get("lang"))); // Save tweet language
        if (!formatted.get("lang").equals("en")) return ""; // If language differs from English stop work

        formatted.put("original_text", checkForNull(raw.get("text")));

        formatted.put("created_at", checkForNull(raw.get("created_at"))); // Save Date and Time

        String rawText = checkForNull(raw.get("text")); // Get text and check does it exist
        rawText = (cleanString(rawText)); // Make some preprocessing
        String processedText = tokenizePOS(rawText);

        formatted.put("text", processedText); // Save text

        if (raw.get("geo") != null) { // Check whether geo information exist
            JSONObject geo = (JSONObject) raw.get("geo"); // Create new JSON object for every subarray, from example

            if (geo.get("coordinates") != null) { // Check whether coordinates information exist
                JSONArray coordinates = (JSONArray) geo.get("coordinates");

                String coordinateX = ((Double) coordinates.get(0)).toString();
                String coordinateY = ((Double) coordinates.get(1)).toString();

                formatted.put("coordinateX", checkForNull(coordinateX)); // Save X coordinate or empty sign
                formatted.put("coordinateY", checkForNull(coordinateY)); // Save Y coordinate or empty sign
            }

            if (geo.get("type") != null) { // Check whether coordinates have type of organization
                String typeOfCoordinates = (String) geo.get("type"); // Get type of coordinates, usually Point

               formatted.put("coordinateType", checkForNull(typeOfCoordinates)); // Save type of coordinates
            }
        }
        else { // Save information with empty signs if geo information doesn't exist
            formatted.put("coordinateX", "?");
            formatted.put("coordinateY", "?");
            formatted.put("coordinateType", "?");
        }

        JSONObject jsonUser = null; // Create object for extracting User information from subarray
        if (raw.get("user") != null) { // Check whether User information exists
            jsonUser = (JSONObject) raw.get("user"); // Save User information in object
        }

        formatted.put("time_zone", checkForNull(jsonUser.get("time_zone"))); // Get Time Zone

        String location = checkForNull(jsonUser.get("location")); // Get user location
        location = cleanString(location); // Clear text in location from non-latin symbols
        formatted.put("location", location); // Save location

        formatted.put("screen_name", checkForNull(jsonUser.get("screen_name"))); // for debugging get a username

        return formatted.toString(); // Return string with all necessary information

    }

    public static String checkForNull(Object JSONdata) { // Check  whether object is exists or not
        if (JSONdata == null) return "?"; // If doesn't exist return ? as a empty sign
        else return (String) JSONdata; // Return string representation
    }

}
