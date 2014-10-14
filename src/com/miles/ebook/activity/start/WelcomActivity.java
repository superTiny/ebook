package com.miles.ebook.activity.start;

import com.miles.ebook.R;
import com.miles.ebook.activity.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom_main_layout);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		goToMainActivity();
	}

	private void goToMainActivity()
	{
		
		startActivity(new Intent(WelcomActivity.this,MainActivity.class));
	}
}
