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

public class RefreshService extends IntentService {
	static final String TAG = "RefreshService";

	DbHelper dbHelper;

	public RefreshService() {
		super(TAG);

		dbHelper = new DbHelper(this);

		Log.d(TAG, "constructred");
	}

	/** Executed on a separate worker thread. */
	@Override
	protected void onHandleIntent(Intent intent) {

		// Get the feed URL
		String feedUrl = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("feedUrl", null);
		if (feedUrl == null)
			return;

		// Initialize parser
		FeedParser parser = FeedParserFactory.getParser(feedUrl);

		try {
			// Get the posts
			List<Post> posts = parser.parse();

			SQLiteDatabase db = dbHelper.getWritableDatabase();

			// Iterate over posts
			for (Post post : posts) {
				db.insert(DbHelper.TABLE, null, DbHelper.postToValues(post));
				Log.d(TAG, post.getTitle());
			}

		} catch (Exception e) {
			Log.e(TAG, "Problems loading the feed: " + feedUrl, e);
		}
		Log.d(TAG, "onHandleIntent");
	}

}
