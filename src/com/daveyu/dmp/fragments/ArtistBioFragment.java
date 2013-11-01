package com.daveyu.dmp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daveyu.dmp.R;
import com.daveyu.dmp.fragments.ArtistAlbumListFragment.PassLabel;

public class ArtistBioFragment extends Fragment {
	
	PassLabel label_passer;
	GetBio get_bio;
	CharSequence ARTIST_BIO_SUMMARY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			ARTIST_BIO_SUMMARY = savedInstanceState.getCharSequence("ARTIST_BIO_KEY");
		} else {
			String bio_string = get_bio.getBio().replace("\n", "<br>");
			bio_string = bio_string.replace("\r", "<br>");
			ARTIST_BIO_SUMMARY = Html.fromHtml("<br> " + bio_string);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_artist_bio, container, false);
		TextView textView = (TextView) v.findViewById(R.id.artist_bio);
		textView.setText(ARTIST_BIO_SUMMARY);
		textView.setTextSize(14);
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putCharSequence("ARTIST_BIO_KEY", ARTIST_BIO_SUMMARY);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
            get_bio = (GetBio) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
	}
	
	public interface GetBio {
		public String getBio();
	}

}
