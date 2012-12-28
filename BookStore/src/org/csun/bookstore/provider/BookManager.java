package org.csun.bookstore.provider;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class BookManager {
	private static final String TAG = "BookManager";
	private Context mContext;
	
	public BookManager(Context context) {
		mContext = context;
		while (getCount() > 0) {
			removeBook();
		}
	}
	
	public void addBook(Book book) {
		ContentValues values = new ContentValues();
		values.put(BookProviderMetadata.BookTableMetadata.BOOK_NAME, book.getName());
		values.put(BookProviderMetadata.BookTableMetadata.BOOK_AUTHOR, book.getAuthor());
		values.put(BookProviderMetadata.BookTableMetadata.BOOK_ISBN, book.getISBN());
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = BookProviderMetadata.BookTableMetadata.CONTENT_URI;
		resolver.insert(uri, values);
	}
	
	public List<Book> retrieveBooks() {
		List<Book> bookList = new ArrayList<Book>();
		Uri uri = BookProviderMetadata.BookTableMetadata.CONTENT_URI;
		Activity activity = (Activity) mContext;
		Cursor c = activity.managedQuery(uri, null, null, null, null);
		int nameIndex = c.getColumnIndex(BookProviderMetadata.BookTableMetadata.BOOK_NAME);
		int isbnIndex = c.getColumnIndex(BookProviderMetadata.BookTableMetadata.BOOK_ISBN);
		int authorIndex = c.getColumnIndex(BookProviderMetadata.BookTableMetadata.BOOK_AUTHOR);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			String id = c.getString(1);
			String name = c.getString(nameIndex);
			String isbn = c.getString(isbnIndex);
			String author = c.getString(authorIndex);
			bookList.add(new Book(name, author, isbn));
		}
		
		Log.v(TAG, Integer.toString(bookList.size()));
		
		c.close();
		return bookList;
	}
	
	public void removeBook() {
		int i = getCount();
		ContentResolver resolver = this.mContext.getContentResolver();
		Uri uri = BookProviderMetadata.BookTableMetadata.CONTENT_URI;
		Uri delUri = Uri.withAppendedPath(uri, Integer.toString(i));
		resolver.delete(delUri, null, null);
	}
	
	public int getCount() {
		Uri uri = BookProviderMetadata.BookTableMetadata.CONTENT_URI;
		Activity a = (Activity)this.mContext;
		Cursor c = a.managedQuery(uri, 
				null, //projection
				null, //selection string
				null, //selection args array of strings
				null); //sort order
		int numberOfRecords = c.getCount();
		c.close();
		return numberOfRecords;
	}
}
