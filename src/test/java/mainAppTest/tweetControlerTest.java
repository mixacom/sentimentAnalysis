package mainAppTest;

import static org.junit.Assert.assertEquals;
import mainApp.TweetController;

import org.junit.Test;

public class tweetControlerTest {
	
	@Test
	public void testReplaceNestedContent() {
		TweetController contr = new TweetController();
		String result = contr.replaceNestedContent("IMPORTANT", "This is an[#nested]test");
		assertEquals("This is anIMPORTANTtest", result);
	}
}
