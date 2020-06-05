package EDIIGraph;

public class SubRConnection {
    public static final boolean RED   = false;
    public static final boolean BLUE = true;

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public boolean isColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    private String subreddit;
    private boolean color;

    public SubRConnection(String subreddit, boolean color){
        this.subreddit = subreddit;
        this.color = color;
    }
}
