package com.gagnon.mario.mr.traveloid.pettycash;

import com.gagnon.mario.mr.traveloid.core.ObjectBase;

import java.io.Serializable;

//import com.google.common.base.Preconditions;

public class PettyCash extends ObjectBase implements Serializable {

	public static PettyCash create(Long id, Long tripId,
			String name, String description, String currency,
			Double exchangeRate, Double amount) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		PettyCash pettyCash = new PettyCash();
		pettyCash.mNew = false;
		pettyCash.mDirty = false;
		pettyCash.mId = id;
		pettyCash.mTripId = tripId;
		pettyCash.mName = name;
		pettyCash.mDescription = description;
		pettyCash.mCurrency = currency;
		pettyCash.mExchangeRate = exchangeRate;
		pettyCash.mAmount = amount;

		return pettyCash;
	}

	public static PettyCash createNew() {

		PettyCash pettyCash = new PettyCash();
		pettyCash.mNew = true;
		pettyCash.mDirty = true;
		pettyCash.mName = "";
		pettyCash.mDescription = "";
		pettyCash.mCurrency = "";
		pettyCash.mExchangeRate = 1.0;
		pettyCash.mAmount = 0.0;

		return pettyCash;
	}

	private Long mId;
	private Long mTripId;
	private String mName;
	private String mDescription;
	private String mCurrency;
	private Double mExchangeRate;
	private Double mAmount;

	private PettyCash() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PettyCash other = (PettyCash) obj;
		if (mAmount == null) {
			if (other.mAmount != null)
				return false;
		} else if (!mAmount.equals(other.mAmount))
			return false;
		if (mCurrency == null) {
			if (other.mCurrency != null)
				return false;
		} else if (!mCurrency.equals(other.mCurrency))
			return false;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
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
		if (mTripId == null) {
			if (other.mTripId != null)
				return false;
		} else if (!mTripId.equals(other.mTripId))
			return false;
		return true;
	}

	public Long getId() {
		return mId;
	}
	public Long getTripId() {
		return mTripId;
	}
	public String getName() {
		return mName;
	}
	public String getDescription() {
		return mDescription;
	}
	public String getCurrency() {
		return mCurrency;
	}
	public Double getExchangeRate() {
		return mExchangeRate;
	}
	public Double getAmount() {
		return mAmount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAmount == null) ? 0 : mAmount.hashCode());
		result = prime * result
				+ ((mCurrency == null) ? 0 : mCurrency.hashCode());
		result = prime * result
				+ ((mDescription == null) ? 0 : mDescription.hashCode());
		result = prime * result
				+ ((mName == null) ? 0 : mName.hashCode());
		result = prime * result
				+ ((mExchangeRate == null) ? 0 : mExchangeRate.hashCode());
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((mTripId == null) ? 0 : mTripId.hashCode());
		return result;
	}


	public void setId(Long id) {
		mId = id;
	}

	public void setTripId(Long tripId) {
		if (null == mTripId || !mTripId.equals(tripId)) {
			mDirty = true;
			mTripId = tripId;
		}
	}

	public void setName(String name) {
		if (!mName.equals(name)) {
			mDirty = true;
			mName = name;
		}
	}

	public void setDescription(String description) {
		if (!mDescription.equals(description)) {
			mDirty = true;
			mDescription = description;
		}
	}

	public void setCurrency(String currency) {
		if (!mCurrency.equals(currency)) {
			mDirty = true;
			mCurrency = currency;
		}
	}

	public void setExchangeRate(Double exchangeRate) {
		if (null == mExchangeRate || !mExchangeRate.equals(exchangeRate)) {
			mDirty = true;
			mExchangeRate = exchangeRate;
		}
	}

	public void setAmount(Double amount) {
		if (null == mAmount || !mAmount.equals(amount)) {
			mDirty = true;
			mAmount = amount;
		}
	}

	public void synchronize(PettyCash o) {
		setAmount(o.getAmount());
		setDescription(o.getDescription());
		setName(o.getName());
		setCurrency(o.getCurrency());
		setExchangeRate(o.getExchangeRate());
	}

	@Override
	public String toString()
	{
		return 	String.format("%s, %.2f %s", mName, mAmount, mCurrency);
	}



}
