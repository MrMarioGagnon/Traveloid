package com.gagnon.mario.mr.traveloid.core;

public class DatePart {

	private int mYear;
	private int mMonth;
	private int mDay;

	public DatePart(int year, int month, int day) {

		mYear = year;
		mMonth = month;
		mDay = day;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatePart other = (DatePart) obj;
		if (mDay != other.mDay)
			return false;
		if (mMonth != other.mMonth)
			return false;
        return mYear == other.mYear;
    }

	/**
	 * @return the day
	 */
	public int getDay() {
		return mDay;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return mMonth;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return mYear;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mDay;
		result = prime * result + mMonth;
		result = prime * result + mYear;
		return result;
	}

	/**
	 * @param day
	 *            the day to set
	 */
	public void setDay(int day) {
		mDay = day;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(int month) {
		mMonth = month;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		mYear = year;
	}

	@Override
	public String toString() {
		return "DatePart [mYear=" + mYear + ", mMonth=" + mMonth + ", mDay="
				+ mDay + "]";
	}

}
