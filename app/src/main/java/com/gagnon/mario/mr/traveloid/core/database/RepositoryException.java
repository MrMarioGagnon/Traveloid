package com.gagnon.mario.mr.traveloid.core.database;

public class RepositoryException extends Exception {

	private static final long serialVersionUID = 724124399251281949L;

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public RepositoryException(Throwable throwable) {
		super(throwable);
	}

}