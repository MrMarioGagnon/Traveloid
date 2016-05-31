/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.gagnon.mario.mr.traveloid.category;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.dialog.DialogUtils;
import com.gagnon.mario.mr.traveloid.core.dialog.SingleChoiceEventHandler;

public class CategoriesList extends ExpandableListActivity {

	private DbAdapter mDbAdapter;

	private SingleChoiceEventHandler mMessageBoxResponseHandler = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {
			CategoriesList.this.finish();
		}
	};

	private OnChildClickListener mChildCategoryClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View view,
				int groupPosition, int childPosition, long rowId) {
			String group = (String) parent.getExpandableListAdapter().getGroup(
					groupPosition);
			String child = (String) parent.getExpandableListAdapter().getChild(
					groupPosition, childPosition);

			String category = String.format("%s:%s", group, child);

			Bundle bundle = new Bundle();
			bundle.putString("category", category);

			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			finish();

			return false;
		}

	};

	private static final int CATEGORY_EDITOR = 0;

	private ExpandableListAdapter mAdapter;

	private SingleChoiceEventHandler mCategoryClicked = new SingleChoiceEventHandler() {

		@Override
		public void execute(int idSelected) {

			Category[] availableCategory = ((CategoriesListAdapter) mAdapter)
					.getCategories();
			Category selectedCategory = availableCategory[idSelected];

			showCategoryEditor(selectedCategory);

		}

	};

	private void showAvailableCategory() {

		CharSequence[] categoryNameList = new CharSequence[mAdapter
				.getGroupCount()];

		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			categoryNameList[i] = (CharSequence) mAdapter.getGroup(i);
		}

		AlertDialog ad = DialogUtils.availableCategoryDialog(this,
				categoryNameList, mCategoryClicked);

		ad.setOwnerActivity(this);
		ad.show();

	}

	private void showCategoryEditor(Category category) {

		Intent i = new Intent(this, CategoryEditor.class);

		if (null != category) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("category", category);
			i.putExtras(bundle);
		}

		startActivityForResult(i, CATEGORY_EDITOR);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		Bundle bundle = data.getExtras();
		Category category = (Category) bundle.getSerializable("category");

		// no need to continue if no category return from activity
		if (null == category)
			return;

		// no need to continue if category is not new or dead or update
		if (!category.isDirty())
			return;

		try {
			mDbAdapter.categorySave(category);

			((CategoriesListAdapter) this.getExpandableListAdapter()).refresh();
			((BaseExpandableListAdapter) this.getExpandableListAdapter())
					.notifyDataSetInvalidated();

		} catch (Exception exception) {
			DialogUtils.messageBox(this, getString(R.string.error_saving_item),
					getString(R.string.activity_categories_list)).show();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mDbAdapter = new DbAdapterImpl(this);

			// Set up our adapter
			mAdapter = new CategoriesListAdapter(this);
			setListAdapter(mAdapter);
			registerForContextMenu(getExpandableListView());

			ExpandableListView lv = getExpandableListView();
			lv.setOnChildClickListener(mChildCategoryClickListener);

			this.setSelectedChild(2, 1, true);
		} catch (Exception ex) {
			AlertDialog ad = DialogUtils.singleButtonMessageBox(this,
					getString(R.string.error_starting_activity),
					getString(R.string.activity_categories_list),
					mMessageBoxResponseHandler);
			ad.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.categories_list_menu, menu);
		return true;
	}

	@Override
	public void onGroupExpand(int groupPosition) {

		ExpandableListView elv = this.getExpandableListView();
		ExpandableListAdapter ela = this.getExpandableListAdapter();

		for (int i = 0; i < ela.getGroupCount(); i++) {
			if (i != groupPosition)
				elv.collapseGroup(i);
		}

		super.onGroupExpand(groupPosition);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_menu_item_add:
			showCategoryEditor(null);
			return true;
		case R.id.option_menu_item_update:
			showAvailableCategory();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
