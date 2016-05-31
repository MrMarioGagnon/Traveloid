package com.gagnon.mario.mr.traveloid.trip;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gagnon.mario.mr.traveloid.R;

public class TripAdapter extends BaseAdapter {

	static class ViewHolder {
		public TextView textViewDestination;
		public TextView textViewDate;
		public String defaultTitle;
	}

	private List<Trip> mTripsList;

	private LayoutInflater mInflater;

	public TripAdapter(Context context, List<Trip> listTrip) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);

		this.setTripsList(listTrip);
	}

	public int getCount() {
		return getTripsList().size();
	}

	public Object getItem(int position) {
		return getTripsList().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {

		Trip entry = getTripsList().get(position);

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
			convertView = mInflater.inflate(R.layout.trip_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.textViewDestination = (TextView) convertView
					.findViewById(R.id.textViewDestination);
			holder.textViewDate = (TextView) convertView
					.findViewById(R.id.textViewDate);

			holder.defaultTitle = mInflater.getContext().getResources()
					.getString(R.string.default_);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.textViewDestination.setText(String.format("%s%s",
				entry.getDestination(), entry.getDefault() ? "-"
						+ holder.defaultTitle : ""));
		holder.textViewDate.setText(entry.getDate());

		return convertView;

	}

	public void setTripsList(List<Trip> tripsList) {
		mTripsList = tripsList;
	}

	public List<Trip> getTripsList() {
		return mTripsList;
	}

}