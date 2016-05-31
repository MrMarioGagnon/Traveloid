/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gagnon.mario.mr.traveloid;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for TraveloidProvider
 */
public final class Traveloid {

	/**
	 * Categories table
	 */
	public static final class Categories implements BaseColumns {

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/categories");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mg.grandgalot.category";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mg.grandgalot.category";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The table name
		 */
		public static final String TABLE_NAME = "Categories";

		public static final String NAME = "name";

		public static final String SUB_CATEGORY = "subCategory";

		public static final String CREATED_DATE = "created";

		public static final String MODIFIED_DATE = "modified";

		// This class cannot be instantiated
		private Categories() {
		}
	}

	/**
	 * Expenses table
	 */
	public static final class Expenses implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/expenses");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mg.grandgalot.expense";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mg.grandgalot.expense";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The table name
		 */
		public static final String TABLE_NAME = "Expenses";

		public static final String TRIP_ID = "tripId";

		public static final String DATE = "date";

		public static final String CATEGORY = "category";

		public static final String AMOUNT = "amount";

		public static final String PAYMENT_METHOD = "paymentMethod";

		public static final String REFERENCE_NO = "referenceNo";

		public static final String DESCRIPTION = "description";

		public static final String TRAVELER = "traveler";
		
		public static final String PAYOR = "payor";		

		public static final String CURRENCY = "currency";
		
		public static final String EXCHANGE_RATE = "exchangeRate";

		public static final String CREATED_DATE = "created";

		public static final String MODIFIED_DATE = "modified";

		// This class cannot be instantiated
		private Expenses() {
		}
	}

	/**
	 * Petty Cash table
	 */
	public static final class PettyCash implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
//		public static final Uri CONTENT_URI = Uri.parse("content://"
//				+ AUTHORITY + "/expenses");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
//		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mg.grandgalot.expense";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
//		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mg.grandgalot.expense";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The table name
		 */
		public static final String TABLE_NAME = "PettyCash";

		public static final String TRIP_ID = "tripId";

		public static final String NAME = "name";

		public static final String DESCRIPTION = "description";

		public static final String CURRENCY = "currency";

		public static final String EXCHANGE_RATE = "exchangeRate";

		public static final String AMOUNT = "amount";

		public static final String CREATED_DATE = "created";

		public static final String MODIFIED_DATE = "modified";

		// This class cannot be instantiated
		private PettyCash() {
		}
	}

	/**
	 * Travelers table
	 */
	public static final class Travelers implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/travelers");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mg.grandgalot.traveler";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mg.grandgalot.traveler";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The table name
		 */
		public static final String TABLE_NAME = "Travelers";

		public static final String NAME = "name";

		/**
		 * The timestamp for when the trip was created
		 * <P>
		 * Type: INTEGER (long from System.curentTimeMillis())
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the trip was last modified
		 * <P>
		 * Type: INTEGER (long from System.curentTimeMillis())
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		// This class cannot be instantiated
		private Travelers() {
		}
	}

	/**
	 * Trips table
	 */
	public static final class Trips implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/trips");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mg.grandgalot.trip";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mg.grandgalot.trip";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The table name
		 */
		public static final String TABLE_NAME = "Trips";

		/**
		 * The destination of the trip
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DESTINATION = "destination";

		/**
		 * The date of the trip
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DATE = "date";

		public static final String DEFAULT = "default_";

		public static final String TRAVELER = "traveler";

		public static final String CURRENCY = "currency";

		/**
		 * The timestamp for when the trip was created
		 * <P>
		 * Type: INTEGER (long from System.curentTimeMillis())
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the trip was last modified
		 * <P>
		 * Type: INTEGER (long from System.curentTimeMillis())
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		// This class cannot be instantiated
		private Trips() {
		}
	}

	public static final String DATABASE_NAME = "traveloid.db";

	public static final String AUTHORITY = "com.gagnon.mario.mr.provider.Traveloid";

	public static final int DATABASE_VERSION = 4;

	// This class cannot be instantiated
	private Traveloid() {
	}

}
