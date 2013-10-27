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
import com.daveyu.dmp.fragments.ArtistSongListFragment;

public class ArtistActivity extends FragmentActivity 
	implements PassLabel, ChangeBackgroundDialog.ChangeBackgroundDialogListener {

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
	
	private Intent mRequestFileIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		ARTIST = intent.getStringExtra("ARTIST");
		setTitle(ARTIST);
		
		setContentView(R.layout.activity_artist);
		
		ImageView imageView = (ImageView) findViewById(com.daveyu.dmp.R.id.artist_background); 
		
		File file = new File(getExternalFilesDir(null) + File.separator + "artistpics" + File.separator + ARTIST + ".jpg");
		if (file.exists()) {
			Uri uri = Uri.parse(file.toURI().toString());
			imageView.setImageURI(uri);
		} else {
			imageView.setImageResource(com.daveyu.dmp.R.drawable.background_test_2);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change_background:
			changeBackground();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
		if (resultCode != RESULT_OK) {
            // Exit without doing anything else
            return;
        } else {
            
            Uri returnUri = returnIntent.getData();
            ImageView imageView = (ImageView) findViewById(com.daveyu.dmp.R.id.artist_background); 
            imageView.setImageURI(returnUri);
            copyImageToDir(returnUri);
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
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment2 = new ArtistAlbumListFragment();
			Fragment fragment3 = new ArtistSongListFragment();
			Fragment fragment4 = new ArtistBioFragment();
			
			switch (position) {
			case 0:
				return fragment2;
			case 1:
				return fragment3;
			}
			return fragment4;
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
		return ARTIST;// TODO Auto-generated method stub
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            if (out != null) {
	                try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
		}
		
		
	
		
	}
	
}
