package com.gagnon.mario.mr.traveloid.category;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.ActivityValidationStatus;
import com.gagnon.mario.mr.traveloid.core.LetterDigitFilter;
import com.gagnon.mario.mr.traveloid.core.Tools;

public class CategoryEditor extends Activity {

	private LinearLayout mLinearLayoutSubCategory;
	private int mLayoutPosition = 1;
	private Category mCategory;
	private EditText mEditTextCategory;
	private TextView mTextViewMessage;

	private OnClickListener mRemoveSubCategoryClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mLinearLayoutSubCategory.removeView((ViewGroup) v.getParent());
			mLayoutPosition--;
		}
	};

	private OnClickListener mAddSubCategoryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			View view = createSubCategoryView(null);

			mLinearLayoutSubCategory.addView(view, mLayoutPosition);
			mLayoutPosition++;

		}
	};

	private OnClickListener mButtonSaveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			ActivityValidationStatus status = ValidateActivity();

			if (status.isValid()) {

				mTextViewMessage.setVisibility(View.GONE);

				Bundle bundle;
				Intent intent;

				mCategory.setName(mEditTextCategory.getText().toString());

				ViewGroup mainView = mLinearLayoutSubCategory;

				EditText editTextSubCategory;
				SortedSet<String> subCategorySet = new TreeSet<>();
				for (int i = 0; i < mainView.getChildCount(); i++) {
					View view = mainView.getChildAt(i);
					if (view.getId() != R.id.relativeLayoutHeader
							&& view.getId() != R.id.linearLayoutFooter) {
						editTextSubCategory = (EditText) view
								.findViewById(R.id.editTextName);
						if (editTextSubCategory.toString().trim().length() != 0)
							subCategorySet.add(editTextSubCategory.getText()
									.toString());
					}
				}

				mCategory.setSubCategories(subCategorySet
						.toArray(new String[subCategorySet.size()]));

				bundle = new Bundle();
				bundle.putSerializable("category", mCategory);

				intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);

				finish();

			} else {
				mTextViewMessage.setText(status.getMessage());
				mTextViewMessage.setVisibility(View.VISIBLE);
			}

		}
	};

	private OnClickListener mButtonCancelClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}
	};
	private OnClickListener mButtonDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Bundle bundle;
			Intent intent;
			mCategory.setDead(true);
			bundle = new Bundle();
			bundle.putSerializable("category", mCategory);

			intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);

			finish();

		}
	};

	private void AddSubCategories(String[] subCategories) {

		if (null == subCategories)
			return;

		View view;
		for (String subCategory : subCategories) {

			view = createSubCategoryView(subCategory);

			mLinearLayoutSubCategory.addView(view, mLayoutPosition);
			mLayoutPosition++;

		}

	}

	private View createSubCategoryView(String name) {

		LayoutInflater inflater = LayoutInflater.from(this);
		EditText editText;
		View view = inflater.inflate(R.layout.sub_category_row, null);

        editText = (EditText) view.findViewById(R.id.editTextName);
		editText.setText(name);
		editText.setFilters(new InputFilter[] { new LetterDigitFilter() });

		ImageButton imageButtonEdit = (ImageButton) view
				.findViewById(R.id.imageButtonEdit);
		imageButtonEdit.setOnClickListener(mRemoveSubCategoryClickListener);

		return view;

	}

	private ActivityValidationStatus ValidateActivity() {

		List<String> messages = new ArrayList<>();

		if (mEditTextCategory.getText().toString().trim().length() == 0) {
			messages.add(this.getResources().getString(
					R.string.message_category_is_mandatory));
		}

		ViewGroup mainView = mLinearLayoutSubCategory;

		boolean hasSubCategory = false;
		EditText editTextSubCategory;
		String subCategoryName;
		for (int i = 0; i < mainView.getChildCount(); i++) {
			View view = mainView.getChildAt(i);
			if (view.getId() != R.id.relativeLayoutHeader
					&& view.getId() != R.id.linearLayoutFooter) {
				editTextSubCategory = (EditText) view
						.findViewById(R.id.editTextName);

				hasSubCategory = true;
				subCategoryName = editTextSubCategory.getText().toString();

				if (subCategoryName.trim().length() == 0) {
					messages.add(this.getResources().getString(
							R.string.message_sub_category_is_mandatory));
					break;
				}
			}
		}

		if (!hasSubCategory) {
			messages.add(this.getResources().getString(
					R.string.message_no_sub_category_available));
		}

		return ActivityValidationStatus.create(Tools.join(messages, "\n"));

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.category_editor);

		mLinearLayoutSubCategory = (LinearLayout) findViewById(R.id.linearLayoutMain);

		ImageButton imageButtonSubCategoryAdd = (ImageButton) findViewById(R.id.imageButtonSubCategoryAdd);
		imageButtonSubCategoryAdd
				.setOnClickListener(mAddSubCategoryClickListener);

		Button buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(mButtonSaveClickListener);
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(mButtonCancelClickListener);
		Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
		buttonDelete.setOnClickListener(mButtonDeleteClickListener);
		mEditTextCategory = (EditText) findViewById(R.id.editTextCategory);
		mTextViewMessage = (TextView) findViewById(R.id.textViewMessage);

		Bundle bundle = getIntent().getExtras();

		if (null != bundle) {
			mCategory = (Category) bundle.getSerializable("category");
			if (null == mCategory) {
				mCategory = Category.createNew();
				imageButtonSubCategoryAdd.performClick();
			} else {
				mEditTextCategory.setText(mCategory.getName());

				if (null == mCategory.getSubCategories()) {
					imageButtonSubCategoryAdd.performClick();
				} else {
					AddSubCategories(mCategory.getSubCategories());
				}
			}
		} else {
			buttonDelete.setVisibility(View.GONE);
			mCategory = Category.createNew();
			imageButtonSubCategoryAdd.performClick();
		}

	}

}
