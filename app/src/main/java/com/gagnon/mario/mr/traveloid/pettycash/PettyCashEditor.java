package com.gagnon.mario.mr.traveloid.pettycash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.ActivityValidationStatus;
import com.gagnon.mario.mr.traveloid.core.Tools;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class PettyCashEditor extends Activity {

    private PettyCash mPettyCash;
    private Trip mCurrentTrip;
    private TextView mTextViewMessage;

    private EditText mEditTextName;
    private EditText mEditTextDescription;
    private EditText mEditTextAmount;
    private EditText mEditTextExchangeRate;
    private Spinner mSpinnerCurrency;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pettycash_editor);

		TextView textViewTripName = (TextView) findViewById(R.id.textViewTripName);
        mTextViewMessage = (TextView) findViewById(R.id.textViewMessage);

        Bundle bundle = getIntent().getExtras();

        if (null != bundle) {
            mPettyCash = (PettyCash) bundle.getSerializable("pettycash");
            mCurrentTrip = (Trip) bundle.getSerializable("trip");
            if (null != mCurrentTrip)
                textViewTripName.setText(mCurrentTrip.getDestination());
        }


        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextDescription = (EditText) findViewById(R.id.editTextDescription);
        mEditTextAmount = (EditText) findViewById(R.id.editTextAmount);
        mEditTextExchangeRate = (EditText) findViewById(R.id.editTextExchangeRate);
        setupSpinnerCurrency();
        setupButtonBack();
        setupButtonSave();

        String activityTitle;
        if (!mPettyCash.isNew()) {
            activityTitle = this.getResources().getString(R.string.edit_pettycash);

            mEditTextName.setText(mPettyCash.getName());
            mEditTextDescription.setText(mPettyCash.getDescription());
            mEditTextAmount.setText(mPettyCash.getAmount().toString());
            mEditTextExchangeRate.setText(mPettyCash.getExchangeRate().toString());
            setSpinner(mSpinnerCurrency, mPettyCash.getCurrency());
        } else {
            activityTitle = this.getResources().getString(R.string.new_pettycash);

            mPettyCash = PettyCash.createNew();
            mPettyCash.setTripId(mCurrentTrip.getId());
            mPettyCash.setExchangeRate(1.0);
            setSpinner(mSpinnerCurrency, mCurrentTrip.getCurrency());

        }

        this.setTitle(activityTitle);

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

    private void setupSpinnerCurrency() {

        mSpinnerCurrency = (Spinner) findViewById(R.id.spinnerCurrency);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.currency_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(adapter);

    }

    private void setupButtonBack() {

        Button mButtonBack = (Button) findViewById(R.id.buttonBack);

        mButtonBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();

            }
        });

    }

    private void setupButtonSave() {
        Button mButtonSave = (Button) findViewById(R.id.buttonSave);

        mButtonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ActivityValidationStatus status = ValidateActivity();

                if (status.isValid()) {

                    mTextViewMessage.setVisibility(View.GONE);

                    mPettyCash.setName(mEditTextName.getText().toString());
                    mPettyCash.setDescription(mEditTextDescription
                            .getText().toString());

                    mPettyCash.setAmount(Double.valueOf(mEditTextAmount
                            .getText().toString()));

                    mPettyCash.setCurrency((String) mSpinnerCurrency
                            .getSelectedItem());

                    mPettyCash.setExchangeRate(Double.valueOf(mEditTextExchangeRate
                            .getText().toString()));

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("pettycash", mPettyCash);

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

    private ActivityValidationStatus ValidateActivity() {

        List<String> messages = new ArrayList<>();

        if (mEditTextName.getText().toString().trim().length() == 0) {
            messages.add(this.getResources().getString(
                    R.string.message_name_is_mandatory));
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



}
