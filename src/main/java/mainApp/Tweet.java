package mainApp;

public class Tweet {

    private final String user;
    private final String content;

    public Tweet(String user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }
}
