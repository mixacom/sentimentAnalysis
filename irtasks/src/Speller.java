import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mikhail on 13.10.2014.
 */
public class Speller {

    public static void main(String[] args) throws IOException, org.xml.sax.SAXException, ParserConfigurationException {

        org.languagetool.JLanguageTool langTool = new org.languagetool.JLanguageTool(new AmericanEnglish());
        langTool.activateDefaultPatternRules();

//        String s = "gim me a minute then send er coz don't evoporate ";
        String s = "Gim me a one neu idea about it.";
        boolean needRepeat = false;

        List<RuleMatch> matches;

        do {
            matches = langTool.check(s);
            for (RuleMatch match : matches) {
                System.out.println("Potential error at line " +
                        match.getLine() + ", column " +
                        match.getColumn() + ": " + match.getMessage());
                System.out.println("Suggested correction: " +
                        match.getSuggestedReplacements());
                List<String> ls = match.getSuggestedReplacements();
                if (ls.size() == 1) {
                    int beg = match.getFromPos();
                    int end = match.getToPos();
                    String s1 = s.substring(0, beg);
                    String s2 = ls.get(0);
                    String s3 = " " + s.substring(end + 1);
                    s = s1 + s2 + s3;
                    System.out.println(s);
                    needRepeat = true;
                    break;
                }
            }
            if (matches.size() == 0) needRepeat = false;
        }
        while (needRepeat);
    }
}
