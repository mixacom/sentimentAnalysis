package mainApp;


public class Tweet {

    private final String content;
    private double score;
    private sentimentClass sClass;
    private final String user;
    private final String date;
    
    public enum sentimentClass {
    	NEGATIVE, NEUTRAL, POSITIVE
    }

    public Tweet(String content, String user, String date) {
        this.content = content;
        this.user = user;
        this.date = date;
    }


    public String getContent() {
        return content;
    }
    
    public double getScore() {
    	//Double doubleObj = (Double) this.score;
    	//if (doubleObj == null) {
    	//	this.score = Score.calculate(this.content);
    	//}
    	//return Score.calculate(this.content);
    	return score;
    }
    
    public void setScore(double score) {
    	this.score = score;
    }
    
    public sentimentClass getSentimentClass() {
    	return sClass;
    }
    
    public void setSentimentClass(sentimentClass sClass) {
    	this.sClass = sClass;
    }
    
    public String getUser() {
    	return user;
    }
    
    public String getDate() {
    	return date;
    }
    
    public int getNumberOfWords() {
    	String words[] = content.split(" ");
    	return words.length;
    }
    
    public String toString() {
    	return "{tweet: content{ " + this.content + " }, score{ " + this.score + " }, testClass{ " + this.sClass + " }";
    }
}
