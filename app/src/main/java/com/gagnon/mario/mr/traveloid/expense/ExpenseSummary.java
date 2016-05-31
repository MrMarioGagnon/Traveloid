package com.gagnon.mario.mr.traveloid.expense;

import java.io.Serializable;

public class ExpenseSummary implements Serializable {

	private static final long serialVersionUID = 6128543723769464633L;
	private String mTraveler;
	private double mTodayExpense;
	private double mTotalExpense;

	public ExpenseSummary(String traveler, double todayExpense,
			double totalExpense) {
		mTraveler = traveler;
		mTodayExpense = todayExpense;
		mTotalExpense = totalExpense;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpenseSummary other = (ExpenseSummary) obj;
		if (Double.doubleToLongBits(mTodayExpense) != Double
				.doubleToLongBits(other.mTodayExpense))
			return false;
		if (Double.doubleToLongBits(mTotalExpense) != Double
				.doubleToLongBits(other.mTotalExpense))
			return false;
		if (mTraveler == null) {
			if (other.mTraveler != null)
				return false;
		} else if (!mTraveler.equals(other.mTraveler))
			return false;
		return true;
	}

	public double getTodayExpense() {
		return mTodayExpense;
	}

	public double getTotalExpense() {
		return mTotalExpense;
	}

	public String getTraveler() {
		return mTraveler;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(mTodayExpense);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mTotalExpense);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((mTraveler == null) ? 0 : mTraveler.hashCode());
		return result;
	}

	public void setTodayExpense(double todayExpense) {
		mTodayExpense = todayExpense;
	}

	public void setTotalExpense(double totalExpense) {
		mTotalExpense = totalExpense;
	}

	public void setTraveler(String traveler) {
		mTraveler = traveler;
	}

	@Override
	public String toString() {
		return "ExpenseSummary [mTraveler=" + mTraveler + ", mTodayExpense="
				+ mTodayExpense + ", mTotalExpense=" + mTotalExpense + "]";
	}

}
