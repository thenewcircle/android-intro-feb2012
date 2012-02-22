package com.eink.newsreader;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

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
	ProcessFeedTask processFeedTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Find views
		output = (WebView) findViewById(R.id.output);

		// Update the screen
		processFeedTask = new ProcessFeedTask();
		processFeedTask.execute(feedUrl);

		Log.d(TAG, "onCreated");
	}

	
	// -- Menu Callbacks ---
	
	/** Called first time menu button is pressed. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
		case R.id.item_prefs:
			startActivity( new Intent(this, PrefsActivity.class) );
			return true;
		}
		return false;
	}





	/** AsyncTask for downloading and parsing the feed. */
	private class ProcessFeedTask extends AsyncTask<String, Void, String> {

		/** Work to be done on a separate (non-UI) thread. */
		@Override
		protected String doInBackground(String... feedUrls) {
			// Initialize parser
			parser = FeedParserFactory.getParser(feedUrls[0]);

			try {
				StringBuffer content = new StringBuffer();

				// Get the posts
				posts = parser.parse();

				String desc;
				// Iterate over posts
				for (Post post : posts) {
					desc = post.getDescription();
					// ellipsis
					if (desc.length() > MAX_LENGTH) {
						desc = desc.substring(0, MAX_LENGTH - 3) + "...";
					}
					// Create html output
					content.append(String.format(
							"<a href=%s><h2>%s</h2></a>\n%s\n<hr/>",
							post.getLink(), post.getTitle(), desc));
				}

				return content.toString();

			} catch (Exception e) {
				String message = "<strong>Problems parsing the feed: "
						+ feedUrls[0] + "</strong>";
				Log.e(TAG, message, e);
				return message;
			}
		}

		/** Executed on UI thread after background job is completed. */
		@Override
		protected void onPostExecute(String content) {
			super.onPostExecute(content);

			// Update output
			output.loadData(content, "text/html", "utf-8");
		}

	}

}