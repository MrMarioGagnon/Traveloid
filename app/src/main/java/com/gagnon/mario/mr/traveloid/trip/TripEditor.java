/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gagnon.mario.mr.traveloid.trip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.ActivityValidationStatus;
import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.Tools;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.MultipleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.traveler.Traveler;
import com.gagnon.mario.mr.traveloid.traveler.TravelerEditor;

public class TripEditor extends Activity {

	private SingleChoiceEventHandler mMessageBoxResponseHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {
			TripEditor.this.finish();
		}
	};

	private TravelerEditor.OnSaveListener OnSaveListener = new TravelerEditor.OnSaveListener() {
		@Override
		public void save(Traveler traveler) {
			addTraveler(traveler);
		}
	};

	private DbAdapter mDbAdapter;

	private ImageButton mImageButtonTraveler;
	private EditText mEditTextDestination;
	private TextView mTextViewDate;
	private TextView mTextViewTraveler;
	private TextView mTextViewMessage;
	private Trip mCurrentTrip = null;
	private Spinner mSpinnerCurrency;

	private CharSequence[] mTravelerArray;
	static final int DATE_DIALOG_ID = 1;

	private MultipleChoiceEventHandler mPositiveHandler = new MultipleChoiceEventHandler() {

		@Override
		public void execute(boolean[] idSelected) {

			SortedSet<String> travelerSet = new TreeSet<>();

			boolean checked;
			for (int i = 0; i < idSelected.length; i++) {

				checked = idSelected[i];

				if (checked) {
					travelerSet.add(mTravelerArray[i].toString());
				}

			}

			mCurrentTrip.setTravelerSet(travelerSet);
			mTextViewTraveler.setText(mCurrentTrip.getTravelerForDisplay());

		}

	};

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			updateTextViewDate(year, monthOfYear, dayOfMonth);
		}
	};

	SingleChoiceEventHandler mDeleteConfirmationHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {

			mCurrentTrip.setDead(true);
			boolean resetDefault = mCurrentTrip.getDefault();
			try {
				mDbAdapter.tripSave(mCurrentTrip);

				Bundle bundle = new Bundle();
				bundle.putBoolean("resetDefault", resetDefault);

				Intent intent = new Intent();
				intent.putExtras(bundle);

				setResult(RESULT_OK, intent);
			} catch (RepositoryException e) {
				DialogUtils
						.messageBox(
								TripEditor.this,
								TripEditor.this
										.getString(R.string.error_deleting_item),
								TripEditor.this
										.getString(R.string.activity_trip_editor))
						.show();
				setResult(RESULT_CANCELED);
			}

			finish();
		}
	};

	private OnClickListener mButtonSaveOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ActivityValidationStatus status = ValidateActivity();

			if (status.isValid()) {

				mTextViewMessage.setVisibility(View.GONE);

				mCurrentTrip.setDestination(mEditTextDestination.getText()
						.toString());
				mCurrentTrip.setDate(mTextViewDate.getText().toString());

				mCurrentTrip.setTravelerSet(mTextViewTraveler.getText()
						.toString());

				mCurrentTrip.setCurrency((String) mSpinnerCurrency
						.getSelectedItem());

				try {
					mDbAdapter.tripSave(mCurrentTrip);
					
					setResult(RESULT_OK);					
				} catch (RepositoryException e) {
					DialogUtils
							.messageBox(
									TripEditor.this,
									getString(R.string.error_unable_to_save_object_to_database),
									getString(R.string.activity_trip_editor))
							.show();
					setResult(RESULT_CANCELED);					
				}
				finish();
			} else {
				mTextViewMessage.setText(status.getMessage());
				mTextViewMessage.setVisibility(View.VISIBLE);
			}

		}
	};

	private OnClickListener mButtonDeleteOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog ad = DialogUtils
					.twoButtonMessageBox(
							TripEditor.this,
							getString(R.string.message_are_you_sure_you_want_to_delete_this_trip),
							getString(R.string.activity_trip_editor),
							mDeleteConfirmationHandler,
							SingleChoiceEventHandler.NO_OP);

			ad.show();

		}
	};

	private OnClickListener mButtonBackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}
	};

	private OnClickListener mImageButtonDateOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);
		}

	};

	private void addTraveler(Traveler traveler) {

		try {
			Traveler newTraveler = mDbAdapter.travelerSave(traveler);

			if (mTextViewTraveler.length() == 0) {
				SortedSet<String> travelerSet = new TreeSet<>();
				travelerSet.add(newTraveler.getName());
				mCurrentTrip.setTravelerSet(travelerSet);
				mTextViewTraveler.setText(newTraveler.getName());
			}

			if (mDbAdapter.travelerCount() > 1)
				mImageButtonTraveler.setEnabled(true);
		} catch (Exception exception) {
			DialogUtils.messageBox(this, getString(R.string.error_saving_item),
					getString(R.string.activity_trip_editor)).show();

		}

	}

	private boolean[] buildTravelersCheckedArray(List<Traveler> travelersList,
			String travelersName) {

		boolean[] checked = new boolean[travelersList.size()];

		int i = 0;
		for (Traveler traveler : travelersList) {

			checked[i] = travelersName.contains(traveler.getName());
			i++;
		}

		return checked;

	}

	private void setSpinnerCurrency(String currency) {

		Adapter adapter = mSpinnerCurrency.getAdapter();

		for (int i = 0; i < adapter.getCount(); i++) {

			String item = (String) adapter.getItem(i);
			if (null != item) {
				if (item.equals(currency)) {
					mSpinnerCurrency.setSelection(i);
					break;
				}
			}

		}

	}

	private void setupEditTextDestination() {
		mEditTextDestination = (EditText) findViewById(R.id.editTextDestination);
	}

	private void setupImageButtonTraveler() {

		mImageButtonTraveler = (ImageButton) findViewById(R.id.imageButtonTraveler);
		// add a click listener to the button
		mImageButtonTraveler.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTravelerSetterDialog();
			}
		});

	}

	private void setupSpinnerCurrency() {

		mSpinnerCurrency = (Spinner) findViewById(R.id.spinnerCurrency);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.currency_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCurrency.setAdapter(adapter);

	}

	private void setupTextViewDate() {
		mTextViewDate = (TextView) findViewById(R.id.textViewDate);
	}

	private void setupTextViewTraveler() {
		mTextViewTraveler = (TextView) findViewById(R.id.textViewTraveler);
	}

	private void showTravelerEditor(Traveler traveler) {

		Dialog dialog = new TravelerEditor(this, traveler, OnSaveListener);

		dialog.setOwnerActivity(this);
		dialog.show();

	}

	private void showTravelerSetterDialog() {

		try {
			List<Traveler> travelersList = mDbAdapter.travelerFetchAll();

			mTravelerArray = new CharSequence[travelersList.size()];
			int i = 0;
			for (Traveler traveler : travelersList) {
				mTravelerArray[i++] = traveler.getName();
			}

			Dialog dialog = DialogUtils.travelerSetterDialog(
					this,
					mTravelerArray,
					mPositiveHandler,
					buildTravelersCheckedArray(travelersList,
							mCurrentTrip.getTravelerForDisplay()));

			dialog.setOwnerActivity(this);
			dialog.show();
		} catch (Exception exception) {
			DialogUtils.messageBox(this,
					getString(R.string.error_unable_to_fetch_all_trip),
					getString(R.string.activity_trip_editor)).show();

		}

	}

	private void updateTextViewDate(int year, int month, int day) {
		mTextViewDate.setText(String.format("%d-%02d-%02d", year, month + 1,
				day));
	}

	private ActivityValidationStatus ValidateActivity() {

		List<String> messages = new ArrayList<>();

		if (mEditTextDestination.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_destination_is_mandatory));
		}

		if (mTextViewDate.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_date_is_mandatory));
		}

		if (mTextViewTraveler.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_traveler_is_mandatory));
		}

		return ActivityValidationStatus.create(Tools.join(messages, "\n"));

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			mDbAdapter = new DbAdapterImpl(this);

			setContentView(R.layout.trip_editor);

			setupImageButtonTraveler();
			setupEditTextDestination();
			setupTextViewDate();
			setupTextViewTraveler();
			setupSpinnerCurrency();

			Button buttonBack = (Button) findViewById(R.id.buttonBack);
			buttonBack.setOnClickListener(mButtonBackOnClickListener);

			Button buttonSave = (Button) findViewById(R.id.buttonSave);
			buttonSave.setOnClickListener(mButtonSaveOnClickListener);

			Button buttonDelete = (Button) this.findViewById(R.id.buttonDelete);
			buttonDelete.setOnClickListener(mButtonDeleteOnClickListener);

			ImageButton imageButtonDate = (ImageButton) findViewById(R.id.imageButtonDate);
			imageButtonDate.setOnClickListener(mImageButtonDateOnClickListener);

			mTextViewMessage = (TextView) findViewById(R.id.textViewMessage);

			Bundle bundle = getIntent().getExtras();

			String activityTitle;
			int year;
			int month;
			int day;

			if (null != bundle) {
				Long tripId = (Long) bundle.getSerializable("tripId");

				if (null != tripId)
					mCurrentTrip = mDbAdapter.tripFetch(tripId);
			}

			if (null != mCurrentTrip) {

				activityTitle = this.getResources().getString(
						R.string.edit_trip);

				mEditTextDestination.setText(mCurrentTrip.getDestination());
				mTextViewTraveler.setText(mCurrentTrip.getTravelerForDisplay());

				setSpinnerCurrency(mCurrentTrip.getCurrency());

				DatePart datePart;
				try {
					datePart = mCurrentTrip.getDatePart();
				} catch (ParseException e) {
					datePart = new DatePart(2000, 0, 1);
				}
				year = datePart.getYear();
				month = datePart.getMonth();
				day = datePart.getDay();

			} else {

				// New Trip

				activityTitle = this.getResources()
						.getString(R.string.new_trip);

				// get the current date
				Calendar c = Calendar.getInstance();
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
				mCurrentTrip = Trip.createNew();
				mCurrentTrip.setDefault(getIntent().getBooleanExtra(
						"isDefault", false));

				// Ne pas afficher le bouton Delete
				buttonDelete.setVisibility(View.GONE);

			}

			this.setTitle(activityTitle);

			updateTextViewDate(year, month, day);

			if (mDbAdapter.travelerCount() == 0) {
				mImageButtonTraveler.setEnabled(false);
				showTravelerEditor(Traveler.createNew());
			}
		} catch (Exception exception) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_trip_editor),
					mMessageBoxResponseHandler);
			ad.show();

		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			String[] dateParts = mTextViewDate.getText().toString().split("-");
			int year = Integer.valueOf(dateParts[0].trim());
			int month = Integer.valueOf(dateParts[1].trim()) - 1;
			int day = Integer.valueOf(dateParts[2].trim());
			return new DatePickerDialog(this, mDateSetListener, year, month,
					day);

		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.travelers_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.travelers_list_add:
			showTravelerEditor(Traveler.createNew());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
