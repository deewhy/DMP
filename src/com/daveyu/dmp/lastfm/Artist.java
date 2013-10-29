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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

public class Artist {

	private static final String ns = null;
	private String artist;
	private Context context;
	SignalBackgroundChange signal;
	
	
	/**
	 * Creates the Last.fm artist.getInfo URL and starts task to download
	 * and process it.
	 */
	public void getImage(String artist, Context context) {
		this.artist = artist;
		this.context = context;
		String artistURL = artist.replace("&", "%26");
		artistURL = artistURL.replace(' ', '+');
		String URL = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
				+ artistURL + "&api_key=" + getApiKey() + "&autocorrect=1";
		new DownloadTask().execute(URL);
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
	 * Method used by getImageUrlFromXml() to skip unwanted tags
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
	 * @param in
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
	
}
