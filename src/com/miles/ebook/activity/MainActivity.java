package com.miles.ebook.activity;

import com.miles.ebook.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity
{

	private static final String TAG = "MainActivity";
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView()
	{
		mT
	}

	private void addTab(String tag, int indicatorDrawableId, String tab_text)
	{
		TabHost.TabSpec spec = mTabHost.newTabSpec(tag);
		spec.setContent(new TabHost.TabContentFactory()
		{
			public View createTabContent(String tag)
			{
				return findViewById(R.id.content);
			}
		});
		spec.setIndicator(createTabView(indicatorDrawableId, tab_text, tag));
		mTabHost.addTab(spec);
	}

	private View createTabView(final int id, final String tab_text,
			final String tag)
	{
		View view = LayoutInflater.from(this).inflate(R.layout.tab_main_nav, null);

		TextView tv = (TextView) view.findViewById(R.id.tvTitle);
		tv.setText(tab_text);
		ImageView iv = (ImageView) view.findViewById(R.id.ivIcon);
		iv.setImageResource(id);

		return view;
	}
}
