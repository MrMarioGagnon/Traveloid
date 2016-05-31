package com.gagnon.mario.mr.traveloid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.Tools;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.expense.Expense;
import com.gagnon.mario.mr.traveloid.expense.ExpenseEditor;
import com.gagnon.mario.mr.traveloid.expense.ExpenseReport;
import com.gagnon.mario.mr.traveloid.expense.ExpenseSummary;
import com.gagnon.mario.mr.traveloid.expense.ExpenseSummaryAdapter;
import com.gagnon.mario.mr.traveloid.expense.ExpenseSummaryCalculator;
import com.gagnon.mario.mr.traveloid.expense.ExpensesList;
import com.gagnon.mario.mr.traveloid.pettycash.PettyCashList;
import com.gagnon.mario.mr.traveloid.traveler.TravelersList;
import com.gagnon.mario.mr.traveloid.trip.Trip;
import com.gagnon.mario.mr.traveloid.trip.TripsList;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mario on 8/6/2015.
 */
public class Main extends Activity{

    @Override
    public void finish() {

        try {
            Tools.copy(null, null);
        } catch (Exception ex) {
            DialogUtils.messageBox(this, ex.getMessage(),
                    getString(R.string.activity_main)).show();

        }

        super.finish();
    }

    private SingleChoiceEventHandler mMessageBoxResponseHandler = new SingleChoiceEventHandler() {

        @Override
        public void execute(int idSelected) {
            Main.this.finish();
        }
    };

    private SingleChoiceEventHandler mMessageBoxOkButtonClickListener = new SingleChoiceEventHandler() {

        @Override
        public void execute(int idSelected) {
            Main.this.finish();

        }

    };

