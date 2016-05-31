package com.gagnon.mario.mr.traveloid.trip;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;


import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.ObjectBase;
import com.gagnon.mario.mr.traveloid.core.Tools;

public class Trip extends ObjectBase implements Serializable {

	private static final long serialVersionUID = -3190241412282916415L;

	public static Trip create(Long id, String destination, String date,
			Boolean default_, SortedSet<String> travelerSet, String currency) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		Trip trip = new Trip();
		trip.mNew = false;
		trip.mDirty = false;
		trip.mId = id;
		trip.mDestination = destination;
		trip.mDate = date;
		trip.mDefault = default_;
		trip.mTravelerSet = travelerSet;
		trip.mCurrency = currency;

		return trip;
	}

	public static Trip create(Long id, String destination, String date,
			Boolean default_, String travelers, String currency) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		String[] travelerArray = travelers.split("\\|");
		SortedSet<String> travelerSet = new TreeSet<>();
        Collections.addAll(travelerSet, travelerArray);

		return create(id, destination, date, default_, travelerSet, currency);

	}

	public static Trip createNew() {

		Trip trip = new Trip();
		trip.mNew = true;
		trip.mDirty = true;
		trip.mDestination = "";
		trip.mDate = "";
		trip.mDefault = false;
		trip.mTravelerSet = new TreeSet<>();
		trip.mCurrency = "";

		return trip;
	}

	private Long mId;

	private String mDestination;

	private String mDate;

	private Boolean mDefault;
	private SortedSet<String> mTravelerSet;
	private String mCurrency;
	private Trip() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trip other = (Trip) obj;
		if (mCurrency == null) {
			if (other.mCurrency != null)
				return false;
		} else if (!mCurrency.equals(other.mCurrency))
			return false;
		if (mDate == null) {
			if (other.mDate != null)
				return false;
		} else if (!mDate.equals(other.mDate))
			return false;
		if (mDefault == null) {
			if (other.mDefault != null)
				return false;
		} else if (!mDefault.equals(other.mDefault))
			return false;
		if (mDestination == null) {
			if (other.mDestination != null)
				return false;
		} else if (!mDestination.equals(other.mDestination))
			return false;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (mTravelerSet == null) {
			if (other.mTravelerSet != null)
				return false;
		} else if (!mTravelerSet.equals(other.mTravelerSet))
			return false;
		return true;
	}

	public String getCurrency() {
		return mCurrency;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return mDate;
	}

	public DatePart getDatePart() throws ParseException {
		return Tools.DatePartExtractor(getDate());
	}

	/**
	 * @return the default
	 */
	public Boolean getDefault() {
		return mDefault;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return mDestination;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return mId;
	}

	public String getTravelerAsString() {
		return Tools.join(getTravelerSet(), "|");
	}

	public String getTravelerForDisplay() {

		if (null == getTravelerSet())
			return "";

		return Tools.join(getTravelerSet(), ",");

	}

	public SortedSet<String> getTravelerSet() {
		return mTravelerSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mCurrency == null) ? 0 : mCurrency.hashCode());
		result = prime * result + ((mDate == null) ? 0 : mDate.hashCode());
		result = prime * result
				+ ((mDefault == null) ? 0 : mDefault.hashCode());
		result = prime * result
				+ ((mDestination == null) ? 0 : mDestination.hashCode());
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result
				+ ((mTravelerSet == null) ? 0 : mTravelerSet.hashCode());
		return result;
	}

	public void setCurrency(String currency) {
		if (!mCurrency.equals(currency)) {
			mDirty = true;
			mCurrency = currency;
		}

	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {

		if (!mDate.equals(date)) {
			mDirty = true;
			mDate = date;
		}
	}

	/**
	 * @param default_
	 *            the default to set
	 */
	public void setDefault(Boolean default_) {
		if (!mDefault.equals(default_)) {
			mDirty = true;
			mDefault = default_;
		}
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		if (!mDestination.equals(destination)) {
			mDirty = true;
			mDestination = destination;
		}
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		mId = id;
	}

	public void setTravelerSet(SortedSet<String> travelerSet) {
		if (!mTravelerSet.equals(travelerSet)) {
			mDirty = true;
			mTravelerSet = travelerSet;
		}
	}

	public void setTravelerSet(String travelers) {

		String[] travelerArray = travelers.split(",");
		SortedSet<String> travelerSet = new TreeSet<>();
        Collections.addAll(travelerSet, travelerArray);

		setTravelerSet(travelerSet);

	}

    @Override
	public String toString() {
		return String.format("%s:%s", getDestination(), getDate());
	}

}
