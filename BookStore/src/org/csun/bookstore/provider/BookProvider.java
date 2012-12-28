package org.csun.bookstore.provider;

import java.sql.SQLException;
import java.util.HashMap;

import org.csun.bookstore.provider.BookProviderMetadata.BookTableMetadata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BookProvider extends ContentProvider {
	private static final String DEBUG_TAG = "BookProvider";
	
	/**
	 *  Set up projection map
	 */
	private static HashMap<String, String> sBookProjectionMap;
	static {
		sBookProjectionMap = new HashMap<String, String>();
		sBookProjectionMap.put(BookTableMetadata._ID, BookTableMetadata._ID);
		sBookProjectionMap.put(BookTableMetadata.BOOK_NAME, BookTableMetadata.BOOK_NAME);
		sBookProjectionMap.put(BookTableMetadata.BOOK_AUTHOR, BookTableMetadata.BOOK_AUTHOR);
		sBookProjectionMap.put(BookTableMetadata.BOOK_ISBN, BookTableMetadata.BOOK_ISBN);
		sBookProjectionMap.put(BookTableMetadata.CREATED_DATE, BookTableMetadata.CREATED_DATE);
		sBookProjectionMap.put(BookTableMetadata.MODIFIED_DATE, BookTableMetadata.MODIFIED_DATE);
	}
	
	/**
	 * Provide a mechanism to identify all incoming Uri patterns
	 */
	private static final UriMatcher sUriMatcher;
	private static final int INDICATOR_BOOK_COLLECTION = 1;
	private static final int INDICATOR_SINGLE_BOOK = 2;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BookProviderMetadata.AUTHORITY, "books", INDICATOR_BOOK_COLLECTION);
		sUriMatcher.addURI(BookProviderMetadata.AUTHORITY, "books/#", INDICATOR_SINGLE_BOOK);
	}

	/**
	 * Setup and create database
	 * @author Chan
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, BookProviderMetadata.DATABASE_NAME, null, BookProviderMetadata.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(DEBUG_TAG, "DatabaseHelper onCreate()");
			db.execSQL("CREATE TABLE " + BookTableMetadata.TABLE_NAME 
				+ "("
				+ BookTableMetadata._ID + " INTEGER PRIMARY KEY,"
				+ BookTableMetadata.BOOK_NAME + " TEXT,"
				+ BookTableMetadata.BOOK_ISBN + " TEXT,"
				+ BookTableMetadata.BOOK_AUTHOR + " TEXT,"
				+ BookTableMetadata.CREATED_DATE + " INTEGER,"
				+ BookTableMetadata.MODIFIED_DATE + " INTEGER"
				+ ");"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(DEBUG_TAG, "DatabaseHelper onUpgrade()");
			db.execSQL("DROP TABLE IF EXISTS " + BookTableMetadata.TABLE_NAME);
			onCreate(db);
		}
		
	}
	
	private DatabaseHelper mDatabseHelper;

	@Override
	public boolean onCreate() {
		mDatabseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
			case INDICATOR_BOOK_COLLECTION:
				qb.setTables(BookTableMetadata.TABLE_NAME);
				qb.setProjectionMap(sBookProjectionMap);
				break;
				
			case INDICATOR_SINGLE_BOOK:
				qb.setTables(BookTableMetadata.TABLE_NAME);
				qb.setProjectionMap(sBookProjectionMap);
				qb.appendWhere(BookTableMetadata._ID + "=" + uri.getPathSegments().get(1));
				break;
			
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BookTableMetadata.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = mDatabseHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INDICATOR_BOOK_COLLECTION:
			return BookTableMetadata.CONTENT_TYPE;
		
		case INDICATOR_SINGLE_BOOK:
			return BookTableMetadata.CONTENT_ITEM_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != INDICATOR_BOOK_COLLECTION) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		if (values.containsKey(BookTableMetadata.CREATED_DATE) == false) {
			values.put(BookTableMetadata.CREATED_DATE, now);
		}
		
		if (values.containsKey(BookTableMetadata.MODIFIED_DATE) == false) {
			values.put(BookTableMetadata.MODIFIED_DATE, now);
		}
		
		if (values.containsKey(BookTableMetadata.BOOK_NAME) == false) {
			try {
				throw new SQLException("Failed to insert row because BOOK_NAME is required " + uri);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (values.containsKey(BookTableMetadata.BOOK_ISBN) == false) {
			values.put(BookTableMetadata.BOOK_ISBN, "Unknown ISBN");
		}
		
		if (values.containsKey(BookTableMetadata.BOOK_AUTHOR) == false) {
			values.put(BookTableMetadata.BOOK_AUTHOR, "Unknown Author");
		}
		
		SQLiteDatabase db = mDatabseHelper.getWritableDatabase();
		long rowId = db.insert(BookTableMetadata.TABLE_NAME, BookTableMetadata.BOOK_NAME, values);
		if (rowId > 0) {
			Uri insertedBookUri = ContentUris.withAppendedId(BookTableMetadata.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertedBookUri, null);
			return insertedBookUri;
		} else {
			try {
				throw new SQLException("Failed to insert row into " + uri);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDatabseHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
			case INDICATOR_BOOK_COLLECTION:
				count = db.delete(BookTableMetadata.TABLE_NAME, where, whereArgs);
				break;
				
			case INDICATOR_SINGLE_BOOK:
				String rowId = uri.getPathSegments().get(1);
				count = db.delete(BookTableMetadata.TABLE_NAME, BookTableMetadata._ID + "=" + rowId + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mDatabseHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
			case INDICATOR_BOOK_COLLECTION:
				count = db.update(BookTableMetadata.TABLE_NAME, values, where, whereArgs);
				break;
				
			case INDICATOR_SINGLE_BOOK:
				String rowId = uri.getPathSegments().get(1);
				count = db.update(BookTableMetadata.TABLE_NAME, values, BookTableMetadata._ID + "=" + rowId + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	
}
