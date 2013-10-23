package adapters;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ArtistListCursorAdapter extends SimpleCursorAdapter implements
		View.OnClickListener {
	
	Context context;
	Activity activity;
	private LayoutInflater mInflater;

	public ArtistListCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		 if (!mDataValid) {
	            throw new IllegalStateException("this should only be called when the cursor is valid");
	        }
	        if (!mCursor.moveToPosition(position)) {
	            throw new IllegalStateException("couldn't move cursor to position " + position);
	        }
	        View v;
	        if (convertView == null) {
	            v = mInflater.inflate(com.daveyu.dmp.R.layout.list_item, null);
	            holder = new ViewHolder();
	            holder.artistName = (TextView)v.findViewById(com.daveyu.dmp.R.id.text_1);
	            holder.playButton = (ImageView)v.findViewById(com.daveyu.dmp.R.id.list_play_button);
	            holder.header = (TextView)v.findViewById(com.daveyu.dmp.R.id.header);
	            v.setTag(holder);
	        } else {
	            v = convertView;
	        }
	        bindView(v, mContext, mCursor);  
	        return v;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        final int[] from = mFrom;
        ViewHolder holder;
        holder = (ViewHolder) view.getTag();
        
        /**
         * Used to hold first letter of current and previous cursor selections, which are used to
         * determine whether to show section header/divider or not. Section header/divider
         * is set to visible if the first letter of the current selection is different than the first
         * letter of the previous selection.
         */
        char currentStringFirstLetter, prevStringFirstLetter;
        boolean needHeader = false;
        
        String text = cursor.getString(from[0]);
        if (text == null) {text = "";}
        
        final int position = cursor.getPosition();
        
        /**
         * First item of the list will always require a header. Otherwise deal with cases where list
         * item begins with "The", and set needHeader based on whether currentStringFirstLetter matches with
         * prevStringFirstLetter.
         */
        if (position == 0) {
        	needHeader = true;
        	} else {
        		int currentStringLength = cursor.getString(from[0]).length();
        		
        		if (cursor.getString(from[0]).startsWith("The ")) {
        			currentStringFirstLetter = cursor.getString(from[0]).toLowerCase(Locale.US).charAt(4);
        			} else {
        				currentStringFirstLetter = cursor.getString(from[0]).toLowerCase(Locale.US).charAt(0);
        				}
        		
        		cursor.moveToPosition(position - 1);
        		int prevStringLength = cursor.getString(from[0]).length();
        		
        		if (cursor.getString(from[0]).startsWith("The ")) {
        			prevStringFirstLetter = cursor.getString(from[0]).toLowerCase(Locale.US).charAt(4);
        			} else {
        				prevStringFirstLetter = cursor.getString(from[0]).toLowerCase(Locale.US).charAt(0);
        				}
        		
        		if (prevStringLength > 0 && currentStringLength > 0 && prevStringFirstLetter != currentStringFirstLetter) {
        			needHeader = true;
        			}
        		
        		cursor.moveToPosition(position);
        		}
        
        String header_letter = text.substring(0, 1);
        
        /**
         * If header is needed, set visibility of header. For items that begin with '0'-'9', replace with 
         * '#' as the header. For items that begin with "The", use the first letter that comes after "The" 
         * 	as the header. 
         */
        if (needHeader) {
        	if ((int)header_letter.charAt(0) >= 48 && (int)header_letter.charAt(0) <= 57) {
        		setViewText((TextView) holder.header, "#");
        		} else if (text.startsWith("The ")) {
        			if ((int)text.charAt(4) >= 48 && (int)text.charAt(4) <= 57) {
        				setViewText((TextView) holder.header, "#");
        				} else {
        					setViewText((TextView) holder.header, text.substring(4, 5));
        					}
        			} else {
        				setViewText((TextView) holder.header, text.substring(0, 1));
        				}
        	holder.header.setVisibility(View.VISIBLE);
        	ElementType headerElement = new ElementType();
        	headerElement.type = "HEADER";
        	holder.header.setTag(headerElement);
        	holder.header.setOnClickListener(this);
        	} else {
        		holder.header.setVisibility(View.GONE);
        		}
        
        /**
         * Tag information inside 'Play all' button.
         */
        setViewText((TextView) holder.artistName, text);
        ElementType playAllButton = new ElementType();
        playAllButton.type = "PLAY_ALL_BUTTON";
        playAllButton.artistName = text;
        holder.playButton.setTag(playAllButton);
        holder.playButton.setOnClickListener(this);
                    
                
    }
	
	static class ViewHolder {
		private TextView header;
		private TextView artistName;
		private ImageView playButton;
	}
	
	/**
	 * Used to hold tag data used by onClick() to determine what type of element
	 * was clicked and which row was clicked.
	 */
	static class ElementType {
		String type;
		String artistName;
	}
	
	
	/**
	 * Placeholder
	 */
	@Override
	public void onClick(View v) {
		ElementType buttonType;
		buttonType = (ElementType) v.getTag();
		if (buttonType.type == "HEADER") {
			Toast.makeText(v.getContext(), "Clicked the header", Toast.LENGTH_SHORT).show();	
		} else {
		 Toast.makeText(v.getContext(), "Clicked the play button for " + buttonType.artistName, Toast.LENGTH_SHORT).show(); }

	}

}
