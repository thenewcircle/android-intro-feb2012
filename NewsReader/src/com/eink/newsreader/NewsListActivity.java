package com.eink.newsreader;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class NewsListActivity extends ListActivity {
	static final String TAG = "NewsListActivity";
	static final int MAX_LENGTH = 250;
	static final String[] FROM = { DbHelper.C_TITLE, DbHelper.C_DESC };
	static final int[] TO = { R.id.post_title, R.id.post_desc };

	DbHelper dbHelper;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	NewPostReceiver receiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the data
		dbHelper = new DbHelper(this);

		// Setup the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.post, null, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		setListAdapter(adapter);

		receiver = new NewPostReceiver();

		Log.d(TAG, "onCreated");
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();

		registerReceiver(receiver, new IntentFilter(
				RefreshService.EINK_NEW_POSTS_ACTION) );

		Log.d(TAG, "onResumed");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	private void refresh() {
		// Update the screen
		String feedUrl = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("feedUrl", null);
		if (feedUrl == null || "".equals(feedUrl)) {
			// Bounce user to Prefs activity
			Toast.makeText(this, "Please enter Feed URL", Toast.LENGTH_LONG)
					.show();
			startActivity(new Intent(this, PrefsActivity.class));
		} else {
			// Load the data and update the screen
			cursor = dbHelper.getReadableDatabase().query(DbHelper.TABLE, null,
					null, null, null, null, null);
			startManagingCursor(cursor);

			Log.d(TAG, "Curoser has recrods: " + cursor.getCount());

			((SimpleCursorAdapter) getListAdapter()).changeCursor(cursor);

			Log.d(TAG, "Refreshing...");
		}
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
		switch (item.getItemId()) {
		case R.id.item_prefs:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		case R.id.item_refresh:
			startService(new Intent(this, RefreshService.class));
			return true;
		}
		return false;
	}

	/** View binder to help us deal with WebView. */
	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() == R.id.post_desc) {
				String desc = cursor.getString(columnIndex);
				((TextView) view).setText(Html.fromHtml(desc));
				return true;
			} else {
				return false;
			}
		}

	};

	private class NewPostReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	}

}