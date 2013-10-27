package com.daveyu.dmp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.daveyu.dmp.fragments.AlbumSongListFragment;
import com.daveyu.dmp.fragments.AlbumSongListFragment.PassLabel;

public class AlbumSongListActivity extends FragmentActivity implements PassLabel {
	
	String ALBUM;
	String ARTIST;
	String ALBUM_ART;
	String CALLER;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		ALBUM = intent.getStringExtra("ALBUM");
		ARTIST = intent.getStringExtra("ARTIST");
		ALBUM_ART = intent.getStringExtra("ALBUM_ART");
		CALLER = intent.getStringExtra("CALLER");
		getActionBar().setTitle(ALBUM);
		getActionBar().setSubtitle(ARTIST);
		
		setContentView(R.layout.activity_album_song_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		int actionBarHeight = 0;
		
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		actionBarHeight = actionBarHeight + (actionBarHeight / 2);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new AlbumSongListFragment();
		ft.add(R.id.album_song_list_fragment_container, fragment);
		ft.commit();
		
		//View view = findViewById(com.daveyu.dmp.R.id.album_song_list_fragment_container);
		//view.setPadding(0, actionBarHeight, 0, 0);
		
		ImageView background = (ImageView) findViewById(com.daveyu.dmp.R.id.album_background);
		try {
            background.setImageResource(Integer.parseInt(ALBUM_ART));
        } catch (NumberFormatException nfe) {
            background.setImageURI(Uri.parse(ALBUM_ART));
        }
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_song_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case com.daveyu.dmp.R.id.action_go_to_artist:
			goToArtist();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public String getArtistName() {
		return ARTIST;
	}

	@Override
	public String getAlbumName() {
		return ALBUM;
	}
	
	public void goToArtist() {
		if (CALLER.equals("ARTIST")) {
			onBackPressed();
		} else if (CALLER.equals("MAIN")) {
			Intent intent = new Intent(this, ArtistActivity.class);
			intent.putExtra("ARTIST", ARTIST);
			startActivity(intent);
		}
	}

}
