package com.gagnon.mario.mr.traveloid.pettycash;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class PettyCashList extends ListActivity {

    private SingleChoiceEventHandler mMessageBoxOkButtonClickHandler;

	private DbAdapter mDbAdapter = null;
	private List<PettyCash> mPettyCashList;
    private Trip mCurrentTrip = null;
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
    private PettyCash mPettyCashSelected;
    private static final int PETTYCASH_EDITOR = 1;

    private void showPettyCashEditor(PettyCash pettyCash) {
        Intent i = new Intent(this, PettyCashEditor.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("pettycash", pettyCash);
        bundle.putSerializable("trip", mCurrentTrip);
        i.putExtras(bundle);

        startActivityForResult(i, PETTYCASH_EDITOR);
    }

    private void setupListView() throws RepositoryException {

        LayoutInflater vi = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = vi.inflate(R.layout.pettycash_row_no_item, null);
        emptyView.setVisibility(View.GONE);

        ((ViewGroup) this.getListView().getParent()).addView(emptyView);
        this.getListView().setEmptyView(emptyView);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mCurrentTrip = (Trip) bundle.getSerializable("trip");
            if (null != mCurrentTrip) {
                // TODO What to do if current trip is null
            }
        }

        mPettyCashList = mDbAdapter.pettyCashFetchForTrip(mCurrentTrip.getId());

        setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mPettyCashList));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int i = 0;
                for (PettyCash pettyCash : mPettyCashList) {
                    if (i == position) {
                        mPettyCashSelected = pettyCash;
                        break;
                    }
                    i++;
                }

                showPettyCashEditor(mPettyCashSelected);

            }
        });

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

                // Setup the menu header
                int i = 0;
                for (PettyCash pettyCash : mPettyCashList) {
                    if (i == position) {
                        menu.setHeaderTitle(pettyCash.toString());
                        break;
                    }
                    i++;
                }

                // Add a menu item to delete the note
                menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);

            }
        });

    }

    public PettyCashList() {

        mMessageBoxOkButtonClickHandler = new SingleChoiceEventHandler() {

            @Override
            public void execute(int idSelected) {
                PettyCashList.this.finish();

            }

        };
		mPettyCashList = new ArrayList<>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pettycash_list_menu, menu);
		return true;
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mDbAdapter = new DbAdapterImpl(this);

            setupListView();

			setTitle(String.format("%s-%s", getTitle(), this.getResources()
					.getString(R.string.title_pettycashs)));

		} catch (Exception e) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_pettycash_list),
					mMessageBoxOkButtonClickHandler);
			ad.show();
		}

	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            // Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {

                PettyCash toDelete = null;
                int i = 0;
                for (PettyCash pettyCash : mPettyCashList) {
                    if (i == info.position) {
                        toDelete = pettyCash;
                        break;
                    }
                    i++;
                }

                if (null != toDelete) {
                    toDelete.setDead(true);
                    try {
                        mDbAdapter.pettyCashSave(toDelete);
                        mPettyCashList.remove(toDelete);

                        BaseAdapter baseAdapter = (BaseAdapter) getListAdapter();
                        baseAdapter.notifyDataSetChanged();

                    } catch (Exception exception) {
                        DialogUtils.messageBox(this,
                                getString(R.string.error_deleting_item),
                                getString(R.string.activity_pettycash_list)).show();
                    }

                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (resultCode != RESULT_OK)
            return;

        Bundle bundle = intent.getExtras();

        switch (requestCode) {
            case PETTYCASH_EDITOR:
                PettyCash pettyCash = (PettyCash) bundle.getSerializable("pettycash");

                Boolean isNewPettyCash = pettyCash.isNew();

                try {
                    PettyCash updatedPettyCash = mDbAdapter.pettyCashSave(pettyCash);

                    if(isNewPettyCash){
                        mPettyCashList.add(updatedPettyCash);
                    }
                    for (PettyCash pc : mPettyCashList) {

                        if (pc.getId().equals(updatedPettyCash.getId())) {
                            pc.synchronize(updatedPettyCash);
                            break;
                        }
                    }

                    BaseAdapter baseAdapter = (BaseAdapter) getListAdapter();
                    baseAdapter.notifyDataSetChanged();

                } catch (Exception exception) {
                    DialogUtils.messageBox(this,
                            getString(R.string.error_saving_item),
                            getString(R.string.activity_pettycash_list)).show();
                }
                break;

            default:

                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.pettycash_list_menu:
                showPettyCashEditor(PettyCash.createNew());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
