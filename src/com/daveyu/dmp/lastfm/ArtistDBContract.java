package com.daveyu.dmp.lastfm;

import android.provider.BaseColumns;

public final class ArtistDBContract {
	
	public ArtistDBContract() {}
	
	public static abstract class ArtistDBEntry implements BaseColumns {
		public static final String TABLE_NAME = "artist";
		public static final String COLUMN_NAME_ARTIST = "artist";
		public static final String COLUMN_NAME_BIO = "bio";
		public static final String COLUMN_NAME_IMAGE = "image";
	}
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + ArtistDBEntry.TABLE_NAME + " (" +
			ArtistDBEntry._ID + " INTEGER PRIMARY KEY," +
			ArtistDBEntry.COLUMN_NAME_ARTIST + TEXT_TYPE + COMMA_SEP +
			ArtistDBEntry.COLUMN_NAME_BIO + TEXT_TYPE + COMMA_SEP +
			ArtistDBEntry.COLUMN_NAME_IMAGE + TEXT_TYPE +
			")";
	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + ArtistDBEntry.TABLE_NAME;
}
