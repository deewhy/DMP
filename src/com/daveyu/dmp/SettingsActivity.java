package com.daveyu.dmp;

import android.app.Activity;
import android.os.Bundle;

import com.daveyu.dmp.fragments.SettingsFragment;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsFragment()).commit();
	}
}
