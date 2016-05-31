package com.gagnon.mario.mr.traveloid.trip;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;

public class TripsList extends ListActivity {

	public static final int TRIP_EDITOR = 0;

	private DbAdapter mDbAdapter = null;
	private boolean mListWasUpdated = false;

	private List<Trip> mTripsList = new ArrayList<>();

	private SingleChoiceEventHandler mMessageBoxOkButtonClickHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {
			TripsList.this.finish();

		}

	};

	private SingleChoiceEventHandler mDefaultTripSelectedHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {

			if (idSelected >= 0) {
				Trip newDefaultTrip = mTripsList.get(idSelected);

				if (null != newDefaultTrip) {
					try {
						mDbAdapter.tripChangeDefault(newDefaultTrip);

						mTripsList = mDbAdapter.tripFetchAll();

						ListView lv = TripsList.this.getListView();
						TripAdapter adapter = new TripAdapter(TripsList.this,
								mTripsList);
						lv.setAdapter(adapter);
						mListWasUpdated = true;

					} catch (RepositoryException e) {

						DialogUtils.messageBox(TripsList.this, e.getMessage(),
								getString(R.string.activity_trips_list)).show();

					}
				}
			}

		}

	};

	/***
	 * Handler when selecting a trip to edit from dialog
	 */
	private SingleChoiceEventHandler mTripSelectedHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {

			Context context = TripsList.this;

			try {
				Trip tripToBeUpdated = mTripsList.get(idSelected);
				Long tripIdToBeUpdated = tripToBeUpdated.getId();

				Intent i = new Intent(TripsList.this, TripEditor.class);

				Bundle bundle = new Bundle();
				bundle.putSerializable("tripId", tripIdToBeUpdated);
				i.putExtras(bundle);

				startActivityForResult(i, TRIP_EDITOR);

			} catch (IndexOutOfBoundsException ex) {
				DialogUtils.messageBox(context,
						context.getString(R.string.error_trip_not_found),
						context.getString(R.string.title_trips)).show();
			}

		}

	};

	private void showAvailableTripDialog() {

		CharSequence[] tripNameList = new CharSequence[mTripsList.size()];

		for (int i = 0; i < mTripsList.size(); i++) {
			tripNameList[i] = mTripsList.get(i).toString();
		}

		AlertDialog ad = com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils
				.singleChoicePickerDialog(this,
						getString(R.string.title_select_trip), tripNameList,
						mTripSelectedHandler, -1);

		ad.setOwnerActivity(this);
		ad.show();

	}

	private void showDefaultTripDialog() {
		CharSequence[] tripNameList = new CharSequence[mTripsList.size()];

		Trip trip;
		int defaultId = 0;
		for (int i = 0; i < mTripsList.size(); i++) {

			trip = mTripsList.get(i);
			if (trip.getDefault())
				defaultId = i;

			tripNameList[i] = trip.toString();
		}

		AlertDialog ad = DialogUtils.singleChoicePickerDialog(this,
				getString(R.string.title_select_default_trip), tripNameList,
				mDefaultTripSelectedHandler, defaultId);

		ad.setOwnerActivity(this);
		ad.show();

	}

	private void showTripEditor() {
		Intent i = new Intent(this, TripEditor.class);
		i.putExtra("isDefault", mTripsList.size() == 0);
		startActivityForResult(i, TRIP_EDITOR);
	}

	/***
	 * Comming back from Trip editor
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode != RESULT_OK)
			return;

		// Reload list with updated trip
		try {

			if (null != intent) {
				if (intent.getBooleanExtra("resetDefault", false))
					// Reset default trip
					mDbAdapter.tripFetchDefault();
			}

			mTripsList = mDbAdapter.tripFetchAll();

			ListView lv = this.getListView();

			TripAdapter adapter = new TripAdapter(this, mTripsList);

			lv.setAdapter(adapter);
			
			mListWasUpdated = true;

		} catch (RepositoryException e) {
			DialogUtils.messageBox(this,
					getString(R.string.error_unable_to_fetch_all_trip),
					getString(R.string.activity_trips_list)).show();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mDbAdapter = new DbAdapterImpl(this);

			LayoutInflater vi = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View emptyView = vi.inflate(R.layout.trip_row_no_item, null);
			emptyView.setVisibility(View.GONE);

			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			((ViewGroup) lv.getParent()).addView(emptyView);
			this.getListView().setEmptyView(emptyView);

			mTripsList = mDbAdapter.tripFetchAll();

			TripAdapter adapter = new TripAdapter(this, mTripsList);

			lv.setAdapter(adapter);

			setTitle(String.format("%s-%s", getTitle(), this.getResources()
					.getString(R.string.title_trips)));

		} catch (Exception e) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_trips_list),
					mMessageBoxOkButtonClickHandler);
			ad.show();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Trip selectedTrip = mTripsList.get(position);

		if (null != selectedTrip) {

			Long selectedId = selectedTrip.getId();

			Bundle bundle = new Bundle();
			bundle.putSerializable("selectedId", selectedId);

			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();

		}

	}

	@Override
	public void onBackPressed() {

		setResult( mListWasUpdated ? RESULT_OK : RESULT_CANCELED);
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.trips_list_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int menuId = item.getItemId();
		switch (menuId) {
		case R.id.option_menu_item_add:
			showTripEditor();
			return true;
		case R.id.option_menu_item_update:

			if (mTripsList.size() == 1) {
				mTripSelectedHandler.execute(0);
			} else {
				showAvailableTripDialog();
			}
			return true;
		case R.id.option_menu_item_default:
			showDefaultTripDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		int listSize = mTripsList.size();

		menu.getItem(1).setVisible(listSize > 0);// update
		menu.getItem(2).setVisible(listSize > 1);// default

		return super.onPrepareOptionsMenu(menu);
	}
}
