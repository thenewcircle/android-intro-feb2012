package com.eink.newsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eink.parser.Post;

public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";
	static final String DB_NAME = "news.db";
	static final int DB_VERSION = 1;
	static final String C_ID = "_id";
	static final String C_TITLE = "title";
	static final String C_LINK = "link";
	static final String C_DESC = "description";
	static final String C_DATE = "date";

	/** Constructor */
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/** Called only once, fist time user installs the app. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String
				.format("CREATE TABLE posts (" +
						"%s int primary key, " +
						"%s text, %s text, " +
						"%s text, %s text)",
						C_ID, C_TITLE, C_LINK, C_DESC, C_DATE);
		Log.d(TAG, "onCreate sql: "+sql);
		db.execSQL(sql);
	}

	/** Called every time db version changes. */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists posts");
		onCreate(db);
	}

	/** Helper function to convert Post to ContentValues. */
	public ContentValues postToValues(Post post) {
		ContentValues values = new ContentValues();
		values.put(C_ID, post.hashCode());
		values.put(C_TITLE, post.getTitle());
		values.put(C_LINK, post.getLink().toString());
		values.put(C_DESC, post.getDescription());
		values.put(C_DATE, post.getDate());
		return values;
	}
}
