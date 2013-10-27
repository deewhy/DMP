package com.daveyu.dmp.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlbumSongListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 0;
	private SimpleCursorAdapter adapter;
	PassLabel label_passer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
		
		String[] mProjection = {
				MediaStore.Audio.Media.TITLE,
				//MediaStore.Audio.Media.DURATION
		};
		
		int[] mTo = {
				com.daveyu.dmp.R.id.text_1,
				//com.daveyu.dmp.R.id.text_2
		};
		
		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(),
				com.daveyu.dmp.R.layout.list_item_songs,
				null,
				mProjection,
				mTo,
				0
		);
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(com.daveyu.dmp.R.layout.listview_layout, container, false);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
            label_passer = (PassLabel) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		String mProjection[] = {
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DURATION
		};
		
		String ARTIST_NAME = label_passer.getArtistName();
		String ALBUM_NAME = label_passer.getAlbumName();
		
		String[] selectionArgs = {"", ""};
		selectionArgs[0] = ARTIST_NAME;
		selectionArgs[1] = ALBUM_NAME;
		String selectionClause = "ARTIST = ? and ALBUM = ?";
		String sortOrder = "TRACK";
		
		CursorLoader cursorLoader = new CursorLoader(
				getActivity(),
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				mProjection,
				selectionClause,
				selectionArgs,
				sortOrder
		);
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}
	
	public interface PassLabel {
		public String getArtistName();
		public String getAlbumName();
	}

}
