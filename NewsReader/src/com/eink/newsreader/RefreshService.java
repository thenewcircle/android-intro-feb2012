package com.eink.newsreader;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eink.parser.FeedParser;
import com.eink.parser.FeedParserFactory;
import com.eink.parser.Post;

/** Responsible for pulling the data from the cloud. 
 * @author Marko Gargenta
 */
public class RefreshService extends IntentService {
	static final String TAG = "RefreshService";
	static final String EINK_NEW_POSTS_ACTION = "com.eink.action.NEW_POSTS";

	DbHelper dbHelper;

	/** Default constructor. */
	public RefreshService() {
		super(TAG);

		dbHelper = new DbHelper(this);

		Log.d(TAG, "constructred");
	}

	/** Executed on a separate worker thread.
	 * @param intent Intent that started this service. */
	@Override
	protected void onHandleIntent(Intent intent) {
		boolean newPosts = false;
		
		// Get the feed URL
		String feedUrl = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("feedUrl", null);
		if (feedUrl == null)
			return;

		// Initialize parser
		FeedParser parser = FeedParserFactory.getParser(feedUrl);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			// Get the posts
			List<Post> posts = parser.parse();

			// Iterate over posts
			for (Post post : posts) {
				if( db.insert(DbHelper.TABLE, null, DbHelper.postToValues(post)) != -1) {
					newPosts = true;
				}
				Log.d(TAG, post.getTitle());
			}
			
			if( newPosts ) {
				sendBroadcast( new Intent(EINK_NEW_POSTS_ACTION) );
			}

		} catch (Exception e) {
			Log.e(TAG, "Problems loading the feed: " + feedUrl, e);
		}
		db.close();
		
		Log.d(TAG, "onHandleIntent");
	}

}
