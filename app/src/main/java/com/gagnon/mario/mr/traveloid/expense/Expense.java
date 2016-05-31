package com.gagnon.mario.mr.traveloid.expense;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

//import com.google.common.base.Preconditions;
import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.ObjectBase;
import com.gagnon.mario.mr.traveloid.core.Tools;

public class Expense extends ObjectBase implements Serializable {

	private static final long serialVersionUID = 7570967871331696819L;

	public static Expense create(Long id, Long tripId, String date,
			String category, Double amount, String paymentMethod,
			String reference, String description,
			SortedSet<String> travelerSet, String currency,
			SortedSet<String> payorSet, Double exchangeRate) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		Expense expense = new Expense();
		expense.mNew = false;
		expense.mDirty = false;
		expense.mId = id;
		expense.mTripId = tripId;
		expense.mDate = date;
		expense.mCategory = category;
		expense.mAmount = amount;
		expense.mPaymentMethod = paymentMethod;
		expense.mReference = reference;
		expense.mDescription = description;
		expense.mTravelerSet = travelerSet;
		expense.mCurrency = currency;
		expense.mPayorSet = payorSet;
		expense.mExchangeRate = exchangeRate;

		return expense;
	}

	public static Expense create(Long id, Long tripId, String date,
			String category, Double amount, String paymentMethod,
			String reference, String description, String travelers,
			String currency, String payors, Double exchangeRate) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		String[] travelerArray = travelers.split("\\|");
		SortedSet<String> travelerSet = new TreeSet<>();
        Collections.addAll(travelerSet, travelerArray);

		String[] payorArray = payors.split("\\|");
		SortedSet<String> payorSet = new TreeSet<>();
        Collections.addAll(payorSet, payorArray);

		return create(id, tripId, date, category, amount, paymentMethod,
				reference, description, travelerSet, currency, payorSet,
				exchangeRate);
	}

	public static Expense createNew() {

		Expense expense = new Expense();
		expense.mNew = true;
		expense.mDirty = true;
		expense.mDate = "";
		expense.mCategory = "";
		expense.mAmount = 0.0;
		expense.mPaymentMethod = "";
		expense.mReference = "";
		expense.mDescription = "";
		expense.mTravelerSet = new TreeSet<>();
		expense.mCurrency = "";
		expense.mPayorSet = new TreeSet<>();
		expense.mExchangeRate = 1.0;

		return expense;
	}

	private Double mAmount;
	private String mCategory;
	private String mCurrency;

	private String mDate;

	private String mDescription;

	private Double mExchangeRate;
	private Long mId;
	private String mPaymentMethod;
	private SortedSet<String> mPayorSet;
	private String mReference;
	private SortedSet<String> mTravelerSet;
	private Long mTripId;

	private Expense() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expense other = (Expense) obj;
		if (mAmount == null) {
			if (other.mAmount != null)
				return false;
		} else if (!mAmount.equals(other.mAmount))
			return false;
		if (mCategory == null) {
			if (other.mCategory != null)
				return false;
		} else if (!mCategory.equals(other.mCategory))
			return false;
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
		if (mDescription == null) {
			if (other.mDescription != null)
				return false;
		} else if (!mDescription.equals(other.mDescription))
			return false;
		if (mExchangeRate == null) {
			if (other.mExchangeRate != null)
				return false;
		} else if (!mExchangeRate.equals(other.mExchangeRate))
			return false;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (mPaymentMethod == null) {
			if (other.mPaymentMethod != null)
				return false;
		} else if (!mPaymentMethod.equals(other.mPaymentMethod))
			return false;
		if (mPayorSet == null) {
			if (other.mPayorSet != null)
				return false;
		} else if (!mPayorSet.equals(other.mPayorSet))
			return false;
		if (mReference == null) {
			if (other.mReference != null)
				return false;
		} else if (!mReference.equals(other.mReference))
			return false;
		if (mTravelerSet == null) {
			if (other.mTravelerSet != null)
				return false;
		} else if (!mTravelerSet.equals(other.mTravelerSet))
			return false;
		if (mTripId == null) {
			if (other.mTripId != null)
				return false;
		} else if (!mTripId.equals(other.mTripId))
			return false;
		return true;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return mAmount;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return mCategory;
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
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	public Double getExchangeRate() {
		return mExchangeRate;
	}

	public Long getId() {
		return mId;
	}

	/**
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return mPaymentMethod;
	}

	public String getPayorAsString() {
		return Tools.join(getPayorSet(), "|");
	}

	public String getPayorForDisplay() {

		if (null == getPayorSet())
			return "";

		return Tools.join(getPayorSet(), ",");

	}

	public SortedSet<String> getPayorSet() {
		return mPayorSet;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return mReference;
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

	public Long getTripId() {
		return mTripId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAmount == null) ? 0 : mAmount.hashCode());
		result = prime * result
				+ ((mCategory == null) ? 0 : mCategory.hashCode());
		result = prime * result
				+ ((mCurrency == null) ? 0 : mCurrency.hashCode());
		result = prime * result + ((mDate == null) ? 0 : mDate.hashCode());
		result = prime * result
				+ ((mDescription == null) ? 0 : mDescription.hashCode());
		result = prime * result
				+ ((mExchangeRate == null) ? 0 : mExchangeRate.hashCode());
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result
				+ ((mPaymentMethod == null) ? 0 : mPaymentMethod.hashCode());
		result = prime * result
				+ ((mPayorSet == null) ? 0 : mPayorSet.hashCode());
		result = prime * result
				+ ((mReference == null) ? 0 : mReference.hashCode());
		result = prime * result
				+ ((mTravelerSet == null) ? 0 : mTravelerSet.hashCode());
		result = prime * result + ((mTripId == null) ? 0 : mTripId.hashCode());
		return result;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		if (null == mAmount || !mAmount.equals(amount)) {
			mDirty = true;
			mAmount = amount;
		}
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		if (!mCategory.equals(category)) {
			mDirty = true;
			mCategory = category;
		}
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
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		if (!mDescription.equals(description)) {
			mDirty = true;
			mDescription = description;
		}
	}

	public void setExchangeRate(Double exchangeRate) {
		if (null == mExchangeRate || !mExchangeRate.equals(exchangeRate)) {
			mDirty = true;
			mExchangeRate = exchangeRate;
		}
	}

	public void setId(Long id) {
		mId = id;
	}

	/**
	 * @param paymentMethod
	 *            the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		if (!mPaymentMethod.equals(paymentMethod)) {
			mDirty = true;
			mPaymentMethod = paymentMethod;
		}
	}

	public void setPayorSet(SortedSet<String> payorSet) {
		if (!mPayorSet.equals(payorSet)) {
			mDirty = true;
			mPayorSet = payorSet;
		}
	}

	public void setPayorSet(String payors) {

		String[] payorArray = payors.split(",");
		SortedSet<String> payorSet = new TreeSet<>();
        Collections.addAll(payorSet, payorArray);

		setPayorSet(payorSet);

	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String reference) {
		if (!mReference.equals(reference)) {
			mDirty = true;
			mReference = reference;
		}
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

	public void setTripId(Long tripId) {
		if (null == mTripId || !mTripId.equals(tripId)) {
			mDirty = true;
			mTripId = tripId;
		}
	}

	public void synchronize(Expense o) {
		setDate(o.getDate());
		setCategory(o.getCategory());
		setAmount(o.getAmount());
		setPaymentMethod(o.getPaymentMethod());
		setReference(o.getReference());
		setDescription(o.getDescription());
		setTravelerSet(o.getTravelerSet());
		setCurrency(o.getCurrency());
		setPayorSet(o.getPayorSet());
		setExchangeRate(o.getExchangeRate());
	}

	@Override
	public String toString() {
		return mDate + ", " + mCategory + ", " + mAmount;
	}

}
