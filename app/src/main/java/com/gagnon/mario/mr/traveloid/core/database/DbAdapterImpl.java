/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gagnon.mario.mr.traveloid.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.Traveloid;
import com.gagnon.mario.mr.traveloid.Traveloid.Trips;
import com.gagnon.mario.mr.traveloid.category.Category;
import com.gagnon.mario.mr.traveloid.core.logger.LoggerManager;
import com.gagnon.mario.mr.traveloid.expense.Expense;
import com.gagnon.mario.mr.traveloid.pettycash.PettyCash;
import com.gagnon.mario.mr.traveloid.traveler.Traveler;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
/**
 * @author grandgalot
 * 
 */
public class DbAdapterImpl implements DbAdapter {

	private static class DatabaseHelper extends SQLiteOpenHelper {

		// Context mContext;

		DatabaseHelper(Context context) {
			super(context, Traveloid.DATABASE_NAME, null,
					Traveloid.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_TRIP_CREATE);
			db.execSQL(TABLE_EXPENSE_CREATE);
			db.execSQL(TABLE_TRAVELER_CREATE);
			db.execSQL(TABLE_CATEGORY_CREATE);
			db.execSQL(TABLE_PETTYCASH_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS Trips");
			db.execSQL("DROP TABLE IF EXISTS Categories");
			db.execSQL("DROP TABLE IF EXISTS Expenses");
			db.execSQL("DROP TABLE IF EXISTS Travelers");
			db.execSQL("DROP TABLE IF EXISTS PettyCash");
			onCreate(db);
		}
	}

	 //region Database creation sql statement
	private static final String TABLE_TRIP_CREATE = "create table "
			+ Trips.TABLE_NAME + " (_id integer primary key autoincrement, "
			+ Trips.DESTINATION + " text not null," + Trips.DATE
			+ " text not null," + Trips.DEFAULT + " integer," + Trips.TRAVELER
			+ " text not null," + Trips.CURRENCY + " text not null,"
			+ Trips.CREATED_DATE + " integer," + Trips.MODIFIED_DATE
			+ " integer);";

	private static final String TABLE_EXPENSE_CREATE = "create table "
			+ Traveloid.Expenses.TABLE_NAME + " (_id integer primary key autoincrement, "
			+ Traveloid.Expenses.TRIP_ID + " integer not null," + Traveloid.Expenses.DATE
			+ " text not null," + Traveloid.Expenses.CATEGORY + " text not null,"
			+ Traveloid.Expenses.AMOUNT + " numeric not null," + Traveloid.Expenses.PAYMENT_METHOD
			+ " text not null," + Traveloid.Expenses.REFERENCE_NO + " text,"
			+ Traveloid.Expenses.DESCRIPTION + " text," + Traveloid.Expenses.TRAVELER
			+ " text not null," + Traveloid.Expenses.PAYOR + " text,"
			+ Traveloid.Expenses.CURRENCY + " text not null,"
			+ Traveloid.Expenses.EXCHANGE_RATE + " numeric not null default 1,"
			+ Traveloid.Expenses.CREATED_DATE + " integer," + Traveloid.Expenses.MODIFIED_DATE
			+ " integer);";

	private static final String TABLE_PETTYCASH_CREATE = "create table "
			+ Traveloid.PettyCash.TABLE_NAME + " (_id integer primary key autoincrement, "
			+ Traveloid.PettyCash.TRIP_ID + " integer not null,"
			+ Traveloid.PettyCash.NAME + " text not null,"
			+ Traveloid.PettyCash.DESCRIPTION + " text not null,"
			+ Traveloid.PettyCash.CURRENCY + " text not null,"
			+ Traveloid.PettyCash.EXCHANGE_RATE + " numeric not null default 1,"
			+ Traveloid.PettyCash.AMOUNT + " numeric not null,"
			+ Traveloid.PettyCash.CREATED_DATE + " integer,"
			+ Traveloid.PettyCash.MODIFIED_DATE + " integer);";

	private static final String TABLE_TRAVELER_CREATE = "create table "
			+ Traveloid.Travelers.TABLE_NAME
			+ " (_id integer primary key autoincrement, " + Traveloid.Travelers.NAME
			+ " text not null," + Traveloid.Travelers.CREATED_DATE + " integer,"
			+ Traveloid.Travelers.MODIFIED_DATE + " integer);";

	private static final String TABLE_CATEGORY_CREATE = "create table "
			+ Traveloid.Categories.TABLE_NAME
			+ " (_id integer primary key autoincrement, " + Traveloid.Categories.NAME
			+ " text not null," + Traveloid.Categories.SUB_CATEGORY + " text not null,"
			+ Traveloid.Categories.CREATED_DATE + " integer," + Traveloid.Categories.MODIFIED_DATE
			+ " integer);";
	//endregion

	private static final String TAG = "DbAdapterImpl";
	private DatabaseHelper mDbHelper;
    private Context mContext = null;

	//region Constructor
	public DbAdapterImpl(Context ctx) {
		mContext = ctx;
		mDbHelper = new DatabaseHelper(ctx);
	}
	//endregion

	//region Category Methods

