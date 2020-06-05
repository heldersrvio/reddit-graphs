package EDIIGraph;
import java.io.*;
import java.util.*;

import com.github.jreddit.oauth.app.RedditScriptApp;
import com.github.jreddit.parser.entity.Comment;
import com.github.jreddit.parser.entity.More;
import com.github.jreddit.parser.entity.imaginary.CommentTreeElement;
import com.github.jreddit.parser.entity.imaginary.FullSubmission;
import com.github.jreddit.parser.listing.CommentsListingParser;
import com.github.jreddit.parser.single.FullSubmissionParser;
import com.github.jreddit.request.retrieval.comments.CommentsOfUserRequest;
import com.github.jreddit.request.retrieval.mixed.FullSubmissionRequest;
import com.github.jreddit.request.retrieval.param.UserOverviewSort;
import org.apache.http.impl.client.HttpClientBuilder;

import com.github.jreddit.oauth.RedditOAuthAgent;
import com.github.jreddit.oauth.RedditToken;
import com.github.jreddit.oauth.app.RedditApp;
import com.github.jreddit.oauth.client.RedditClient;
import com.github.jreddit.oauth.client.RedditHttpClient;
import com.github.jreddit.oauth.exception.RedditOAuthException;
import com.github.jreddit.parser.entity.Submission;
import com.github.jreddit.parser.exception.RedditParseException;
import com.github.jreddit.parser.listing.SubmissionsListingParser;
import com.github.jreddit.request.retrieval.param.SubmissionSort;
import com.github.jreddit.request.retrieval.submissions.SubmissionsOfSubredditRequest;
import com.github.jreddit.request.retrieval.param.CommentSort;
import com.github.jreddit.parser.entity.UserInfo;
import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Path;

public class Main {

    public static ArrayList<UserInfo> getRelevantUsersByKarma(String subreddit, boolean yespositives) throws RedditOAuthException, RedditParseException {
        // Information about the app
        String userAgent = "EDIIRedditGraphs";
        String clientID = CLIENTID;
        String redirectURI = REDIRECTURI;
        String secret = SECRET;
        ArrayList<UserInfo> positives = new ArrayList<UserInfo>();
        ArrayList<UserInfo> negatives = new ArrayList<UserInfo>();
        HashMap<String, Participant> relevants = new HashMap<String, Participant>();

        // Reddit application
        RedditApp redditApp = new RedditScriptApp(clientID, secret, redirectURI);

        // Create OAuth agent
        RedditOAuthAgent agent = new RedditOAuthAgent(userAgent, redditApp);

        // Create request executor
        RedditClient client = new RedditHttpClient(userAgent, HttpClientBuilder.create().build());

        // Create token (will be valid for 1 hour)
        RedditToken token = agent.tokenAppOnly(false);

        // Create parser for request
        SubmissionsListingParser parser = new SubmissionsListingParser();

        // Create the request
        SubmissionsOfSubredditRequest request = (SubmissionsOfSubredditRequest) new SubmissionsOfSubredditRequest(subreddit, SubmissionSort.HOT).setLimit(100);

        // Perform and parse request, and store parsed result
        List<Submission> submissions = parser.parse(client.get(token, request));

        // Now print out the result (don't care about formatting
        FullSubmissionParser parser2 = new FullSubmissionParser();
        for (Submission s: submissions) {
            FullSubmissionRequest commentsrequest = new FullSubmissionRequest(s.getId36()).setSort(CommentSort.CONFIDENCE);
            commentsrequest.setShowMore(true);
            FullSubmission comments = parser2.parse(client.get(token, commentsrequest));
            List<CommentTreeElement> ctree = comments.getCommentTree();
            for (int i = 0; i < ctree.size(); i++){
                if (ctree.get(i) instanceof Comment) {
                    if (!relevants.containsKey(((Comment) ctree.get(i)).getAuthor())) {
                        Participant p = new Participant(((Comment) ctree.get(i)).getAuthor(), ((Comment) ctree.get(i)).getScore());
                        relevants.put(p.getUsername(), p);
                    } else {
                        relevants.get(((Comment) ctree.get(i)).getAuthor()).updateComments_Number();
                        relevants.get(((Comment) ctree.get(i)).getAuthor()).updateKarma(((Comment) ctree.get(i)).getScore());
                    }
                }
            }

            commentsrequest = new FullSubmissionRequest(s.getId36()).setSort(CommentSort.CONTROVERSIAL);
            commentsrequest.setShowMore(true);
            comments = parser2.parse(client.get(token, commentsrequest));
            ctree = comments.getCommentTree();
            for (int i = 0; i < ctree.size(); i++){
                if (ctree.get(i) instanceof Comment) {
                    if (!relevants.containsKey(((Comment) ctree.get(i)).getAuthor())) {
                        Participant p = new Participant(((Comment) ctree.get(i)).getAuthor(), ((Comment) ctree.get(i)).getScore());
                        relevants.put(p.getUsername(), p);
                    } else {
                        relevants.get(((Comment) ctree.get(i)).getAuthor()).updateComments_Number();
                        relevants.get(((Comment) ctree.get(i)).getAuthor()).updateKarma(((Comment) ctree.get(i)).getScore());
                    }
                }
            }
        }

        Iterator it = relevants.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int cn = ((Participant) pair.getValue()).getComments_number();
            if (cn >= 5){
                UserInfo uf = new UserInfo();
                uf.setId((String) pair.getKey());
                if (((Participant) pair.getValue()).getKarma() >= cn)
                    positives.add(uf);
                else
                    negatives.add(uf);

            }
            it.remove(); // avoids a ConcurrentModificationException
        }

