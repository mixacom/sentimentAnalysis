package mainApp;

import java.util.Date;

public class Tweet {

    private final String user;
    private final String content;
    private final Date date;

    public Tweet(String user, Date date, String content) {
        this.user = user;
        this.date = date;
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }
    
    public Date getDate() {
    	return date;
    }
}
