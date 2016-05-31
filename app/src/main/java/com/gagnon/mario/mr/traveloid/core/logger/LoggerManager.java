package com.gagnon.mario.mr.traveloid.core.logger;

public class LoggerManager {

	private final static LoggerManager mInstance = new LoggerManager();

	public static void debug(String className, String methodName, String message) {
		getInstance().getLogger().debug(className, methodName, message);
	}

	public static void error(Exception exception) {
		getInstance().getLogger().error(exception);
	}

	public static void error(String message) {
		getInstance().getLogger().error(message);
	}

	public static LoggerManager getInstance() {
		return mInstance;
	}

	public static void info(String message) {
		getInstance().getLogger().info(message);
	}

	private Logger mLogger = null;

	private LoggerManager() {
	}

	public Logger getLogger() {

		if (null == mLogger) {
			mLogger = new LoggerImpl();
		}

		return mLogger;
	}

	public void setLogger(Logger logger) {
		mLogger = logger;
	}
}
