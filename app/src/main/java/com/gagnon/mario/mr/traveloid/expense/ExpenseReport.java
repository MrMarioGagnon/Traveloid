package com.gagnon.mario.mr.traveloid.expense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import android.content.Context;
import android.os.Environment;

import com.gagnon.mario.mr.traveloid.core.Tools;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.core.database.RepositoryException;
import com.gagnon.mario.mr.traveloid.trip.Trip;

public class ExpenseReport {

	private Context mContext;
	private BufferedWriter out;

	public ExpenseReport(Context context) throws Throwable {
		super();
		try {
			mContext = context;

			if (!Tools.isExternalStorageAvailable())
				throw new IOException("SD Card not available.");

			File sd = Environment.getExternalStorageDirectory();

			File sdTraveloid = new File(sd, "traveloid");

			if (!sdTraveloid.exists()) {
				boolean directoryCreated = sdTraveloid.mkdir();
				if (!directoryCreated)
					throw new IOException(String.format(
							"Unable to create directory : %s.",
							sdTraveloid.getName()));
			}

			String now = Tools.now("yyyyMMddkkmmss");
			String destinationFilePath = String.format("r%s.rpt", now);
			File backupDB = new File(sdTraveloid, destinationFilePath);

			// Create file
			FileWriter fstream = new FileWriter(backupDB);
			out = new BufferedWriter(fstream);

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			throw e;
		}

	}

/*	private void addCategoryHeader(String category,
			SortedSet<String> travelers, Expense expense) {

		String row = Tools.join(travelers, "\t");

		write(category);
		write("\t");
		write(row);

	}
*/
	private void addDetail(SortedSet<String> travelers, Expense expense) {

		String line = String.format("%s\t%s\t%s\t%s\t%.2f\t%.2f\t%s\t%s\t%s\t%s",
				expense.getCategory(), expense.getTravelerForDisplay(),
				expense.getPayorForDisplay(), expense.getDate(),
				expense.getAmount(), expense.getExchangeRate() , expense.getCurrency(),
				expense.getPaymentMethod(), expense.getDescription(),
				expense.getReference());

		write(line);

	}

	private void addTotal(SortedSet<String> travelers, Expense expense) {
		// TODO Auto-generated method stub

	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void create(Trip trip) {

		List<Expense> expensesList;
		SortedSet<String> travelers = trip.getTravelerSet();

		DbAdapter dbAdapter = null;
		try {
			dbAdapter = new DbAdapterImpl(mContext);
			expensesList = dbAdapter.expenseFetchForTrip(trip.getId());

			String currentCategory = null;

			for (Expense expense : expensesList) {

				String category = expense.getCategory();

				if (!category.equals(currentCategory)) {

					if (null != currentCategory) {
						addTotal(travelers, expense);
					}

					// addCategoryHeader(category, travelers, expense);

					currentCategory = category;

				}

				addDetail(travelers, expense);

			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void write(String line) {
		try {
			out.write(line);
			out.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