    //region Private
	private Category categoryAdd(SQLiteDatabase db, Category category) throws RepositoryException {

        String name = category.getName();
        String subCategories = category.getSubCategoriesAsString();
        String [] subCategoryArray = category.getSubCategories();

		ContentValues initialValues = new ContentValues();
        initialValues.put(Traveloid.Categories.NAME, name);
		initialValues.put(Traveloid.Categories.SUB_CATEGORY, subCategories);

		Long now = System.currentTimeMillis();
		initialValues.put(Traveloid.Categories.CREATED_DATE, now);
		initialValues.put(Traveloid.Categories.MODIFIED_DATE, now);

		Long id = db.insert(Traveloid.Categories.TABLE_NAME, null, initialValues);
        if(id < 1){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_add_category));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_add_category));
        }

        return Category.create(id, name, subCategoryArray);

	}

	private Category categoryDelete(SQLiteDatabase db, Category category) throws RepositoryException {

		String criteria = String.format("%s=?", Traveloid.Categories._ID);

        Boolean isDeleted = db.delete(Traveloid.Categories.TABLE_NAME, criteria, new String[] {category.getId().toString()}) > 0;

        if(!isDeleted){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_category));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_delete_category));
        }

        return null;

	}

    private Category categoryUpdate(SQLiteDatabase db, Category category) throws RepositoryException {

        String criteria = String.format("%s=?", Traveloid.Categories._ID);

        Long id = category.getId();
        String name = category.getName();
        String subCategories = category.getSubCategoriesAsString();
        String [] subCategoryArray = category.getSubCategories();

        ContentValues args = new ContentValues();
        args.put(Traveloid.Categories.NAME, name);
        args.put(Traveloid.Categories.SUB_CATEGORY, subCategories);

        Long now = System.currentTimeMillis();
        args.put(Traveloid.Categories.MODIFIED_DATE, now);

        Boolean isUpdated = db.update(Traveloid.Categories.TABLE_NAME, args, criteria, new String[]{id.toString()}) > 0;

        if(!isUpdated){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_category));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_update_category));
        }
        return Category.create(id, name, subCategoryArray);
    }

	private Cursor categoryGetCursor(SQLiteDatabase db, String selection, String[] selectionArgs) {

		Cursor cursor = db.query(Traveloid.Categories.TABLE_NAME, new String[]{
                        Traveloid.Categories._ID, Traveloid.Categories.NAME, Traveloid.Categories.SUB_CATEGORY},
                selection, selectionArgs, null, null, Traveloid.Categories.NAME);

		if (null != cursor) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	private Category categoryExtractFromCursor(Cursor cursor) {

		Category category = null;

		if (null != cursor && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {

			int colIndex = cursor.getColumnIndex(Traveloid.Categories._ID);
			Long rowId = cursor.getLong(colIndex);

			colIndex = cursor.getColumnIndex(Traveloid.Categories.NAME);
			String name = cursor.getString(colIndex);

			colIndex = cursor.getColumnIndex(Traveloid.Categories.SUB_CATEGORY);
			String subCategoriesColumn = cursor.getString(colIndex);

			String[] subCategories = subCategoriesColumn.split("\\|");

			category = Category.create(rowId, name, subCategories);

		}

		return category;

	}

    private List<Category> categoryFetch(SQLiteDatabase db, String selection, String[] selectionArgs) throws RepositoryException {

        List<Category> categoryList = null;

        SQLiteDatabase innerDb;
        if(null == db) {
            innerDb = mDbHelper.getReadableDatabase();
        }
        else{
            innerDb = db;
        }
        if(null == innerDb){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_category));
        }

        Cursor cursor = null;
        try {
            cursor = this.categoryGetCursor(innerDb, selection, selectionArgs);
            categoryList = new ArrayList<>();

            Category category;
            while (!cursor.isAfterLast()) {

                category = categoryExtractFromCursor(cursor);

                if (null != category)
                    categoryList.add(category);

                cursor.moveToNext();

            }
        } catch (Exception exception) {
            LoggerManager.error(exception);
            throw new RepositoryException(
                    mContext.getString(R.string.error_unable_to_fetch_category));

        }
        finally{
            if(null != cursor){
                cursor.close();
            }
            if(null == db) {
                innerDb.close();
            }
        }

        return categoryList;

    }

    private Category categorySave(SQLiteDatabase db, Category category) throws RepositoryException {

        SQLiteDatabase innerDb;
        if (null == db){
            innerDb = mDbHelper.getWritableDatabase();
        }
        else{
            innerDb = db;
        }

        if(null == innerDb){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_save_object_to_database));
        }

        try {
            if (category.isDead()) {
                return categoryDelete(innerDb, category);
            }

            if (category.isNew()) {
                return categoryAdd(innerDb, category);
            }

            return categoryUpdate(innerDb, category);
        } catch (Exception exception) {
            LoggerManager.error(exception);
            throw new RepositoryException(
                    mContext.getString(R.string.error_unable_to_save_object_to_database));
        }
        finally{
            if(null == db) {
                innerDb.close();
            }
        }

    }

    //endregion

    //region Public

    @Override
    public Category categorySave(Category category) throws RepositoryException {
        return categorySave(null, category);
    }

    @Override
    public List<Category> categoryFetchAll() throws RepositoryException{
        return categoryFetch(null, null, null);
    }
    //endregion

	//endregion

	//region Expense Methods

	//region Private
	private Expense expenseAdd(SQLiteDatabase db, Expense expense) throws RepositoryException {

        Long tripId = expense.getTripId();
        String date = expense.getDate();
        String category = expense.getCategory();
        Double amount = expense.getAmount();
        String paymentMethod = expense.getPaymentMethod();
        String reference = expense.getReference();
        String description = expense.getDescription();
        String traveler = expense.getTravelerAsString();
        String currency = expense.getCurrency();
        String payor = expense.getPayorAsString();
        Double exchangeRate = expense.getExchangeRate();

		ContentValues initialValues = new ContentValues();
		initialValues.put(Traveloid.Expenses.TRIP_ID, tripId);
		initialValues.put(Traveloid.Expenses.DATE, date);
		initialValues.put(Traveloid.Expenses.CATEGORY, category);
		initialValues.put(Traveloid.Expenses.AMOUNT, amount);
		initialValues.put(Traveloid.Expenses.PAYMENT_METHOD, paymentMethod);
		initialValues.put(Traveloid.Expenses.REFERENCE_NO, reference);
		initialValues.put(Traveloid.Expenses.DESCRIPTION, description);
		initialValues.put(Traveloid.Expenses.TRAVELER, traveler);
		initialValues.put(Traveloid.Expenses.CURRENCY, currency);
		initialValues.put(Traveloid.Expenses.PAYOR, payor);
		initialValues.put(Traveloid.Expenses.EXCHANGE_RATE, exchangeRate);

		Long now = System.currentTimeMillis();
		initialValues.put(Traveloid.Expenses.CREATED_DATE, now);
		initialValues.put(Traveloid.Expenses.MODIFIED_DATE, now);

		Long id = db.insert(Traveloid.Expenses.TABLE_NAME, null, initialValues);
        if(id < 1){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_add_expense));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_add_expense));
        }
		return Expense.create(id, tripId, date, category, amount, paymentMethod, reference,description, traveler,currency,payor,exchangeRate);
	}

	private Expense expenseDelete(SQLiteDatabase db, Expense expense) throws RepositoryException {

		String criteria = String.format("%s=?", Traveloid.Expenses._ID);

		Boolean isDeleted = db.delete(Traveloid.Expenses.TABLE_NAME, criteria, new String[]{expense.getId().toString()}) > 0;

        if(!isDeleted){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_expense));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_delete_expense));
        }

        return null;

	}

	private Expense expenseUpdate(SQLiteDatabase db, Expense expense) throws RepositoryException {

        String criteria = String.format("%s=?", Traveloid.Expenses._ID);

        Long id = expense.getId();
        Long tripId = expense.getTripId();
        String date = expense.getDate();
        String category = expense.getCategory();
        Double amount = expense.getAmount();
        String paymentMethod = expense.getPaymentMethod();
        String reference = expense.getReference();
        String description = expense.getDescription();
        String traveler = expense.getTravelerAsString();
        String currency = expense.getCurrency();
        String payor = expense.getPayorAsString();
        Double exchangeRate = expense.getExchangeRate();

		ContentValues args = new ContentValues();
		args.put(Traveloid.Expenses.TRIP_ID, tripId);
		args.put(Traveloid.Expenses.DATE, date);
		args.put(Traveloid.Expenses.CATEGORY, category);
		args.put(Traveloid.Expenses.AMOUNT, amount);
		args.put(Traveloid.Expenses.PAYMENT_METHOD, paymentMethod);
		args.put(Traveloid.Expenses.REFERENCE_NO, reference);
		args.put(Traveloid.Expenses.DESCRIPTION, description);
		args.put(Traveloid.Expenses.TRAVELER, traveler);
		args.put(Traveloid.Expenses.CURRENCY, currency);
		args.put(Traveloid.Expenses.PAYOR, payor);
		args.put(Traveloid.Expenses.EXCHANGE_RATE, exchangeRate);

		Long now = System.currentTimeMillis();
		args.put(Traveloid.Expenses.MODIFIED_DATE, now);

        Boolean isUpdated = db.update(Traveloid.Expenses.TABLE_NAME, args, criteria, new String[]{id.toString()}) > 0;

        if(!isUpdated){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_expense));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_update_expense));
        }
        return Expense.create(id, tripId, date, category, amount, paymentMethod, reference,description, traveler,currency,payor,exchangeRate);
	}

    private Expense expenseSave(SQLiteDatabase db, Expense expense) throws RepositoryException {

        SQLiteDatabase innerDb;
        if(null == db){
            innerDb = mDbHelper.getWritableDatabase();
        }
        else{
            innerDb = db;
        }

        if(null == innerDb){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_save_object_to_database));
        }

        try {
            if (expense.isDead()) {
                return expenseDelete(innerDb, expense);
            }

            if (expense.isNew()) {
                return expenseAdd(innerDb, expense);
            }

            return expenseUpdate(innerDb, expense);
        } catch (Exception exception) {
            LoggerManager.error(exception);
            throw new RepositoryException(
                    mContext.getString(R.string.error_unable_to_save_object_to_database));
        }
        finally{
            if(null == db) {
                innerDb.close();
            }
        }

    }

   	protected Expense expenseExtractFromCursor(Cursor cursor) {

		Expense expense = null;

		if (null != cursor && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {

			Long rowId = cursor.getLong(cursor
					.getColumnIndex(Traveloid.Expenses._ID));

			Long tripId = cursor.getLong(cursor
					.getColumnIndex(Traveloid.Expenses.TRIP_ID));

			String date = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.DATE));

			String category = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.CATEGORY));

			Double amount = cursor.getDouble(cursor
					.getColumnIndex(Traveloid.Expenses.AMOUNT));

			String paymentMethod = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.PAYMENT_METHOD));

			String referenceNo = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.REFERENCE_NO));

			String description = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.DESCRIPTION));

			String traveler = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.TRAVELER));

			String payor = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.PAYOR));

			String currency = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.CURRENCY));

			Double exchangeRate = cursor.getDouble(cursor
					.getColumnIndex(Traveloid.Expenses.EXCHANGE_RATE));

			expense = Expense.create(rowId, tripId, date, category, amount,
					paymentMethod, referenceNo, description, traveler,
					currency, payor, exchangeRate);

		}

		return expense;

	}

    private Cursor expenseGetCursor(SQLiteDatabase db, String selection, String[] selectionArgs, String orderBy) {

        Cursor cursor =

                db.query(true, Traveloid.Expenses.TABLE_NAME, new String[]{Traveloid.Expenses._ID,
                                Traveloid.Expenses.TRIP_ID, Traveloid.Expenses.DATE, Traveloid.Expenses.CATEGORY,
                                Traveloid.Expenses.AMOUNT, Traveloid.Expenses.PAYMENT_METHOD,
                                Traveloid.Expenses.REFERENCE_NO, Traveloid.Expenses.DESCRIPTION, Traveloid.Expenses.TRAVELER,
                                Traveloid.Expenses.CURRENCY, Traveloid.Expenses.PAYOR, Traveloid.Expenses.EXCHANGE_RATE}, selection, selectionArgs, null, null,
                        orderBy, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    private List<Expense> expenseFetch(SQLiteDatabase db, String selection, String[] selectionArgs, String orderBy) throws RepositoryException {

        List<Expense> expenseList = null;

        SQLiteDatabase innerDb;
        if(null == db) {
            innerDb = mDbHelper.getReadableDatabase();
        }
        else{
            innerDb = db;
        }
        if(null == innerDb){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_expense));
        }

        Cursor cursor = null;
        try {
            cursor = this.expenseGetCursor(innerDb, selection, selectionArgs, orderBy);
            expenseList = new ArrayList<>();

            Expense expense;
            while (!cursor.isAfterLast()) {

                expense = expenseExtractFromCursor(cursor);

                if (null != expense)
                    expenseList.add(expense);

                cursor.moveToNext();

            }
        } catch (Exception exception) {
            LoggerManager.error(exception);
            throw new RepositoryException(
                    mContext.getString(R.string.error_unable_to_fetch_expense));

        }
        finally{
            if(null != cursor){
                cursor.close();
            }
            if(null == db) {
                innerDb.close();
            }
        }

        return expenseList;

    }

    //endregion

    //region Public
    @Override
    public Expense expenseSave(Expense expense) throws RepositoryException {
        return expenseSave(null, expense);
    }

    @Override
    public List<Expense> expenseFetchForTrip(Long tripId) throws RepositoryException{
        String criteria = String.format("%s=?", Traveloid.Expenses.TRIP_ID);
        return expenseFetch(null, criteria, new String[]{tripId.toString()}, null);
    }

    //endregion

	//endregion

	//region PettyCash Methods

	//region Private
	private PettyCash pettyCashAdd(SQLiteDatabase db, PettyCash pettyCash) throws RepositoryException {

		Long tripId = pettyCash.getTripId();
		String name = pettyCash.getName();
		String description = pettyCash.getDescription();
		String currency = pettyCash.getCurrency();
		Double exchangeRate = pettyCash.getExchangeRate();
		Double amount = pettyCash.getAmount();

		ContentValues initialValues = new ContentValues();
		initialValues.put(Traveloid.PettyCash.TRIP_ID, tripId);
		initialValues.put(Traveloid.PettyCash.NAME, name);
		initialValues.put(Traveloid.PettyCash.DESCRIPTION, description);
		initialValues.put(Traveloid.PettyCash.CURRENCY, currency);
		initialValues.put(Traveloid.PettyCash.EXCHANGE_RATE, exchangeRate);
		initialValues.put(Traveloid.PettyCash.AMOUNT, amount);

		Long now = System.currentTimeMillis();
		initialValues.put(Traveloid.PettyCash.CREATED_DATE, now);
		initialValues.put(Traveloid.PettyCash.MODIFIED_DATE, now);

		Long id = db.insert(Traveloid.PettyCash.TABLE_NAME, null, initialValues);
        if(id < 1){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_add_pettycash));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_add_pettycash));
        }

		return PettyCash.create(id, tripId, name, description, currency, exchangeRate, amount);

	}

	private PettyCash pettyCashDelete(SQLiteDatabase db, PettyCash pettyCash) throws RepositoryException {

		String criteria = String.format("%s=?", Traveloid.PettyCash._ID);
        Boolean isDeleted = db.delete(Traveloid.PettyCash.TABLE_NAME, criteria, new String[]{pettyCash.getId().toString()}) > 0;

        if(!isDeleted){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_pettycash));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_delete_pettycash));
        }

		return null;

	}

	private PettyCash pettyCashUpdate(SQLiteDatabase db, PettyCash pettyCash) throws RepositoryException {

		String criteria = String.format("%s=?", Traveloid.PettyCash._ID);

        Long id = pettyCash.getId();
        Long tripId = pettyCash.getTripId();
        String name = pettyCash.getName();
        String description = pettyCash.getDescription();
        String currency = pettyCash.getCurrency();
        Double exchangeRate = pettyCash.getExchangeRate();
        Double amount = pettyCash.getAmount();

		ContentValues args = new ContentValues();
		args.put(Traveloid.PettyCash.TRIP_ID, tripId);
		args.put(Traveloid.PettyCash.NAME, name);
		args.put(Traveloid.PettyCash.DESCRIPTION, description);
		args.put(Traveloid.PettyCash.CURRENCY, currency);
		args.put(Traveloid.PettyCash.EXCHANGE_RATE, exchangeRate);
		args.put(Traveloid.PettyCash.AMOUNT, amount);

		Long now = System.currentTimeMillis();
		args.put(Traveloid.PettyCash.MODIFIED_DATE, now);

        Boolean isUpdated = db.update(Traveloid.PettyCash.TABLE_NAME, args, criteria, new String[]{id.toString()}) > 0;
        if(!isUpdated){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_pettycash));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_update_pettycash));
        }
        return PettyCash.create(id, tripId, name, description, currency, exchangeRate, amount);
	}

	private PettyCash pettyCashSave(SQLiteDatabase db, PettyCash pettyCash) throws RepositoryException {

		SQLiteDatabase innerDb;
		if(null == db){
			innerDb = mDbHelper.getWritableDatabase();
		}
		else{
			innerDb = db;
		}

		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_save_object_to_database));
		}

		try {
			if (pettyCash.isDead()) {
                return pettyCashDelete(innerDb, pettyCash);
			}

			if (pettyCash.isNew()) {
				return  pettyCashAdd(innerDb, pettyCash);
			}

			return pettyCashUpdate(innerDb, pettyCash);
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_save_object_to_database));
		}
		finally{
			if(null == db) {
				innerDb.close();
			}
		}

	}

	private PettyCash pettyCashExtractFromCursor(Cursor cursor) {

		PettyCash pettyCash = null;

		if (null != cursor && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {

			Long rowId = cursor.getLong(cursor
					.getColumnIndex(Traveloid.PettyCash._ID));

			Long tripId = cursor.getLong(cursor
					.getColumnIndex(Traveloid.PettyCash.TRIP_ID));

			String name = cursor.getString(cursor
					.getColumnIndex(Traveloid.PettyCash.NAME));

			String description = cursor.getString(cursor
					.getColumnIndex(Traveloid.PettyCash.DESCRIPTION));

			String currency = cursor.getString(cursor
					.getColumnIndex(Traveloid.Expenses.CURRENCY));

			Double exchangeRate = cursor.getDouble(cursor
					.getColumnIndex(Traveloid.Expenses.EXCHANGE_RATE));

			Double amount = cursor.getDouble(cursor
					.getColumnIndex(Traveloid.Expenses.AMOUNT));

			pettyCash = PettyCash.create(rowId, tripId, name, description, currency, exchangeRate, amount);

		}

		return pettyCash;

	}

	private Cursor pettyCashGetCursor(SQLiteDatabase db, String selection, String[] selectionArgs) {

		String orderBy = String.format("%s DESC", Traveloid.PettyCash.NAME);

		Cursor cursor =
				db.query(Traveloid.PettyCash.TABLE_NAME, new String[] { Traveloid.PettyCash._ID,
								Traveloid.PettyCash.TRIP_ID, Traveloid.PettyCash.NAME, Traveloid.PettyCash.DESCRIPTION,
								Traveloid.PettyCash.CURRENCY, Traveloid.PettyCash.EXCHANGE_RATE,
								Traveloid.PettyCash.AMOUNT }, selection, selectionArgs, null, null,
						orderBy, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	private List<PettyCash> pettyCashFetch(SQLiteDatabase db, String selection, String[] selectionArgs) throws RepositoryException {

		List<PettyCash> pettyCashList = null;

		SQLiteDatabase innerDb;
		if(null == db) {
			innerDb = mDbHelper.getReadableDatabase();
		}
		else{
			innerDb = db;
		}
		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_pettycash));
		}

		Cursor cursor = null;
		try {
			cursor = this.pettyCashGetCursor(innerDb, selection, selectionArgs);
			pettyCashList = new ArrayList<>();

			PettyCash pettyCash;
			while (!cursor.isAfterLast()) {

				pettyCash = pettyCashExtractFromCursor(cursor);

				if (null != pettyCash)
					pettyCashList.add(pettyCash);

				cursor.moveToNext();

			}
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_pettycash));

		}
		finally{
			if(null != cursor){
				cursor.close();
			}
			if(null == db) {
				innerDb.close();
			}
		}

		return pettyCashList;

	}

	//endregion

	//region Public
	@Override
	public PettyCash pettyCashSave(PettyCash pettyCash) throws RepositoryException {
		return pettyCashSave(null, pettyCash);
	}

	@Override
	public List<PettyCash> pettyCashFetchForTrip(Long tripId) throws RepositoryException{
		String criteria = String.format("%s=?", Traveloid.PettyCash.TRIP_ID);
		return pettyCashFetch(null, criteria, new String[]{tripId.toString()});
	}

	//endregion

	//endregion

	//region Traveler Methods

	//region Private
	private Traveler travelerAdd(SQLiteDatabase db, Traveler traveler) throws RepositoryException {

		String name = traveler.getName();

		ContentValues initialValues = new ContentValues();
		initialValues.put(Traveloid.Travelers.NAME, name);

		Long now = System.currentTimeMillis();
		initialValues.put(Traveloid.Travelers.CREATED_DATE, now);
		initialValues.put(Traveloid.Travelers.MODIFIED_DATE, now);

		Long id = db.insert(Traveloid.Travelers.TABLE_NAME, null, initialValues);
        if(id < 1){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_add_travler));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_add_travler));
        }

		return Traveler.create(id, name);

	}

	private Traveler travelerDelete(SQLiteDatabase db, Traveler traveler) throws RepositoryException {

		String criteria = String.format("%s=%d", Traveloid.Travelers._ID, traveler.getId());
		Boolean isDeleted = db.delete(Traveloid.Travelers.TABLE_NAME, criteria, null) > 0;

        if(!isDeleted){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_traveler));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_delete_traveler));
        }

        return null;
	}

	private Traveler travelerUpdate(SQLiteDatabase db, Traveler traveler) throws RepositoryException {

		String criteria = String.format("%s=?", Traveloid.Travelers._ID);

        Long id = traveler.getId();
        String name = traveler.getName();

		ContentValues args = new ContentValues();
		args.put(Traveloid.Travelers.NAME, traveler.getName());

		Long now = System.currentTimeMillis();
		args.put(Traveloid.Travelers.MODIFIED_DATE, now);

        Boolean isUpdated = db.update(Traveloid.Travelers.TABLE_NAME, args, criteria, new String[] {id.toString()}) > 0;

        if(!isUpdated){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_traveler));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_update_traveler));
        }
        return Traveler.create(id, name);
	}

	private Traveler travelerSave(SQLiteDatabase db, Traveler traveler) throws RepositoryException {

		SQLiteDatabase innerDb;
		if(null == db){
			innerDb = mDbHelper.getWritableDatabase();
		}
		else{
			innerDb = db;
		}

		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_save_object_to_database));
		}

		try {

			if (traveler.isDead()) {
				return travelerDelete(innerDb, traveler);
			}

			if (traveler.isNew()) {
				return travelerAdd(innerDb, traveler);
			}

			return travelerUpdate(innerDb, traveler);
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_save_object_to_database));
		}
		finally {
			if(null == db) {
				innerDb.close();
			}
		}

	}

	private Traveler travelerExtractFromCursor(Cursor cursor) {

		Traveler traveler = null;

		if (null != cursor && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {

			Long rowId = cursor.getLong(cursor
                    .getColumnIndex(Traveloid.Travelers._ID));

			String name = cursor.getString(cursor
					.getColumnIndex(Traveloid.Travelers.NAME));

			traveler = Traveler.create(rowId, name);

		}

		return traveler;

	}

	private Cursor travelerGetCursor(SQLiteDatabase db, String selection, String[] selectionArgs){

		Cursor cursor = db.query(Traveloid.Travelers.TABLE_NAME, new String[]{
						Traveloid.Travelers._ID, Traveloid.Travelers.NAME}, selection, selectionArgs,
				null, null, Traveloid.Travelers.NAME);

		if (null != cursor) {
			cursor.moveToFirst();
		}

		return cursor;

	}

	private List<Traveler> travelerFetch(SQLiteDatabase db, String selection, String[] selectionArgs) throws RepositoryException{

		List<Traveler> travelerList = null;

		SQLiteDatabase innerDb;
		if(null == db) {
			innerDb = mDbHelper.getReadableDatabase();
		}
		else{
			innerDb = db;
		}
		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_traveler));
		}

		Cursor cursor = null;
		try {
			cursor = this.travelerGetCursor(innerDb, selection, selectionArgs);
			travelerList = new ArrayList<>();

			Traveler traveler;
			while (!cursor.isAfterLast()) {

				traveler = travelerExtractFromCursor(cursor);

				if (null != traveler)
					travelerList.add(traveler);

				cursor.moveToNext();

			}
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_traveler));

		}
		finally{
			if(null != cursor){
				cursor.close();
			}
			if(null == db) {
				innerDb.close();
			}
		}

		return travelerList;
	}

    //endregion

	//region Public
