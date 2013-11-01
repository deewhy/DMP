package com.daveyu.dmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import Testing.ChangeBackgroundDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.daveyu.dmp.fragments.ArtistAlbumListFragment;
import com.daveyu.dmp.fragments.ArtistAlbumListFragment.PassLabel;
import com.daveyu.dmp.fragments.ArtistBioFragment;
import com.daveyu.dmp.fragments.ArtistBioFragment.GetBio;
import com.daveyu.dmp.fragments.ArtistSongListFragment;
import com.daveyu.dmp.lastfm.Artist;
import com.daveyu.dmp.lastfm.Artist.SignalBackgroundChange;
import com.daveyu.dmp.lastfm.Artist.SignalBioChange;

public class ArtistActivity extends FragmentActivity 
	implements PassLabel, ChangeBackgroundDialog.ChangeBackgroundDialogListener, SignalBackgroundChange, SignalBioChange, GetBio {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	String ARTIST;
	String ARTIST_BIO;
	Uri ARTIST_URI;
	
	private Intent mRequestFileIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Artist getArtist = new Artist();
		
		if (savedInstanceState != null) {
			ARTIST = savedInstanceState.getString("ARTIST_NAME_KEY");
			ARTIST_BIO = savedInstanceState.getString("ARTIST_BIO_KEY");
		} else {
			Intent intent = getIntent();
			ARTIST = intent.getStringExtra("ARTIST");
			getArtist.getBio(ARTIST, this);
		}
		
		setTitle(ARTIST);
		setContentView(R.layout.activity_artist);
		ImageView imageView = (ImageView) findViewById(com.daveyu.dmp.R.id.artist_background);
		
		if (isExternalStorageReadable() == true) {
			File file = new File(getExternalFilesDir(null) + File.separator + "artistpics" + File.separator + ARTIST + ".jpg");
			if (file.exists()) {
				ARTIST_URI = Uri.parse(file.toURI().toString());
				imageView.setImageURI(ARTIST_URI);
			} else {
				getArtist.getImage(ARTIST, this);
			}
		}
		
		mRequestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
		mRequestFileIntent.setType("image/*");

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("ARTIST_BIO_KEY", ARTIST_BIO);
		savedInstanceState.putString("ARTIST_NAME_KEY", ARTIST);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_change_background:
			changeBackground();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
		if (resultCode != RESULT_OK) {
            // Exit without doing anything else
            return;
        } else {
            Uri ARTIST_URI = returnIntent.getData();
            ImageView imageView = (ImageView) findViewById(com.daveyu.dmp.R.id.artist_background); 
            imageView.setImageURI(ARTIST_URI);
            copyImageToDir(ARTIST_URI);
        }
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			Fragment ArtistAlbumTabFragment = new ArtistAlbumListFragment();
			Fragment ArtistSongTabFragment = new ArtistSongListFragment();
			Fragment ArtistBioTabFragment = new ArtistBioFragment();
			
			switch (position) {
			case 0:
				return ArtistAlbumTabFragment;
			case 1:
				return ArtistSongTabFragment;
			case 2:
				return ArtistBioTabFragment;
			default:
				return ArtistAlbumTabFragment;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_artist_album_list).toUpperCase(l);
			case 1:
				return getString(R.string.title_artist_song_list).toUpperCase(l);
			case 2:
				return getString(R.string.title_artist_bio).toUpperCase(l);
			}
			return null;
		}
	}
	
	public void changeBackground() {
		ChangeBackgroundDialog dialog = new ChangeBackgroundDialog();
		dialog.show(getSupportFragmentManager(), "getBackgroundImageDialog");
	}

	/**
	 * Callback method telling Activity to pass artist name to Fragment
	 */
	@Override
	public String getArtistName() {
		return ARTIST;
	}
	
	/**
	 * Callback methods for setting background image 
	 * from disk or from web
	 */
	@Override
	public void sourceGallery(DialogFragment dialog) {
		Toast.makeText(getApplicationContext(), "Clicked the button for source: Gallery", Toast.LENGTH_SHORT).show(); 
		startActivityForResult(mRequestFileIntent, 0);
	}

	@Override
	public void sourceWeb(DialogFragment dialog) {
		Toast.makeText(getApplicationContext(), "Clicked the button for source: Web", Toast.LENGTH_SHORT).show();
		Artist getArtist = new Artist();
		getArtist.getImage(ARTIST, this);
	}
	
	/**
	 * Makes a copy of returned image and saves it as /Files/artistpics/ARTIST.jpg
	 */
	public void copyImageToDir(Uri uri) {
		InputStream in = null;
		FileOutputStream out = null;
		
		File artistPicDirectory = new File(getExternalFilesDir(null) + File.separator + "artistpics");
		artistPicDirectory.mkdir();
		File file = new File(artistPicDirectory, ARTIST + ".jpg");
		try {
			in = getContentResolver().openInputStream(uri);
			out = new FileOutputStream(file);
			
			int c;
			while  ((c = in.read()) != -1) {
				out.write(c);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} finally {
			 if (in != null) {
	                try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	            if (out != null) {
	                try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
		}
	}
	
	/**
	 * Callback method used by AsyncTask when finished task. Attempts to set newly downloaded
	 * file to background ImageView.
	 */
	@Override
	public void tryChangeBackground() {
		ImageView imageView = (ImageView) findViewById(com.daveyu.dmp.R.id.artist_background); 
		File file = new File(getExternalFilesDir(null) + File.separator + "artistpics" + File.separator + ARTIST + ".jpg");
		if (file.exists()) {
			ARTIST_URI = Uri.parse(file.toURI().toString());
			imageView.setImageURI(ARTIST_URI);
			imageView.invalidate();
		}
	}
	
	/**
	 * Callback method used by AsyncTask when finished task. 
	 * Passes retrieved artist biography as String.
	 */
	@Override
	public void tryChangeBio(String summary) {
		ARTIST_BIO = summary;
	}
	
	/**
	 * Callback method used by ArtistBioFragment to retrieve 
	 * artist biography String.
	 */
	@Override
	public String getBio() {
		if (ARTIST_BIO == null) {
			return "Artist bio could not be loaded. <br>Please try again later.";
		} else {
		return ARTIST_BIO;
		}
	}
	
}
