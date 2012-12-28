package org.csun.bookstore;

import java.util.ArrayList;
import java.util.List;

import org.csun.bookstore.provider.Book;
import org.csun.bookstore.provider.BookManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class BookShelfActivity extends Activity {
	private static final String TAG = "BookShelfActivity";
	private BookItemAdapter mAdapter = null;
	private List<Book> mBookList;
	private BookManager mBookManager;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_shelf);
		mBookManager = new BookManager(this);
		addDummyBooks(mBookManager);
		mBookList = mBookManager.retrieveBooks();
		mAdapter = new BookItemAdapter(getApplicationContext(), mBookList);
		mListView = (ListView) findViewById(R.id.activity_book_shelf_xml_listview_book_list);
		mListView.setAdapter(mAdapter);
	}
	
	private void addDummyBooks(BookManager bm) {
		bm.addBook(
			new Book(
				"Professional Android Application Development", 
				"Reto Meier", 
				"978-1118102275"));
		bm.addBook(
			new Book(
				"Programming Android: Java Programming for the New Generation of Mobile Devicest", 
				"Zigurd Mednieks", 
				"978-1449316648"));
	}
}
