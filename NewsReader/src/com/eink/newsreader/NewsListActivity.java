package com.eink.newsreader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NewsListActivity extends Activity {
	TextView textOutput;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        textOutput = (TextView) findViewById(R.id.text_output);
        
        textOutput.append("Hello!");
    }
}