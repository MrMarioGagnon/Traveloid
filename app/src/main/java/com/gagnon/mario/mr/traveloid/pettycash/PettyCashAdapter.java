package com.gagnon.mario.mr.traveloid.pettycash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gagnon.mario.mr.traveloid.R;
import java.util.List;

public class PettyCashAdapter extends BaseAdapter {

	static class ViewHolder {
		public TextView textViewName;
		public TextView textViewAmount;
	}

	private List<PettyCash> mPettyCashList;

	private LayoutInflater mInflater;

	public PettyCashAdapter(Context context, List<PettyCash> pettyCashList) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);

		this.setPettyCashList(pettyCashList);
	}

	public int getCount() {
		return getPettyCashList().size();
	}

	public Object getItem(int position) {
		return getPettyCashList().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {

		PettyCash entry = getPettyCashList().get(position);

		// A ViewHolder keeps references to children views to avoid unnecessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.pettycash_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.textViewName = (TextView) convertView
					.findViewById(R.id.textViewName);
			holder.textViewAmount = (TextView) convertView
					.findViewById(R.id.textViewAmount);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.textViewName.setText(entry.getName());
		holder.textViewAmount.setText(String.format("%.2f %s", entry.getAmount(), entry.getCurrency()));

		return convertView;

	}

	public void setPettyCashList(List<PettyCash> pettyCashList) {
		mPettyCashList = pettyCashList;
	}

	public List<PettyCash> getPettyCashList() {
		return mPettyCashList;
	}

}