package com.daveyu.dmp;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.daveyu.dmp.fragments.AlbumListFragment;
import com.daveyu.dmp.fragments.ArtistListFragment;
import com.daveyu.dmp.fragments.GenreListFragment;
import com.daveyu.dmp.fragments.PlaylistListFragment;
import com.daveyu.dmp.fragments.SongListFragment;

public class MainActivity extends FragmentActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			/**
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			**/
			Fragment ArtistTabFragment = new ArtistListFragment();
			Fragment AlbumTabFragment = new AlbumListFragment();
			Fragment SongTabFragment = new SongListFragment();
			Fragment GenreTabFragment = new GenreListFragment();
			Fragment PlaylistTabFragment = new PlaylistListFragment();
			
			switch (position) {
			case 0:
				return ArtistTabFragment;
			case 1:
				return AlbumTabFragment;
			case 2:
				return SongTabFragment;
			case 3:
				return PlaylistTabFragment;
			case 4:
				return GenreTabFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_artist_list).toUpperCase(l);
			case 1:
				return getString(R.string.title_album_list).toUpperCase(l);
			case 2:
				return getString(R.string.title_song_list).toUpperCase(l);
			case 3:
				return getString(R.string.title_playlist_list).toUpperCase(l);
			case 4:
				return getString(R.string.title_genre_list).toUpperCase(l);
			}
			return null;
		}
	}

}
