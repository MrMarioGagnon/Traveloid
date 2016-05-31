package com.gagnon.mario.mr.traveloid.expense;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.trip.Trip;

public class ExpensesList extends ListActivity {

	private List<Expense> mExpensesList = new ArrayList<>();
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int EXPENSE_EDITOR = 0;
	private Trip mCurrentTrip;
	private DbAdapter mDbAdapter;

	private SingleChoiceEventHandler mMessageBoxResponseHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {
			ExpensesList.this.finish();
		}
	};

	private OnItemClickListener mExpenseListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			try {
				Expense expense = mExpensesList.get(position);

				Intent i = new Intent(parent.getContext(), ExpenseEditor.class);

				Bundle bundle = new Bundle();
				bundle.putSerializable("trip", mCurrentTrip);
				bundle.putSerializable("expense", expense);
				i.putExtras(bundle);

				startActivityForResult(i, EXPENSE_EDITOR);

			} catch (IndexOutOfBoundsException ex) {

				Toast.makeText(getApplicationContext(), "Expense not found",
						Toast.LENGTH_SHORT).show();

			}

		}
	};

	private void showExpenseEditor() {
		Intent i = new Intent(this, ExpenseEditor.class);

		Bundle bundle = new Bundle();
		bundle.putSerializable("trip", mCurrentTrip);
		i.putExtras(bundle);

		startActivityForResult(i, EXPENSE_EDITOR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		Bundle bundle = data.getExtras();

		switch (requestCode) {
		case EXPENSE_EDITOR:

			Expense expense = (Expense) bundle.getSerializable("expense");

            Boolean isNewExpense = expense.isNew();

			try {
				Expense updatedExpense = mDbAdapter.expenseSave(expense);

                if(isNewExpense){
                    mExpensesList.add(updatedExpense);
                }

				for (Expense ex : mExpensesList) {

					if (ex.getId().equals(updatedExpense.getId())) {
						ex.synchronize(expense);
						break;
					}
				}

                ExpenseAdapter expenseAdapter = (ExpenseAdapter) this.getListView().getAdapter();
                expenseAdapter.notifyDataSetChanged();
			} catch (Exception exception) {
				DialogUtils.messageBox(this,
						getString(R.string.error_saving_item),
						getString(R.string.activity_expenses_list)).show();
			}

			break;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mDbAdapter = new DbAdapterImpl(this);

			LayoutInflater vi = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View emptyView = vi.inflate(R.layout.expense_row_no_item, null);
			emptyView.setVisibility(View.GONE);

			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			lv.setOnCreateContextMenuListener(this);

			((ViewGroup) lv.getParent()).addView(emptyView);
			this.getListView().setEmptyView(emptyView);

			lv.setOnItemClickListener(mExpenseListItemClickListener);

			Intent i = this.getIntent();
			if (null != i) {

				Bundle bundle = i.getExtras();

				if (null != bundle) {

					mCurrentTrip = (Trip) bundle.getSerializable("trip");

					mExpensesList = mDbAdapter
							.expenseFetchForTrip(mCurrentTrip.getId());

					ExpenseAdapter adapter = new ExpenseAdapter(this,
							mExpensesList);

					String allExpense = this.getResources().getString(
							R.string.all_expenses);

					this.setTitle(String.format("%s:%s(%d)",
							mCurrentTrip.getDestination(), allExpense,
							mExpensesList.size()));

					lv.setAdapter(adapter);

				}

			}
		} catch (Exception ex) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_expenses_list),
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

			Expense expenseToDelete = mExpensesList.get(info.position);

			if (null != expenseToDelete) {

				try {
					expenseToDelete.setDead(true);
					mDbAdapter.expenseSave(expenseToDelete);
					mExpensesList.remove(info.position);
					((BaseAdapter) getListView().getAdapter())
							.notifyDataSetChanged();
				} catch (Exception exception) {
					DialogUtils.messageBox(this,
							getString(R.string.error_deleting_item),
							getString(R.string.activity_expenses_list)).show();
				}
			}
			return true;
		}
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		int position = ((AdapterContextMenuInfo) menuInfo).position;

		Expense expense = mExpensesList.get(position);

		// Setup the menu header
		menu.setHeaderTitle(expense.toString());

		// Add a menu item to delete the note
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.expenses_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.option_menu_item_add_expense:
			showExpenseEditor();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
