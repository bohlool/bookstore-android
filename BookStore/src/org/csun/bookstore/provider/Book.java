package org.csun.bookstore.provider;

public class Book {
	private String mName;
	private String mAuthor;
	private String mISBN;
	
	public Book(String name) {
		mName = name;
		mAuthor = "Unknown Author";
		mISBN = "Unknown ISBN";
	}
	
	public Book(String name, String author) {
		mName = name;
		mAuthor = author;
		mISBN = "Unknown ISBN";
	}
	
	public Book(String name, String author, String isbn) {
		mName = name;
		mAuthor = author;
		mISBN = isbn;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		this.mAuthor = author;
	}

	public String getISBN() {
		return mISBN;
	}

	public void setISBN(String isbn) {
		this.mISBN = isbn;
	}
}
