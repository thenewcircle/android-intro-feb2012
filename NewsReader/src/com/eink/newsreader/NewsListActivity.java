package com.eink.newsreader;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.eink.parser.FeedParser;
import com.eink.parser.FeedParserFactory;
import com.eink.parser.Post;

public class NewsListActivity extends Activity {
	String feedUrl = "http://marakana.com/s/feed.rss";
	TextView textOutput;
	FeedParser parser;
	List<Post> posts;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        // Find views
        textOutput = (TextView) findViewById(R.id.text_output);
        
        // Initialize parser
        parser = FeedParserFactory.getParser(feedUrl);
        
        // Get the posts
        posts = parser.parse();
        
        // Iterate over posts
        for(Post post: posts) {
        		textOutput.append( post.toString() + "\n\n" );
        }
    }
}