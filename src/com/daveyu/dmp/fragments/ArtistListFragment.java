package com.daveyu.dmp.fragments;

import com.daveyu.dmp.adapters.ArtistListCursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.daveyu.dmp.ArtistActivity;

public class ArtistListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 0;
	private ArtistListCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
		
		String[] mProjection = {
				MediaStore.Audio.Artists.ARTIST
			};
		
		int[] mTo = {
				com.daveyu.dmp.R.id.text_1
			};
		
		adapter = new ArtistListCursorAdapter(
				getActivity().getApplicationContext(),
				com.daveyu.dmp.R.layout.list_item,
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
				MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.ARTIST
			};
		
		String sortOrder = "ARTIST_KEY";
		
		CursorLoader cursorLoader = new CursorLoader(
				getActivity(),
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
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
		String[] mProjection = {"ARTIST"};
		String mSelectionClause = "_ID = " + id;
		
		Cursor mCursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, 
				mProjection, 
				mSelectionClause, 
				null, 
				null);
		
		int index = mCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
		
		mCursor.moveToFirst();
		String ARTIST = mCursor.getString(index);
		
		mCursor.close();
		
		Intent intent = new Intent(getActivity(), ArtistActivity.class);
		intent.putExtra("ARTIST", ARTIST);
		startActivity(intent);
	}
	
}