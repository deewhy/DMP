package com.daveyu.dmp.lastfm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Xml;

public class Artist {

	private static final String ns = null;
	private String artist;
	private Context context;
	SignalBackgroundChange signal;
	SignalBioChange signal2;
	
	
	/**
	 * Creates the Last.fm artist.getInfo URL and starts task to download
	 * and process it.
	 */
	public void getImage(String artist, Context context) {
		if (artist.equals(null)) {
			
		} else {
		this.artist = artist;
		this.context = context;
		String artistURL = artist.replace("&", "%26");
		artistURL = artistURL.replace(' ', '+');
		String URL = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
				+ artistURL + "&api_key=" + getApiKey() + "&autocorrect=1";
		new DownloadTask().execute(URL);
		}
	}
	
	public void getBio(String artist, Context context) {
		this.artist = artist;
		this.context = context;
		String artistURL = artist.replace("&", "%26");
		artistURL = artistURL.replace(' ', '+');
		String URL = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
				+ artistURL + "&api_key=" + getApiKey() + "&autocorrect=1";
		new AccessDbTask().execute(URL);
	}
	
	/**
	 * Parses downloaded XML file to look for URL of artist image.
	 * Looks for the tag: <image size="mega"></image>
	 */
	public String getImageUrlFromXml(InputStream in) throws XmlPullParserException, IOException, Exception {
		String imageURL = "";
		try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in, null);
				parser.nextTag();	//skip xml version info
				parser.nextTag();	//skip lfm status
				
