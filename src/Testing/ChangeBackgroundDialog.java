package Testing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ChangeBackgroundDialog extends DialogFragment {
	
	ChangeBackgroundDialogListener listener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		CharSequence[] source = {"Choose from gallery", "Fetch from web"};
		builder.setTitle("Select source").setItems(source, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					listener.sourceGallery(ChangeBackgroundDialog.this);
					break;
				case 1:
					listener.sourceWeb(ChangeBackgroundDialog.this);
					break;
				}
				
			}
		});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (ChangeBackgroundDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ChangeBackgroundDialogListener"); 
		}
	}
	
	public interface ChangeBackgroundDialogListener {
		public void sourceGallery(DialogFragment dialog);
		public void sourceWeb(DialogFragment dialog);
	}
	
}
