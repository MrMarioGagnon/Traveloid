package com.gagnon.mario.mr.traveloid.expense;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.category.CategoriesList;
import com.gagnon.mario.mr.traveloid.core.Account;
import com.gagnon.mario.mr.traveloid.core.ActivityValidationStatus;
import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.Tools;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.MultipleChoiceEventHandler;
import com.gagnon.mario.mr.traveloid.paymentmethod.PaymentMethodExtractor;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseEditor extends Activity {

	private static Boolean initialCall = true;

	private static final int CATEGORY_LIST_SELECTION = 0;

	private TextView mTextViewTraveler;
	private TextView mTextViewPayor;	
	private TextView mTextViewDate;
	private TextView mTextViewCategory;
    private EditText mEditTextAmount;
	private EditText mEditTextExchangeRate;
	private Spinner mSpinnerPaymentMethod;
	private Spinner mSpinnerCurrency;
	private EditText mEditTextReference;
	private EditText mEditTextDescription;
    private Expense mCurrentExpense = null;
	private Trip mCurrentTrip = null;
	private TextView mTextViewMessage;

	static final int DATE_DIALOG_ID = 1;

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
			if(initialCall) {
				initialCall = false;
				return;
			}

            Account account = (Account)parent.getItemAtPosition(pos);
            mEditTextExchangeRate.setText( account.getIsPettyCash() ? account.getExchangeRate().toString(): mCurrentExpense.getExchangeRate().toString());
            setSpinner(mSpinnerCurrency, account.getCurrency());
            mEditTextExchangeRate.setEnabled(!account.getIsPettyCash());
            mSpinnerCurrency.setEnabled( !account.getIsPettyCash() );

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			updateTextViewDate(year, monthOfYear, dayOfMonth);
		}
	};

	private MultipleChoiceEventHandler mTravelerMessageBoxHandler = new MultipleChoiceEventHandler() {

		@Override
		public void execute(boolean[] idSelected) {

			String[] travelerArray = mCurrentTrip.getTravelerSet().toArray(
					new String[mCurrentTrip.getTravelerSet().size()]);

			StringBuilder sb = new StringBuilder();
			boolean checked;
			for (int i = 0; i < idSelected.length; i++) {

				checked = idSelected[i];

				if (checked) {
					if (sb.length() == 0) {
						sb.append(travelerArray[i]);
					} else {
						sb.append(",").append(travelerArray[i]);
					}
				}

			}

			mTextViewTraveler.setText(sb.toString());

		}

	};
	
	private MultipleChoiceEventHandler mPayorMessageBoxHandler = new MultipleChoiceEventHandler() {

		@Override
		public void execute(boolean[] idSelected) {

			String[] travelerArray = mCurrentTrip.getTravelerSet().toArray(
					new String[mCurrentTrip.getTravelerSet().size()]);

			StringBuilder sb = new StringBuilder();
			boolean checked;
			for (int i = 0; i < idSelected.length; i++) {

				checked = idSelected[i];

				if (checked) {
					if (sb.length() == 0) {
						sb.append(travelerArray[i]);
					} else {
						sb.append(",").append(travelerArray[i]);
					}
				}

			}

			mTextViewPayor.setText(sb.toString());

		}

	};

	private boolean[] buildTravelersCheckedArray(String[] travelersArray,
			String travelersName) {

		boolean[] checked = new boolean[travelersArray.length];

		int i = 0;
		for (String traveler : travelersArray) {

			checked[i] = travelersName.contains(traveler);
			i++;
		}

		return checked;

	}

	private Account getDefaultPaymentMethod(Spinner spinner){
        Account defaultAccount = null;
		Adapter adapter = spinner.getAdapter();
		if(adapter.getCount() > 0){
			defaultAccount =  (Account)adapter.getItem(0);
		}
		return defaultAccount;
	}

    private void setSpinnerPaymentMethod(Spinner spinner, String value){

        Adapter adapter = spinner.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {

            Account item = (Account) adapter.getItem(i);
            if (null != item) {
                if (item.getAccountName().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }

        }

    }

	private void setSpinner(Spinner spinner, String value) {

		Adapter adapter = spinner.getAdapter();

		for (int i = 0; i < adapter.getCount(); i++) {

			String item = (String) adapter.getItem(i);
			if (null != item) {
				if (item.equals(value)) {
					spinner.setSelection(i);
					break;
				}
			}

		}

	}

	private void setupButtonBack() {

        Button mButtonBack = (Button) findViewById(R.id.buttonBack);

		mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();

            }
        });

	}

	private void setupButtonSave() {
        Button mButtonSave = (Button) findViewById(R.id.buttonSave);

		mButtonSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ActivityValidationStatus status = ValidateActivity();

                if (status.isValid()) {

                    mTextViewMessage.setVisibility(View.GONE);

                    mCurrentExpense.setDate(mTextViewDate.getText().toString());
                    mCurrentExpense.setCategory(mTextViewCategory.getText()
                            .toString());
                    mCurrentExpense.setAmount(Double.valueOf(mEditTextAmount
                            .getText().toString()));

                    String paymentMethod = ((Account) mSpinnerPaymentMethod.getSelectedItem()).getAccountName();

                    mCurrentExpense
                            .setPaymentMethod(paymentMethod);
                    mCurrentExpense.setReference(mEditTextReference.getText()
                            .toString());
                    mCurrentExpense.setDescription(mEditTextDescription
                            .getText().toString());

                    mCurrentExpense.setTravelerSet(mTextViewTraveler.getText()
                            .toString());

                    mCurrentExpense.setPayorSet(mTextViewPayor.getText().toString());

                    mCurrentExpense.setCurrency((String) mSpinnerCurrency
                            .getSelectedItem());

                    mCurrentExpense.setExchangeRate(Double.valueOf(mEditTextExchangeRate
                            .getText().toString()));

					Log.d("ExpenseEditor", mEditTextExchangeRate.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("expense", mCurrentExpense);

                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                    finish();

                } else {
                    mTextViewMessage.setText(status.getMessage());
                    mTextViewMessage.setVisibility(View.VISIBLE);
                }

            }
        });

	}

	private void setupImageButtonCategory() {
        ImageButton mImageButtonCategory = (ImageButton) findViewById(R.id.imageButtonCategory);

		// add a click listener to CategoryButton
		mImageButtonCategory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showCategoryList();
            }
        });

	}

	private void setupImageButtonDate() {
        ImageButton mImageButtonDate = (ImageButton) findViewById(R.id.imageButtonDate);

		// add a click listener to the button
		mImageButtonDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

	}

	private void setupImageButtonTraveler() {
        ImageButton mImageButtonTraveler = (ImageButton) findViewById(R.id.imageButtonTraveler);

		// add a click listener to the button
		mImageButtonTraveler.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showTravelerDialog(mTravelerMessageBoxHandler, mTextViewTraveler);
            }
        });

	}
	
	private void setupImageButtonPayor() {
        ImageButton mImageButtonPayor = (ImageButton) findViewById(R.id.imageButtonPayor);

		// add a click listener to the button
		mImageButtonPayor.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showTravelerDialog(mPayorMessageBoxHandler, mTextViewPayor);
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

	private void setupSpinnerPaymentMethod() {

		mSpinnerPaymentMethod = (Spinner) findViewById(R.id.spinnerPaymentMethod);

		List<Account> paymentMethod = null;
		try {
			paymentMethod = PaymentMethodExtractor.extract(mCurrentTrip, this.getBaseContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		assert paymentMethod != null;
		ArrayAdapter<Account> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethod);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mSpinnerPaymentMethod.setAdapter(adapter);

        mSpinnerPaymentMethod.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	private void setupTextViewCategory() {
		mTextViewCategory = (TextView) findViewById(R.id.textViewCategory);
	}

	private void setupTextViewDate() {
		mTextViewDate = (TextView) findViewById(R.id.textViewDate);
	}

	private void setupTextViewTraveler() {
		mTextViewTraveler = (TextView) findViewById(R.id.textViewTraveler);
	}
	
	private void setupTextViewPayor() {
		mTextViewPayor = (TextView) findViewById(R.id.textViewPayor);
	}

	private void showCategoryList() {
		Intent i = new Intent(this, CategoriesList.class);
		startActivityForResult(i, CATEGORY_LIST_SELECTION);
	}

	private void showTravelerDialog(MultipleChoiceEventHandler dialogBoxReponseHandler, TextView currentText) {

		String[] travelerArray = mCurrentTrip.getTravelerSet().toArray(
				new String[mCurrentTrip.getTravelerSet().size()]);

		Dialog dialog = DialogUtils.travelerSetterDialog(
				this,
				travelerArray,
				dialogBoxReponseHandler,
				buildTravelersCheckedArray(travelerArray,
						currentText.getText().toString()));

		dialog.setOwnerActivity(this);
		dialog.show();

	}

	private void updateTextViewDate(int year, int month, int day) {
		mTextViewDate.setText(String.format("%d-%02d-%02d", year, month + 1,
				day));
	}

	private ActivityValidationStatus ValidateActivity() {

		List<String> messages = new ArrayList<>();

		if (mTextViewTraveler.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_traveler_is_mandatory));
		}

		if (mTextViewPayor.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_payor_is_mandatory));
		}


		if (mTextViewDate.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_date_is_mandatory));
		}

		if (mTextViewCategory.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_category_is_mandatory));
		}

		if (mEditTextAmount.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_amount_is_mandatory));
		}

		if (mEditTextExchangeRate.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_exchange_rate_is_mandatory));
		}


		return ActivityValidationStatus.create(Tools.join(messages, "\n"));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode != RESULT_OK)
			return;

		Bundle extras = intent.getExtras();
		switch (requestCode) {
		case CATEGORY_LIST_SELECTION:
			String category = extras.getString("category");
			mTextViewCategory.setText(category);
			break;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.expense_editor);

		TextView textViewTripName = (TextView) findViewById(R.id.textViewTripName);
		setupTextViewTraveler();
		setupTextViewPayor();
		setupTextViewDate();
		setupTextViewCategory();
		setupImageButtonDate();
		setupImageButtonTraveler();
		setupImageButtonPayor();
		setupImageButtonCategory();
		mEditTextAmount = (EditText) findViewById(R.id.editTextAmount);
		mEditTextExchangeRate = (EditText) findViewById(R.id.editTextExchangeRate);		
		mEditTextReference = (EditText) findViewById(R.id.editTextRefCheckNo);
		mEditTextDescription = (EditText) findViewById(R.id.editTextDescription);
		mTextViewMessage = (TextView) findViewById(R.id.textViewMessage);

		setupButtonBack();
		setupButtonSave();

		setupSpinnerCurrency();

		String activityTitle;
		int year;
		int month;
		int day;

		Bundle bundle = getIntent().getExtras();

		if (null != bundle) {
			mCurrentExpense = (Expense) bundle.getSerializable("expense");
			mCurrentTrip = (Trip) bundle.getSerializable("trip");
			if (null != mCurrentTrip)
				textViewTripName.setText(mCurrentTrip.getDestination());
		}

		setupSpinnerPaymentMethod();

		if (null != mCurrentExpense) {
			activityTitle = this.getResources().getString(R.string.new_expense);

			DatePart datePart;
			try {
				datePart = mCurrentExpense.getDatePart();
			} catch (ParseException e) {
				datePart = new DatePart(2000, 0, 1);
			}
			year = datePart.getYear();
			month = datePart.getMonth();
			day = datePart.getDay();

			mTextViewCategory.setText(mCurrentExpense.getCategory());
			mEditTextAmount.setText(mCurrentExpense.getAmount().toString());

            setSpinnerPaymentMethod(mSpinnerPaymentMethod,
                    mCurrentExpense.getPaymentMethod());

			setSpinner(mSpinnerCurrency, mCurrentExpense.getCurrency());

			mEditTextReference.setText(mCurrentExpense.getReference());
			mEditTextDescription.setText(mCurrentExpense.getDescription());

		} else {
			activityTitle = this.getResources().getString(R.string.new_expense);

			// get the current date
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			mCurrentExpense = Expense.createNew();
			mCurrentExpense.setTripId(mCurrentTrip.getId());
			mCurrentExpense.setTravelerSet(mCurrentTrip.getTravelerSet());
			mCurrentExpense.setPayorSet(mCurrentTrip.getTravelerSet());

			Account defaultPaimentMethod = getDefaultPaymentMethod(mSpinnerPaymentMethod);
			if(null == defaultPaimentMethod) {
				mCurrentExpense.setExchangeRate(1.0);
				setSpinner(mSpinnerCurrency, mCurrentTrip.getCurrency());
			}
			else{
				mCurrentExpense.setExchangeRate(defaultPaimentMethod.getExchangeRate());
				setSpinner(mSpinnerCurrency, defaultPaimentMethod.getCurrency());
			}

		}

		this.mTextViewTraveler.setText(mCurrentExpense.getTravelerForDisplay());
		this.mTextViewPayor.setText(mCurrentExpense.getPayorForDisplay());
		this.mEditTextExchangeRate.setText(mCurrentExpense.getExchangeRate().toString());
        this.setTitle(activityTitle);
		
		mEditTextAmount.requestFocus();

		Log.d("ExpenseEditor", this.mEditTextExchangeRate.getText().toString());
		updateTextViewDate(year, month, day);
		Log.d("ExpenseEditor2", this.mEditTextExchangeRate.getText().toString());
	}

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ZZZZZZZZZZZZ", this.mEditTextExchangeRate.getText().toString());
    }

    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// get the current date
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(this, mDateSetListener, year, month,
					day);

		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.expense_editor_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.option_menu_item_category:
			showCategoryList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
