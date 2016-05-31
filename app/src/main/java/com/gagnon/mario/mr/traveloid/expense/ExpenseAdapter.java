package com.gagnon.mario.mr.traveloid.expense;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;

public class ExpenseAdapter extends BaseAdapter {

	static class ViewHolder {
		public TextView textViewDate;
		public TextView textViewAmount;
        public TextView textViewCurrency;
		public TextView textViewCategory;
		public TextView textViewTraveler;
	}

	private List<Expense> mExpensesList;

	private LayoutInflater mInflater;

	public ExpenseAdapter(Context context, List<Expense> expensesList) {
		mInflater = LayoutInflater.from(context);
		this.mExpensesList = expensesList;
	}

	public int getCount() {
		return mExpensesList.size();
	}

	public Object getItem(int position) {
		return mExpensesList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {

		Expense entry = mExpensesList.get(position);

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.expense_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.textViewDate = (TextView) convertView
					.findViewById(R.id.textViewDate);
			holder.textViewAmount = (TextView) convertView
					.findViewById(R.id.textViewAmount);
            holder.textViewCurrency = (TextView) convertView
                    .findViewById(R.id.textViewCurrency);
			holder.textViewCategory = (TextView) convertView
					.findViewById(R.id.textViewCategory);
			holder.textViewTraveler = (TextView) convertView
					.findViewById(R.id.textViewTraveler);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.textViewDate.setText(entry.getDate());
		holder.textViewAmount.setText(entry.getAmount().toString());
        holder.textViewCurrency.setText(entry.getCurrency());
		holder.textViewCategory.setText(entry.getCategory());
		holder.textViewTraveler.setText(entry.getTravelerForDisplay());

		return convertView;
	}

}