        //System.out.println(submissions);
        //System.out.println(((Comment) comments.getCommentTree().get(0)).getScore());
        if (yespositives)
            return positives;
        return negatives;
    }

    public static ArrayList<String> getConnectedSubreddits(String subreddit, UserInfo u) throws RedditOAuthException, RedditParseException {
        String userAgent = "EDIIRedditGraphs";
        String clientID = "OrvEUpPIHfZolA";
        String redirectURI = "http://localhost:8080";
        String secret = "32uzhIGCwi_HQZi3qvc44fjcT7k";
        ArrayList<String> relevantsubs = new ArrayList<String>();

        if (u.getId().equals("[deleted]"))
            return null;

        RedditApp redditApp = new RedditScriptApp(clientID, secret, redirectURI);

        // Create OAuth agent
        RedditOAuthAgent agent = new RedditOAuthAgent(userAgent, redditApp);

        // Create request executor
        RedditClient client = new RedditHttpClient(userAgent, HttpClientBuilder.create().build());

        // Create token (will be valid for 1 hour)
        RedditToken token = agent.tokenAppOnly(false);

        CommentsListingParser parser = new CommentsListingParser();

        CommentsOfUserRequest request = new CommentsOfUserRequest(u.getId()).setSort(UserOverviewSort.NEW);
        request.setLimit(100);

        List<Comment> comments = parser.parse(client.get(token, request));

        for (Comment c: comments){
            if (!c.getSubreddit().equals(subreddit)){
                relevantsubs.add(c.getSubreddit());
            }
        }

        return relevantsubs;

    }

    public static void main(String[] Args){
        try {
            ArrayList<UserInfo> positivesuk = getRelevantUsersByKarma("unitedkingdom", true);
            ArrayList<UserInfo> negativesuk = getRelevantUsersByKarma("unitedkingdom", false);
            ArrayList<UserInfo> positivesukpolitics = getRelevantUsersByKarma("ukpolitics", true);
            ArrayList<UserInfo> negativesukpolitics = getRelevantUsersByKarma("ukpolitics", false);
            ArrayList<UserInfo> positivessc = getRelevantUsersByKarma("scotland", true);
            ArrayList<UserInfo> negativessc = getRelevantUsersByKarma("scotland", false);
            /*System.out.println(positivesuk.size());
            System.out.println(negativesuk.size());
            System.out.println(positivesukpolitics.size());
            System.out.println(negativesukpolitics.size());
            System.out.println(positivessc.size());
            System.out.println(negativessc.size());*/
            while (positivesuk.size() > negativesuk.size()){
                positivesuk.remove(0);
            }
            while(positivesukpolitics.size() > negativesukpolitics.size()){
                positivesukpolitics.remove(0);
            }
            while(positivessc.size() > negativessc.size()){
                positivessc.remove(0);
            }
            /*System.out.println("Positive Users from r/unitedkingdom");
            for (UserInfo u: positivesuk){
                System.out.println(u.getId());
            }
            System.out.println("\nNegative users from r/unitedkingdom");
            for (UserInfo u: negativesuk){
                System.out.println(u.getId());
            }
            System.out.println("\nPositive users from r/ukpolitics");
            for (UserInfo u: positivesukpolitics){
                System.out.println(u.getId());
            }
            System.out.println("\nNegative users from r/ukpolitics");
            for (UserInfo u: negativesukpolitics){
                System.out.println(u.getId());
            }
            System.out.println("\nPositives users from r/scotland");
            for (UserInfo u: positivessc){
                System.out.println(u.getId());
            }
            System.out.println("\nNegative users from r/scotland");
            for (UserInfo u: negativessc){
                System.out.println(u.getId());
            }*/

            HashMap<String, ArrayList<SubRConnection>> graph = new HashMap<String, ArrayList<SubRConnection>>();
            ArrayList<SubRConnection> ukcon = new ArrayList<SubRConnection>();
            ArrayList<SubRConnection> ukpcon = new ArrayList<SubRConnection>();
            ArrayList<SubRConnection> sccon = new ArrayList<SubRConnection>();
            for (UserInfo u: positivesuk){
                ArrayList<String> relevantsubs = getConnectedSubreddits("unitedkingdom", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.BLUE);
                        ukcon.add(k);
                    }
                }
            }
            for (UserInfo u: negativesuk){
                ArrayList<String> relevantsubs = getConnectedSubreddits("unitedkingdom", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.RED);
                        ukcon.add(k);
                    }
                }
            }
            graph.put("unitedkingdom", ukcon);
            for (UserInfo u: positivesukpolitics){
                ArrayList<String> relevantsubs = getConnectedSubreddits("ukpolitics", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.BLUE);
                        ukpcon.add(k);
                    }
                }
            }
            for (UserInfo u: negativesukpolitics){
                ArrayList<String> relevantsubs = getConnectedSubreddits("ukpolitics", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.RED);
                        ukpcon.add(k);
                    }
                }
            }
            graph.put("ukpolitics", ukpcon);
            for (UserInfo u: positivessc){
                ArrayList<String> relevantsubs = getConnectedSubreddits("Scotland", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.BLUE);
                        sccon.add(k);
                    }
                }
            }
            for (UserInfo u: negativessc){
                ArrayList<String> relevantsubs = getConnectedSubreddits("Scotland", u);
                if (relevantsubs != null && relevantsubs.size() > 0){
                    //System.out.println("List of subreddits " + u.getId() + " is active in: ");
                    for (String subreddit: relevantsubs) {
                        //System.out.println(subreddit);
                        SubRConnection k = new SubRConnection(subreddit, SubRConnection.RED);
                        sccon.add(k);
                    }
                }
            }
            graph.put("Scotland", sccon);
            Iterator it = graph.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + ": ");
                for (int i = 0; i < ((List) pair.getValue()).size(); i++){
                    System.out.println(((SubRConnection)((List) pair.getValue()).get(i)).getSubreddit());
                }
            }
            GraphStreamGraph gsg = new GraphStreamGraph(graph);
            ConnectedComponents cc = new ConnectedComponents();
            cc.init(gsg.graph);
            System.out.println("Numero de vertices: " + gsg.graph.getNodeCount());
            System.out.println("Numero de arestas: " + gsg.graph.getEdgeCount());
            System.out.println("Componentes conectados: " + cc.getConnectedComponentsCount());
            AStar astar = new AStar(gsg.graph);
            astar.compute("CasualUK", "conspiracy");
            System.out.println("Percurso entre r/CasualUK e r/conspiracy: " + astar.getShortestPath());

        } catch (RedditOAuthException e) {
            e.printStackTrace();
        } catch (RedditParseException e) {
            e.printStackTrace();
        }
    }
}
