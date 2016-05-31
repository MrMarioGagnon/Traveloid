package com.gagnon.mario.mr.traveloid.core.dialog;

import android.content.DialogInterface;

public class SingleChoiceEventListener implements DialogInterface.OnClickListener {

	private SingleChoiceEventHandler mCommand;

	public SingleChoiceEventListener(SingleChoiceEventHandler command) {
		this.mCommand = command;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();

		mCommand.execute(which);
	}

}
