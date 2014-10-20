package com.miles.ebook.activity;

import java.util.logging.Logger;

import com.miles.ebook.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity
{

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView tv=(TextView) findViewById(R.id.tv);
		String a=(Intent.FLAG_ACTIVITY_NO_ANIMATION
				| Intent.FLAG_ACTIVITY_CLEAR_TOP)+"";
		tv.setText(a);
		Log.e(TAG, a);
		Log.e(TAG, Integer.toHexString(Intent.FLAG_ACTIVITY_NO_ANIMATION
				| Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

}
