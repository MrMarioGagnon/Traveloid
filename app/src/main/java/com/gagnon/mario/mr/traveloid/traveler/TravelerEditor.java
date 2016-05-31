package com.gagnon.mario.mr.traveloid.traveler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gagnon.mario.mr.traveloid.R;

public class TravelerEditor extends Dialog {

	private class CancelListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			TravelerEditor.this.dismiss();
		}
	}

	private class OKListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			TravelerEditor.this.mTraveler.setName(String.valueOf(mEditTextName
					.getText()));
			mOnSaveListener.save(TravelerEditor.this.mTraveler);
			TravelerEditor.this.dismiss();
		}
	}

	public interface OnSaveListener {
		void save(Traveler traveler);
	}

	private Traveler mTraveler;
	private OnSaveListener mOnSaveListener;
	private EditText mEditTextName;
	private Button mButtonSave;

	public TravelerEditor(Context context, Traveler traveler,
			OnSaveListener onSaveListener) {
		super(context);
		this.mTraveler = traveler;
		this.mOnSaveListener = onSaveListener;

		if (null == mTraveler.getId()) {
			setTitle(context.getResources().getString(R.string.new_traveler));
		} else {
			setTitle(context.getResources().getString(R.string.edit_traveler));
		}
	}

	private void manageButtonSave(CharSequence s) {
		mButtonSave.setEnabled(s.length() > 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.traveler_editor);

		mButtonSave = (Button) findViewById(R.id.buttonSave);
		mButtonSave.setOnClickListener(new OKListener());

		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new CancelListener());

		mEditTextName = (EditText) findViewById(R.id.editTextName);
		if (null != mTraveler.getId())
			mEditTextName.setText(mTraveler.getName());

		mEditTextName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				manageButtonSave(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
	}

}
