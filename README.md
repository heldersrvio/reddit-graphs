# RedditGraphs

## Summary

Graphical analysis of British subreddits and their relationships using graphs with the help of [jReddit](https://github.com/jReddit/jReddit) and [GraphStream](http://graphstream-project.org).


## Description

Three British subreddits were chosen for analysis: r/Scotland, r/unitedkingdom and r/UKPolitics. From each of them a limited number of recent and important threads is chosen and then most of the comments inside from that thread are separated into those with positive karma and negative karma. At the same time the program creates a list of all the users who commented in those threads.

Users are then separated into those that contribute with mostly negative comments to that particular subreddit and those that contribute with mostly positive comments. The user lists are filtered in order to keep a similar number on both sides. (Note: obviously, most users in any given subreddit will probably contribute with mostly 'positive' comments. However that is irrelevant to the goal of this project, which is to provide an insight into the differences and similarities between both sides).

After that, the program takes a look into other subreddits wherein each of those users has posted recently and the process is repeated, with the difference being that, this time, new users are not added to any list. We get to see in which subreddits those users contribute 'positively', which, then, become positive relations for one of the three original subreddits (the one where we found the user) if that user is 'positive' to that subreddit and negative relations otherwise.

For example, say we find a user that has a cumulative negative karma in r/Scotland from their most recent comments. We then go to that user's comments to find out in which communities he has positive karma. Say we find r/oneplus. Now r/oneplus has a 'negative relationship' with r/Scotland (unless we find other users who have positive karma in both subreddits in order to compensate).

Using GraphStream, positive relations are displayed in blue and negative relations, in red.
