package EDIIGraph;

public class Participant {

    private String username;
    private int comments_number;
    private int karma;

    public String getUsername() {
        return username;
    }

    public int getComments_number() {
        return comments_number;
    }

    public int getKarma() {
        return karma;
    }

    public Participant(String username, int karma){
        this.username = username;
        this.karma = karma;
    }

    public void updateKarma(int add){
        karma = karma + add;
    }

    public void updateComments_Number(){
        comments_number++;
    }
}