				parser.require(XmlPullParser.START_TAG, ns, "artist");
				while (parser.next() != XmlPullParser.END_TAG) {
					if (parser.getEventType() != XmlPullParser.START_TAG) {
						continue;
					}
					String name = parser.getName();
					if (name.equals("image")) {
						String size = parser.getAttributeValue(0);
						if (size.equals("mega")) {
							if (parser.next() == XmlPullParser.TEXT) {
								imageURL = parser.getText();
							}
							return imageURL;
						}
						else {
							skip(parser);
						}
					} else {
						skip(parser);
					}	
				}
				return imageURL;
			} finally {
				in.close();
			}
	}
	
	/**
	 * Parses downloaded XML file to look for artist biography.
	 * Looks for the tag: <bio><summary></summary></bio>
	 */
	public String getBioFromXml(InputStream in) throws XmlPullParserException, IOException {
		String bio = "";
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			parser.nextTag();
			
			parser.require(XmlPullParser.START_TAG, ns, "artist");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				
				String name = parser.getName();
				if (name.equals("bio")) {
					
					while (parser.next() != XmlPullParser.END_TAG) {
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						}
						String name2 = parser.getName();
						if (name2.equals("summary")) {
							if (parser.next() == XmlPullParser.TEXT) {
								bio = parser.getText();
							}
							return bio;
						} else {
							skip(parser);
						}
					}	
					
				} else {
					skip(parser);
				}
			}
			return bio;
		} finally {
			in.close();
		}
	}

	/**
	 * Method used by getImageUrlFromXml() and getBioFromXml() to skip unwanted tags
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
				}
			}
		}
	
	/**
	 * Downloads image to /Files/artistpics/
	 */
	public void getImageFromUrl(InputStream in) {
		FileOutputStream out = null;
		
		File artistPicDirectory = new File(context.getExternalFilesDir(null) + File.separator + "artistpics");
		artistPicDirectory.mkdir();
		File file = new File(artistPicDirectory, artist + ".jpg");
		
		try {
			out = new FileOutputStream(file);
			
			int c;
			while  ((c = in.read()) != -1) {
				out.write(c);
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					} catch (IOException e) {
							e.printStackTrace();
						}
		            } if (out != null) {
		            	try {
		            		out.close();
		            	} catch (IOException e) {
							e.printStackTrace();
						}
		            }
				}
			
	    }

	/**
	 * AsyncTask class
	 * 
	 * In background: 
	 * Calls method to download XML
	 * Calls method to parse XML for image
	 * Calls method to download image
	 * 
	 * Post execute:
	 * Calls ArtistActivity to show downloaded image as background
	 */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			InputStream xmlStream = null;
			InputStream imageStream = null;
			String imageUrl;
			
			try {
				xmlStream = downloadUrl(urls[0]);
				imageUrl = getImageUrlFromXml(xmlStream);
				imageStream = downloadUrl(imageUrl);
				getImageFromUrl(imageStream);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
					
		            if (xmlStream != null) {
		                try {
							xmlStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		            
		            if (imageStream != null) {
		                try {
							imageStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        }
				return null;
			}
			
		protected void onPostExecute(String result) {
			signal = (SignalBackgroundChange) context;
			signal.tryChangeBackground();
		}   	
	}
	
	/**
	 * AsyncTask class
	 * 
	 * In background:
	 * Returns biography string if it already exists in the database
	 * Otherwise, 
	 * Calls method to download XML
	 * Calls method to parse and return biography string from XML
	 * Returns biography string and saves it in database
	 * 
	 * Post execute:
	 * Calls ArtistActivity to pass biography string
	 */
	private class AccessDbTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			String summary;
			InputStream xmlStream = null;
			
			ArtistDBHelper mDBHelper = new ArtistDBHelper(context);
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			String[] projection = {
					ArtistDBContract.ArtistDBEntry._ID,
					ArtistDBContract.ArtistDBEntry.COLUMN_NAME_ARTIST,
					ArtistDBContract.ArtistDBEntry.COLUMN_NAME_BIO
					};
			
			String[] selectionArgs = {""};
			selectionArgs[0] = artist;
			String selection = ArtistDBContract.ArtistDBEntry.COLUMN_NAME_ARTIST + 
					" = ? AND " + 
					ArtistDBContract.ArtistDBEntry.COLUMN_NAME_BIO + " IS NOT NULL";
			
			Cursor c = db.query(
					ArtistDBContract.ArtistDBEntry.TABLE_NAME, 
					projection, 
					selection, 
					selectionArgs, 
					null, 
					null, 
					null
					);
			
			c.moveToFirst();
			
			if (c.getCount() == 1) {
				int index = c.getColumnIndex(ArtistDBContract.ArtistDBEntry.COLUMN_NAME_BIO);
				summary = c.getString(index);
				db.close();
				c.close();
				return summary;
			} else {
				try {
					xmlStream = downloadUrl(urls[0]);
					summary = getBioFromXml(xmlStream);
					ContentValues values = new ContentValues();
					values.put(ArtistDBContract.ArtistDBEntry.COLUMN_NAME_ARTIST, artist);
					values.put(ArtistDBContract.ArtistDBEntry.COLUMN_NAME_BIO, summary);
					db.insert(ArtistDBContract.ArtistDBEntry.TABLE_NAME, null, values);
					db.close();
					c.close();
					return summary;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} finally {
					if (xmlStream != null) {
		                try {
							xmlStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
				}
				
			}
			return null;
		}
		
		protected void onPostExecute(String result) {
			signal2 = (SignalBioChange) context;
			signal2.tryChangeBio(result);
		}
		
	}
	
	
	/**
	 * Returns InputStream of given URL
	 */
	private InputStream downloadUrl(String urlString) throws IOException {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	        
	    conn.connect();
	    InputStream stream = conn.getInputStream();
	    return stream;
	    }
	
	/**
	 * Retreives Last.fm API key from /assets/apikey.txt
	 */
	private String getApiKey() {
		InputStream in = null;
		InputStreamReader reader = null;
		StringBuilder key = new StringBuilder();
		
		try {
			in = context.getAssets().open("apikey.txt");
			reader = new InputStreamReader(in);
			int c;
			while  ((c = reader.read()) != -1) {
				String d = Character.toString((char) c);
				key.append(d);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				 if (in != null) {
		                try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		            if (reader != null) {
		                try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
			}
	    	return key.toString();
	}
	
	/**
	 * Callback interface signaling Activity to change background image
	 */
	public interface SignalBackgroundChange {
	    	public void tryChangeBackground();
	   	}
	public interface SignalBioChange {
			public void tryChangeBio(String summary);
	}
	
	/**
	 * Database helper class
	 */
	public class ArtistDBHelper extends SQLiteOpenHelper {
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "artist.db";
		
		public ArtistDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(ArtistDBContract.SQL_CREATE_ENTRIES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(ArtistDBContract.SQL_DELETE_ENTRIES);
		}
		
		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onUpgrade(db, oldVersion, newVersion);
		}
	}
	
	
	
	
}
