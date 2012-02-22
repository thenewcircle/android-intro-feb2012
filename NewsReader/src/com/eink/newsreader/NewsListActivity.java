package com.eink.newsreader;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.eink.parser.FeedParser;
import com.eink.parser.FeedParserFactory;
import com.eink.parser.Post;

public class NewsListActivity extends Activity {
	static final String TAG = "NewsListActivity";
	static final int MAX_LENGTH = 250;
	String feedUrl = "http://marakana.com/s/feed.rss";
	WebView output;
	FeedParser parser;
	List<Post> posts;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Find views
		output = (WebView) findViewById(R.id.output);

		// Initialize parser
		parser = FeedParserFactory.getParser(feedUrl);

		try {
			StringBuffer content = new StringBuffer();

			// Get the posts
			posts = parser.parse();

			String desc;
			// Iterate over posts
			for (Post post : posts) {
				desc = post.getDescription();
				// ellipsis
				if(desc.length()>MAX_LENGTH) {
					desc = desc.substring(0, MAX_LENGTH-3)+"...";
				}
				// Create html output
				content.append(String.format(
						"<a href=%s><h2>%s</h2></a>\n%s\n<hr/>",
						post.getLink(), post.getTitle(), desc));
			}

			// Update output
			output.loadData(content.toString(), "text/html", "utf-8");

		} catch (Exception e) {
			String message = "Problems parsing the feed: " + feedUrl;
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			Log.e(TAG, message, e);
		}

		Log.d(TAG, "onCreated");
	}
}