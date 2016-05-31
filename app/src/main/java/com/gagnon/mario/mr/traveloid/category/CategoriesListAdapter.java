package com.gagnon.mario.mr.traveloid.category;

import java.util.List;

import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CategoriesListAdapter extends BaseExpandableListAdapter {

	private Category[] mCategories = null;
	private DbAdapter mDbAdapter;

	public CategoriesListAdapter(Context context) throws RepositoryException {

		mDbAdapter = new DbAdapterImpl(context);

		refresh();
	}

	public Category[] getCategories() {
		return mCategories;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return mCategories[groupPosition].getSubCategory(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return mCategories[groupPosition].getSubCategories().length;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		TextView textView = getGenericView(parent.getContext());
		textView.setText(getChild(groupPosition, childPosition).toString());
		return textView;
	}

	public TextView getGenericView(Context context) {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 64);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		textView.setPadding(60, 0, 0, 0);
		return textView;
	}

	public Object getGroup(int groupPosition) {
		return mCategories[groupPosition].getName();
	}

	public int getGroupCount() {
		return mCategories.length;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getGenericView(parent.getContext());
		textView.setText(getGroup(groupPosition).toString());
		return textView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void refresh() throws RepositoryException {

		List<Category> categoryList = mDbAdapter.categoryFetchAll();

		mCategories = categoryList.toArray(new Category[categoryList.size()]);

	}

}