    private View.OnClickListener mActivityButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonAddExpense:
                    showExpenseEditor();
                    break;
                case R.id.buttonViewExpenses:
                    showExpenseList();
                    break;
                case R.id.imageButtonTrip:
                    showTripList();
                    break;
            }
        }
    };

    private static final int TRIPS_LIST = 0;
    private static final int EXPENSE_EDITOR = 1;
    private static final int EXPENSES_LIST = 2;
    private static final int TRAVELERS_LIST = 3;
    private static final int PETTYCASH_LIST = 4;

    private Button mButtonAddExpense;
    private Button mButtonViewExpenses;
    private TextView mTextViewTripName;
    private Trip mCurrentTrip;
    private DbAdapter mDbAdapter;

    private void displayExpense() {

        ListView lv = (ListView) findViewById(R.id.listViewExpenseSummary);

        if (null == mCurrentTrip) {
            lv.setAdapter(null);
            return;
        }

        // get the current date
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePart currentDate = new DatePart(year, month, day);

        try {
            List<Expense> expenseList = mDbAdapter.expenseFetchForTrip(mCurrentTrip.getId());

            List<ExpenseSummary> expenseSummaryList = ExpenseSummaryCalculator
                    .calculate(currentDate, mCurrentTrip.getTravelerSet(),
                            expenseList);

            ExpenseSummaryAdapter esa = new ExpenseSummaryAdapter(this,
                    expenseSummaryList);
            lv.setAdapter(esa);
        } catch (Exception exception) {

            DialogUtils.messageBox(this,
                    getString(R.string.error_unable_to_fetch_all_expense),
                    getString(R.string.activity_main)).show();

        }

    }

    private void showExpenseEditor() {
        Intent i = new Intent(this, ExpenseEditor.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("trip", mCurrentTrip);
        i.putExtras(bundle);

        startActivityForResult(i, EXPENSE_EDITOR);
    }

    private void showExpenseList() {
        Intent i = new Intent(this, ExpensesList.class);
        Bundle bundel = new Bundle();
        bundel.putSerializable("trip", mCurrentTrip);

        i.putExtras(bundel);

        startActivityForResult(i, EXPENSES_LIST);
    }

    private void showTravelerList() {
        Intent i = new Intent(this, TravelersList.class);
        startActivityForResult(i, TRAVELERS_LIST);
    }

    private void showPettyCash() {

        Intent i = new Intent(this, PettyCashList.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", mCurrentTrip);
        i.putExtras(bundle);

        startActivityForResult(i, PETTYCASH_LIST);
    }

    private void createReport(){

        ExpenseReport expenseReport;
        try {
            expenseReport = new ExpenseReport(this);
            expenseReport.create(mCurrentTrip);
            expenseReport.close();
        } catch (Throwable e) {

            AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
                    getString(R.string.message_unable_to_create_report),
                    getString(R.string.activity_main),
                    mMessageBoxResponseHandler);
            ad.show();
        }
    }


    private void showTripList() {
        Intent i = new Intent(this, TripsList.class);
        startActivityForResult(i, TRIPS_LIST);
    }

    private void updateDisplay() {

        String tripName;

        if (null == mCurrentTrip) {
            mButtonAddExpense.setEnabled(false);
            mButtonViewExpenses.setEnabled(false);

            tripName = getResources().getString(R.string.message_create_a_trip);
        } else {
            mButtonAddExpense.setEnabled(true);
            mButtonViewExpenses.setEnabled(true);

            tripName = mCurrentTrip.getDestination();
        }

        mTextViewTripName.setText(tripName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        Bundle extras = null;
        if (null != intent)
            extras = intent.getExtras();

        switch (requestCode) {
            case TRIPS_LIST:

                if (resultCode == RESULT_OK) {

                    if (null != extras) {

                        Long selectedId = (Long) extras
                                .getSerializable("selectedId");

                        if (!mCurrentTrip.getId().equals(selectedId)) {
                            // Change Current Trip
                            try {
                                mCurrentTrip = mDbAdapter.tripFetch(selectedId);
                            } catch (RepositoryException e) {
                                DialogUtils
                                        .messageBox(
                                                this,
                                                getString(R.string.error_unable_to_fetch_trip),
                                                getString(R.string.activity_main));
                            }

                        }
                    } else {

                        try {
                            mCurrentTrip = mDbAdapter.tripFetch(mCurrentTrip
                                    .getId());
                            if (null == mCurrentTrip)
                                mCurrentTrip = mDbAdapter.tripFetchDefault();
                        } catch (RepositoryException e) {
                            DialogUtils.messageBox(this,
                                    getString(R.string.error_unable_to_fetch_trip),
                                    getString(R.string.activity_main));

                        }

                    }
                }

                break;

            case EXPENSE_EDITOR:

                if (resultCode != RESULT_OK)
                    return;

                if (null != extras) {

                    Expense expense = (Expense) extras.getSerializable("expense");
                    if (null != expense) {

                        try {
                            mDbAdapter.expenseSave(expense);
                        } catch (Exception exception) {
                            DialogUtils
                                    .messageBox(
                                            this,
                                            getString(R.string.error_unable_to_save_object_to_database),
                                            getString(R.string.activity_main))
                                    .show();
                        }
                    }
                }

                break;

            case EXPENSES_LIST:

                break;
            case PETTYCASH_LIST:
                break;
        }

        displayExpense();
        updateDisplay();

    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.main);

            mButtonAddExpense = (Button) findViewById(R.id.buttonAddExpense);
            mButtonAddExpense.setOnClickListener(mActivityButtonClickListener);

            findViewById(R.id.imageButtonTrip).setOnClickListener(
                    mActivityButtonClickListener);

            mButtonViewExpenses = (Button) findViewById(R.id.buttonViewExpenses);
            mButtonViewExpenses
                    .setOnClickListener(mActivityButtonClickListener);

            mTextViewTripName = (TextView) findViewById(R.id.textViewTripName);

            mDbAdapter = new DbAdapterImpl(this);
            mCurrentTrip = mDbAdapter.tripFetchDefault();

            displayExpense();
            updateDisplay();


        } catch (Exception exception) {

            AlertDialog ab = DialogUtils.singleButtonMessageBox(this,
                    getString(R.string.error_starting_activity),
                    getString(R.string.activity_main),
                    mMessageBoxOkButtonClickListener);
            ab.show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_travelers_list:
                showTravelerList();
                return true;
            case R.id.menu_item_expense_report:
                createReport();
                return true;
            case R.id.menu_item_petty_cash:
                showPettyCash();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
