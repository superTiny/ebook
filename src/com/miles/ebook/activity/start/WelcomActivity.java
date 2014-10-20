package com.miles.ebook.activity.start;

import com.miles.ebook.R;
import com.miles.ebook.activity.MainActivity;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class WelcomActivity extends Activity
{

	ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom_main_layout);
		mImageView = (ImageView) findViewById(R.id.image);
		mImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				goToMainActivity();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	private void goToMainActivity()
	{
//		ObjectAnimator.ofFloat(mImageView, "alpha", 1, 0, 1).setDuration(15000)
//				.start();
		Intent intent = new Intent(WelcomActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}
}
