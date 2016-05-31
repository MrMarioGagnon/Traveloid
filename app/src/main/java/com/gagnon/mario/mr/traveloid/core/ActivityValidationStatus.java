package com.gagnon.mario.mr.traveloid.core;

public class ActivityValidationStatus {

	public static ActivityValidationStatus create(String message) {
		return new ActivityValidationStatus(message);
	}

	private String mMessage = "";

	private ActivityValidationStatus(String message) {
		mMessage = message;
	}

	public String getMessage() {
		return mMessage;
	}

	public boolean isValid() {
		return mMessage.length() == 0;
	}

}
