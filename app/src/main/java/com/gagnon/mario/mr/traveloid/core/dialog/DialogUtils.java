package com.gagnon.mario.mr.traveloid.core.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.widget.ListView;

import com.gagnon.mario.mr.traveloid.R;

public class DialogUtils {

	private static final OnMultiChoiceClickListener multiChoiceClickListener =

	new OnMultiChoiceClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {

			((AlertDialog) dialog).getListView().setItemChecked(which,
					isChecked);

		}
	};

	public static AlertDialog availableCategoryDialog(final Context context,
			final CharSequence[] adapter,
			final SingleChoiceEventHandler clickCommand) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setItems(adapter, new SingleChoiceEventListener(clickCommand))
				.setTitle(R.string.dialog_title_select_to_edit)
				.setInverseBackgroundForced(true);

		return builder.create();
	}

	public static AlertDialog messageBox(final Context context,
			final String message, final String title) {

		return singleButtonMessageBox(context, message, title,
				SingleChoiceEventHandler.NO_OP);

	}

	public static AlertDialog singleButtonMessageBox(final Context context,
			final String message, final String title,
			final SingleChoiceEventHandler clickCommand) {

		return twoButtonMessageBox(context, message, title, clickCommand, null);

	}

	public static AlertDialog singleChoicePickerDialog(final Context context,
			final String title, final CharSequence[] adapter,
			final SingleChoiceEventHandler positiveCommand,
			final int itemSelectedId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(true)
				.setSingleChoiceItems(adapter, itemSelectedId,
						new SingleChoiceEventListener(positiveCommand))
				.setTitle(title).setInverseBackgroundForced(true);

		return builder.create();
	}

	public static AlertDialog travelerSetterDialog(final Context context,
			final CharSequence[] adapter,
			final MultipleChoiceEventHandler positiveHandler,
			final boolean[] checked) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(true)
				.setMultiChoiceItems(adapter, checked,
						DialogUtils.multiChoiceClickListener)
				.setTitle(R.string.dialog_title_traveler_setter)
				.setInverseBackgroundForced(true)
				.setPositiveButton(R.string.ok,
						new MultipleChoiceEventListener(positiveHandler))
				.setNegativeButton(
						R.string.cancel,
						new SingleChoiceEventListener(
								SingleChoiceEventHandler.NO_OP));

		AlertDialog ad = builder.create();

		ListView lv = ad.getListView();

		for (int i = 0; i < checked.length; i++) {
			lv.setItemChecked(i, checked[i]);
		}

		return ad;

	}

	public static AlertDialog twoButtonMessageBox(final Context context,
			final String message, final String title,
			final SingleChoiceEventHandler firstCommand,
			final SingleChoiceEventHandler secondCommand) {

		int secondButtonResId = null == secondCommand ? android.R.string.ok
				: android.R.string.yes;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setPositiveButton(secondButtonResId,
						new SingleChoiceEventListener(firstCommand))
				.setTitle(title);

		if (null != secondCommand) {
			builder.setNegativeButton(android.R.string.no,
					new SingleChoiceEventListener(secondCommand));

		}

		return builder.create();

	}

}
