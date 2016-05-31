package com.gagnon.mario.mr.traveloid.traveler;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;

public class TravelersList extends ListActivity {

	private SingleChoiceEventHandler mMessageBoxResponseHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {
			TravelersList.this.finish();
		}
	};

	private TravelerEditor.OnSaveListener OnSaveListener = new TravelerEditor.OnSaveListener() {
		@Override
		public void save(Traveler traveler) {

			try {
				mDbAdapter.travelerSave(traveler);
				if (traveler.isNew()) {
					//TODO bug potentiel le traverler ajoute n'est pas initialise completement, on ne recupere pas le new traverler lors de l'ajout
					mTravelerList.add(traveler);
				}

				((BaseAdapter) TravelersList.this.getListAdapter())
						.notifyDataSetChanged();
			} catch (Exception exception) {
				DialogUtils.messageBox(TravelersList.this,
						getString(R.string.error_saving_item),
						getString(R.string.activity_travelers_list)).show();

			}

		}
	};

	public static final int MENU_ITEM_DELETE = Menu.FIRST;

	private List<Traveler> mTravelerList;
	private DbAdapter mDbAdapter;
	private Traveler mTravelerSelected;

	private void setupListView() throws RepositoryException {

		LayoutInflater vi = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View emptyView = vi.inflate(R.layout.traveler_row_no_item, null);
		emptyView.setVisibility(View.GONE);

		/*
		 * RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		 * RelativeLayout.LayoutParams.WRAP_CONTENT,
		 * RelativeLayout.LayoutParams.FILL_PARENT);
		 * lp.addRule(RelativeLayout.BELOW, R.id.buttonAddExpense);
		 */

		((ViewGroup) this.getListView().getParent()).addView(emptyView);
		this.getListView().setEmptyView(emptyView);

		mTravelerList = mDbAdapter.travelerFetchAll();

		setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mTravelerList));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				int i = 0;
				for (Traveler traveler : mTravelerList) {
					if (i == position) {
						mTravelerSelected = traveler;
						break;
					}
					i++;
				}

				showTravelerEditor(mTravelerSelected);

			}
		});

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
											ContextMenuInfo menuInfo) {
				int position = ((AdapterContextMenuInfo) menuInfo).position;

				// Setup the menu header
				int i = 0;
				for (Traveler traveler : mTravelerList) {
					if (i == position) {
						menu.setHeaderTitle(traveler.toString());
						break;
					}
					i++;
				}

				// Add a menu item to delete the note
				menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);

			}
		});

	}

	private void showTravelerEditor(Traveler traveler) {

		Dialog dialog;

		if (null == traveler.getId()) {

			dialog = new TravelerEditor(this, traveler, OnSaveListener);
		} else {

			dialog = new TravelerEditor(this, mTravelerSelected, OnSaveListener);
		}

		dialog.setOwnerActivity(this);
		dialog.show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			mDbAdapter = new DbAdapterImpl(this);

			setupListView();

			if (mTravelerList.size() == 0) {
				showTravelerEditor(Traveler.createNew());
			}

			setTitle(String.format("%s-%s", getTitle(), this.getResources()
					.getString(R.string.title_travelers)));
		} catch (Exception ex) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_travelers_list),
					mMessageBoxResponseHandler);
			ad.show();
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;

		try {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			// Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {

			Traveler toDelete = null;
			int i = 0;
			for (Traveler traveler : mTravelerList) {
				if (i == info.position) {
					toDelete = traveler;
					break;
				}
				i++;
			}

			if (null != toDelete) {
				toDelete.setDead(true);
				try {
					mDbAdapter.travelerSave(toDelete);
					mTravelerList.remove(toDelete);
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				} catch (Exception exception) {
					DialogUtils.messageBox(this,
							getString(R.string.error_deleting_item),
							getString(R.string.activity_travelers_list)).show();
				}

			}
			return true;
		}
		}
		return false;
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
