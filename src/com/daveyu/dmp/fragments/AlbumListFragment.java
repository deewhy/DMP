package com.daveyu.dmp.fragments;

import com.daveyu.dmp.AlbumSongListActivity;

import android.content.Intent;
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
import android.widget.ListView;

public class AlbumListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 0;
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
		
		String[] mProjection = {
				MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ARTIST,
				MediaStore.Audio.Albums.ALBUM_ART
			};
		
		int[] mTo = {
				com.daveyu.dmp.R.id.text_1,
				com.daveyu.dmp.R.id.text_2,
				com.daveyu.dmp.R.id.album_thumbnail
			};
		
		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(),
				com.daveyu.dmp.R.layout.list_item_albums,
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
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		String mProjection[] = {
				MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ARTIST,
				MediaStore.Audio.Albums.ALBUM_ART
			};
		
		String sortOrder = "ALBUM";
		
		CursorLoader cursorLoader = new CursorLoader(
				getActivity(),
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				mProjection,
				null,
				null,
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
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		String[] mProjection = {"ALBUM, ARTIST, ALBUM_ART"};
		String mSelectionClause = "_ID = " + id;
		
		Cursor mCursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				mProjection, 
				mSelectionClause, 
				null, 
				null);
		
		int index = mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
		
		mCursor.moveToFirst();
		String ALBUM = mCursor.getString(index);
		
		index = mCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
		String ARTIST = mCursor.getString(index);
		
		index = mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
		String ALBUM_ART = mCursor.getString(index);
		
		mCursor.close();
		
		Intent intent = new Intent(getActivity(), AlbumSongListActivity.class);
		intent.putExtra("ALBUM", ALBUM);
		intent.putExtra("ARTIST", ARTIST);
		intent.putExtra("ALBUM_ART", ALBUM_ART);
		intent.putExtra("CALLER", "MAIN");
		startActivity(intent);
	}
	
}