//	@Override
//	public Traveler travelerFetch(Long rowId) throws RepositoryException {
//
//		SQLiteDatabase db = mDbHelper.getReadableDatabase();
//		if(null == db){
//			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
//			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_traveler));
//		}
//
//		Traveler traveler = null;
//		String criteria = String.format("%s=%d", Traveloid.Travelers._ID, rowId);
//		Cursor cursor = null;
//
//		try {
//			cursor = travelerGetCursor(db, criteria, null);
//
//			if (null != cursor && cursor.getCount() == 1) {
//				traveler = travelerExtractFromCursor(cursor);
//			}
//		} catch (Exception exception) {
//			LoggerManager.error(exception);
//
//			throw new RepositoryException(
//					mContext.getString(R.string.error_unable_to_fetch_traveler));
//
//		}
//		finally{
//			if(null != cursor){
//				cursor.close();
//			}
//
//			db.close();
//		}
//
//		return traveler;
//
//	}

	@Override
	public List<Traveler> travelerFetchAll() throws RepositoryException {
		return travelerFetch(null, null, null);
	}

	@Override
	public Traveler travelerSave(Traveler traveler) throws RepositoryException {
		return travelerSave(null, traveler);
	}

//	@Override
//	public List<Traveler> travelerFetch(String selection, String[] selectionArgs) throws RepositoryException{
//		return travelerFetch(null, selection, selectionArgs);
//	}

	@Override
	public int travelerCount() throws RepositoryException {

		try {
			List<Traveler> travelerList = travelerFetchAll();
			if (null != travelerList) {
				return travelerList.size();
			}
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_all_traveler));

		}

		return 0;

	}


	//endregion

	//endregion

	//region Trip Methods

	//region Private
	private Trip tripAdd(SQLiteDatabase db, Trip trip) throws RepositoryException {

		String destination = trip.getDestination();
		String date = trip.getDate();
		String traveler = trip.getTravelerAsString();
		String currency = trip.getCurrency();

		ContentValues initialValues = new ContentValues();
		initialValues.put(Trips.DESTINATION, destination);
		initialValues.put(Trips.DATE, date);
		initialValues.put(Trips.DEFAULT, 0);
		initialValues.put(Trips.TRAVELER, traveler);
		initialValues.put(Trips.CURRENCY, currency);

		Long now = System.currentTimeMillis();
		initialValues.put(Trips.CREATED_DATE, now);
		initialValues.put(Trips.MODIFIED_DATE, now);

		Long id = db.insert(Trips.TABLE_NAME, null, initialValues);
        if(id < 1){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_add_trip));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_add_trip));
        }

		return Trip.create(id, destination, date, false, traveler, currency);
	}

	private Trip tripDelete(SQLiteDatabase db, Trip trip) throws RepositoryException {

		String criteria = String.format("%s=%d", Trips._ID, trip.getId());
		Boolean isDeleted = db.delete(Trips.TABLE_NAME, criteria, null) > 0;

        if(!isDeleted){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_trip));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_delete_trip));
        }

        return null;
	}

	private Trip tripUpdate(SQLiteDatabase db, Trip trip) throws RepositoryException {

		String criteria = String.format("%s=?", Trips._ID);

        Long id = trip.getId();
        String destination = trip.getDestination();
        String date = trip.getDate();
        String traveler = trip.getTravelerAsString();
        String currency = trip.getCurrency();
        Boolean default_ = trip.getDefault();

		ContentValues args = new ContentValues();
		args.put(Trips.DESTINATION, trip.getDestination());
		args.put(Trips.DATE, trip.getDate());
		args.put(Trips.DEFAULT, trip.getDefault() ? 1 : 0);
		args.put(Trips.TRAVELER, trip.getTravelerAsString());
		args.put(Trips.CURRENCY, trip.getCurrency());

		Long now = System.currentTimeMillis();
		args.put(Trips.MODIFIED_DATE, now);

        Boolean isUpdated = db.update(Trips.TABLE_NAME, args, criteria, new String[]{id.toString()}) > 0;

        if(!isUpdated){
            LoggerManager.error(mContext.getString(R.string.error_unable_to_delete_trip));
            throw new RepositoryException(mContext.getString(R.string.error_unable_to_update_trip));
        }
        return Trip.create(id,destination,date, default_,traveler, currency);
	}

	private Trip tripSave(SQLiteDatabase db, Trip trip) throws RepositoryException {

		SQLiteDatabase innerDb;
		if(null == db){
			innerDb = mDbHelper.getWritableDatabase();
		}
		else{
			innerDb = db;
		}

		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_save_object_to_database));
		}

		try {
			if (trip.isDead()) {
				return tripDelete(innerDb, trip);
			}

			if (trip.isNew()) {
				return tripAdd(innerDb, trip);
			}

			return tripUpdate(innerDb, trip);
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_save_object_to_database));
		}
		finally{
			if(null == db) {
				innerDb.close();
			}
		}

	}

	private Trip tripExtractFromCursor(Cursor cursor) {

		Trip trip = null;

		if (null != cursor && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {

			Long rowId = cursor.getLong(cursor
					.getColumnIndex(Traveloid.Trips._ID));

			String destination = cursor.getString(cursor
					.getColumnIndex(Traveloid.Trips.DESTINATION));

			String date = cursor.getString(cursor
					.getColumnIndex(Traveloid.Trips.DATE));

			int default_ = cursor.getInt(cursor
					.getColumnIndex(Traveloid.Trips.DEFAULT));

			String traveler = cursor.getString(cursor
					.getColumnIndex(Traveloid.Trips.TRAVELER));

			String currency = cursor.getString(cursor
					.getColumnIndex(Traveloid.Trips.CURRENCY));

			trip = Trip.create(rowId, destination, date, default_ != 0, traveler, currency);

		}

		return trip;

	}

	private Cursor tripGetCursor(SQLiteDatabase db, String selection, String[] selectionArgs){

		String orderBy = String.format("%s DESC,%s", Trips.DATE, Trips.DESTINATION);
		Cursor cursor = db
				.query(Trips.TABLE_NAME, new String[]{Trips._ID,
								Trips.DESTINATION, Trips.DATE, Trips.DEFAULT,
								Trips.TRAVELER, Trips.CURRENCY}, selection,
						selectionArgs, null, null, orderBy);

		if (null != cursor) {
			cursor.moveToFirst();
		}

		return cursor;

	}

	private List<Trip> tripFetch(SQLiteDatabase db, String selection, String[] selectionArgs) throws RepositoryException {

		List<Trip> tripList = null;

		SQLiteDatabase innerDb;
		if(null == db) {
			innerDb = mDbHelper.getReadableDatabase();
		}
		else{
			innerDb = db;
		}
		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_trip));
		}

		Cursor cursor = null;
		try {
			cursor = this.tripGetCursor(innerDb, selection, selectionArgs);
			tripList = new ArrayList<>();

			Trip trip;
			while (!cursor.isAfterLast()) {

				trip = tripExtractFromCursor(cursor);

				if (null != trip)
					tripList.add(trip);

				cursor.moveToNext();

			}
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_trip));

		}
		finally{
			if(null != cursor){
				cursor.close();
			}
			if(null == db) {
				innerDb.close();
			}
		}

		return tripList;

	}

	private List<Trip> tripFetchAll(SQLiteDatabase db) throws RepositoryException {
		return tripFetch(db, null, null);
	}

	/**
	 *
	 * Fetch all trips. Select trip with column default equal to 1 If no trip
	 * selected, make the first trip the default one If more than one trip
	 * selected, make the first default the default one, and un default the
	 * others
	 *
	 * @return default Trip
	 * @throws RepositoryException
	 */
	private Trip tripFetchDefault(SQLiteDatabase db) throws RepositoryException {

		SQLiteDatabase innerDb;
		if(null == db){
			innerDb = mDbHelper.getWritableDatabase();
		}
		else{
			innerDb = db;
		}
		if(null == innerDb){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_default_trip));
		}

		Trip defaultTrip = null;
		List<Trip> tripList = tripFetchAll(innerDb);

		Trip firstTrip = null;
		List<Trip> notDefaultTrip = new ArrayList<>();

		try {
			innerDb.beginTransaction();
			for (Trip trip : tripList) {

				if (null == firstTrip)
					firstTrip = trip;

				if (trip.getDefault()) {

					if (null == defaultTrip) {
						// First default trip
						defaultTrip = trip;
					} else {
						// Flag as default but not first, so un default those trips
						notDefaultTrip.add(trip);
					}

				}
			}

			if (null == defaultTrip && null != firstTrip) {
				// No default trip but a trip exists
				// Set this trip to default
				firstTrip.setDefault(true);
				// Save this new default trip
				tripSave(innerDb, firstTrip);
				defaultTrip = firstTrip;
			}

			for (Trip tripToUnDefault : notDefaultTrip) {
				// Those trips do not need default flag
				// remove default flag and save trip
				tripToUnDefault.setDefault(false);
				tripSave(innerDb, tripToUnDefault);
			}
			innerDb.setTransactionSuccessful();
		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_default_trip));

		}
		finally{
			innerDb.endTransaction();
			if(null == db){
				innerDb.close();
			}
		}

		return defaultTrip;

	}

	//endregion

	//region Public
	@Override
	public Trip tripFetch(Long rowId) throws RepositoryException {

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(null == db){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_readable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_fetch_trip));
		}

		Trip trip = null;
		String criteria = String.format("%s=%d", Trips._ID, rowId);
		Cursor cursor = null;

		try {
			cursor = tripGetCursor(db, criteria, null);

			if (null != cursor && cursor.getCount() == 1) {
				trip = tripExtractFromCursor(cursor);
			}
		} catch (Exception exception) {
			LoggerManager.error(exception);

			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_fetch_trip));

		}
		finally{
			if(null != cursor){
				cursor.close();
			}

			db.close();
		}

		return trip;

	}

	@Override
	public List<Trip> tripFetchAll() throws RepositoryException {
		return tripFetch(null, null, null);
	}

	@Override
	public Trip tripFetchDefault() throws RepositoryException {
		return tripFetchDefault(null);
	}

	@Override
	public void tripChangeDefault(Trip newDefaultTrip)
			throws RepositoryException {

		if (null == newDefaultTrip)
			return;

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if(null == db){
			LoggerManager.error(mContext.getString(R.string.error_unable_to_obtain_writable_database));
			throw new RepositoryException(mContext.getString(R.string.error_unable_to_set_default_trip));
		}

		db.beginTransaction();

		try {

			Trip currentDefaultTrip = tripFetchDefault(db);
			if (null != currentDefaultTrip) {
				currentDefaultTrip.setDefault(false);
				tripSave(db, currentDefaultTrip);
			}
			newDefaultTrip.setDefault(true);

			tripSave(db, newDefaultTrip);

			db.setTransactionSuccessful();

		} catch (Exception exception) {
			LoggerManager.error(exception);
			throw new RepositoryException(
					mContext.getString(R.string.error_unable_to_set_default_trip));

		} finally {
			db.endTransaction();
			db.close();
		}

	}

	@Override
	public Trip tripSave(Trip trip) throws RepositoryException {
		return tripSave(null, trip);
	}

	//endregion

	//endregion

}