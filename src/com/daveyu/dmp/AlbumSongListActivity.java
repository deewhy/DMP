package com.daveyu.dmp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
	
	int actionBarHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			ALBUM = savedInstanceState.getString("ALBUM_KEY");
			ARTIST = savedInstanceState.getString("ARTIST_KEY");
			ALBUM_ART = savedInstanceState.getString("ALBUM_ART_KEY");
			CALLER = savedInstanceState.getString("CALLER_KEY");
			actionBarHeight = savedInstanceState.getInt("ACTIONBAR_HEIGHT_KEY");
		} else {
			Intent intent = getIntent();
			ALBUM = intent.getStringExtra("ALBUM");
			ARTIST = intent.getStringExtra("ARTIST");
			ALBUM_ART = intent.getStringExtra("ALBUM_ART");
			CALLER = intent.getStringExtra("CALLER");
			
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
		}
		
		getActionBar().setTitle(ALBUM);
		getActionBar().setSubtitle(ARTIST);
		
		setContentView(R.layout.activity_album_song_list);
		
		ImageView background = (ImageView) findViewById(com.daveyu.dmp.R.id.album_background);
		try {
            background.setImageResource(Integer.parseInt(ALBUM_ART));
        } catch (NumberFormatException nfe) {
            background.setImageURI(Uri.parse(ALBUM_ART));
        }
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
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case com.daveyu.dmp.R.id.action_go_to_artist:
			goToArtist();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("ALBUM_KEY", ALBUM);
		savedInstanceState.putString("ARTIST_KEY", ARTIST);
		savedInstanceState.putString("ALBUM_ART_KEY", ALBUM_ART);
		savedInstanceState.putString("CALLER_KEY", CALLER);
		savedInstanceState.putInt("ACTIONBAR_HEIGHT_KEY", actionBarHeight);
		super.onSaveInstanceState(savedInstanceState);
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
