package com.gagnon.mario.mr.traveloid.core.logger;

public interface Logger {

	void debug(String className, String methodName,
							   String message);

	void error(Exception exception);

	void error(String message);

	void info(String message);

}
