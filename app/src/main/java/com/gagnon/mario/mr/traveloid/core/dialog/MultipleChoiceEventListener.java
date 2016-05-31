package com.gagnon.mario.mr.traveloid.core.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;

public class MultipleChoiceEventListener implements DialogInterface.OnClickListener {

	private MultipleChoiceEventHandler mHandler;

	public MultipleChoiceEventListener(MultipleChoiceEventHandler handler) {
		this.mHandler = handler;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();
		
		AlertDialog ad = (AlertDialog) dialog;
		
		ListView lv = ad.getListView();
		
		boolean[] itemChecked = new boolean[lv.getCount()];
		for(int i = 0; i < lv.getCount(); i++){
			itemChecked[i] = lv.isItemChecked(i);
		}
		
		mHandler.execute(itemChecked);
	}

}
