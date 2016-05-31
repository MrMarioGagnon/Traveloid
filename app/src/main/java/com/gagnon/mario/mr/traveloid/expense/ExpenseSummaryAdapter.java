package com.gagnon.mario.mr.traveloid.expense;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;

public class ExpenseSummaryAdapter extends BaseAdapter {

	static class ViewHolder {
		public TextView textViewTraveler;
		public TextView textViewTodayTotal;
		public TextView textViewTotal;		
	}
	private List<ExpenseSummary> mExpenseSummaryList;

	private LayoutInflater mInflater;

	public ExpenseSummaryAdapter(Context context, List<ExpenseSummary> listExpenseSummary) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);

		this.mExpenseSummaryList = listExpenseSummary;
	}

	public int getCount() {
		return mExpenseSummaryList.size();
	}

	public Object getItem(int position) {
		return mExpenseSummaryList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {

		ExpenseSummary entry = mExpenseSummaryList.get(position);

		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.expense_summary_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.textViewTraveler = (TextView) convertView
					.findViewById(R.id.textViewTraveler);
			holder.textViewTodayTotal = (TextView) convertView
					.findViewById(R.id.textViewTodayExpense);
			holder.textViewTotal = (TextView) convertView
					.findViewById(R.id.textViewTotal);

			convertView.setTag(holder);
			
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.textViewTraveler.setText(entry.getTraveler());
		holder.textViewTodayTotal.setText( String.format("%s",  entry.getTodayExpense()));
		holder.textViewTotal.setText( String.format("%s", entry.getTotalExpense()));
		
		return convertView;

	}
	
	
}
