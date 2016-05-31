package com.gagnon.mario.mr.traveloid.core;

import android.os.*;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class Tools {

	private static String _now(String stringFormat) {

		Date now = new Date();

		SimpleDateFormat format = new SimpleDateFormat(stringFormat);

		String parsed = null;
		parsed = format.format(now);
		return parsed;

	}

	public static <T> T coalesce(T... items) {
		for (T i : items)
			if (i != null)
				return i;
		return null;
	}

	public static void copy(String sourcePath, String destinationPath)
			throws Exception {

		File sd = Environment.getExternalStorageDirectory();

		File data = Environment.getDataDirectory();

		createDirectory("traveloid");

		File sdTraveloid = new File(sd, "traveloid");

		if (sd.canWrite()) {

			String sourceFilePath = "/data/com.gagnon.mario.mr.traveloid/databases/traveloid.db";

			String now = now("yyyyMMddkkmmss");

			String destinationFilePath = "Traveloid" + now + ".db";

			File currentDB = new File(data, sourceFilePath);

			File backupDB = new File(sdTraveloid, destinationFilePath);

			if (currentDB.exists()) {

				FileChannel src = new FileInputStream(currentDB).getChannel();

				FileChannel dst = new FileOutputStream(backupDB).getChannel();

				dst.transferFrom(src, 0, src.size());

				src.close();

				dst.close();
			}
		}

	}

	public static void createDirectory(String directory) {

		File wallpaperDirectory = new File("/sdcard/" + directory + "/");
		wallpaperDirectory.mkdirs();
	}

	public static DatePart DatePartExtractor(String date) throws ParseException {
		return Tools.DatePartExtractor(date, "-");
	}

	public static DatePart DatePartExtractor(String dateString, String separator)
			throws ParseException {

		String dateFormat = String.format("yyyy%sMM%sdd", separator, separator);
		DateFormat df = new SimpleDateFormat(dateFormat);

		Date date = df.parse(dateString);

		return new DatePart(date.getYear() + 1900, date.getMonth(),
				date.getDate());

	}

	public static boolean isExternalStorageAvailable() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	public static <T> String join(final Collection<T> objs,
			final String delimiter) {
		if (objs == null || objs.isEmpty())
			return "";
		Iterator<T> iter = objs.iterator();
		StringBuffer buffer = new StringBuffer(iter.next().toString());
		while (iter.hasNext())
			buffer.append(delimiter).append(iter.next().toString());
		return buffer.toString();
	}

	public static String now() {

		return Tools._now("yyyy-MM-dd kk:mm:ss");

	}

	public static String now(String stringFormat) {

		return Tools._now(stringFormat);

	}

	public static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

}
