import edu.stanford.nlp.ling.tokensregex.parser.TokenSequenceParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POS {

    public static String postagging(String s) {
        MaxentTagger tagger = new MaxentTagger("taggers/gate-EN-twitter.model");
        return tagger.tagString(s);
    }
}