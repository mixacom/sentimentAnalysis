/**
 * Created by Mikhail on 11.10.2014.
 */

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

public class Tokenizer {

    public static String tokenize(String s) {
        StringReader sr = new StringReader(s);

        String tokenizedString = "";

        PTBTokenizer ptbt = new PTBTokenizer(sr, new CoreLabelTokenFactory(), "");
        for (CoreLabel label; ptbt.hasNext(); ) {
            label = (CoreLabel) ptbt.next();             //  List<String> anApproach = ptbt.tokenize();
            tokenizedString += label.toString() + " ";
//            System.out.print(label + " ");
        }

        tokenizedString = tokenizedString.trim();
        return tokenizedString;
    }
}
