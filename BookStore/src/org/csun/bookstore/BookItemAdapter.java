package org.csun.bookstore;

import java.util.List;

import org.csun.bookstore.provider.Book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookItemAdapter extends BaseAdapter {
	private Context mContext;
	private List<Book> mBookList;
	private static LayoutInflater sInflater;
	private ItemViewHolder mViewHolder;
	
	public BookItemAdapter(Context context, List<Book> bookList) {
		mContext = context;
		mBookList = bookList;
		sInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mBookList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBookList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ItemViewHolder {
		public ImageView bookImageView;
		public TextView nameTextView;
		public TextView authorTextView;
		public TextView isbnTextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = sInflater.inflate(R.layout.book_item, null);
			mViewHolder = new ItemViewHolder();
			mViewHolder.bookImageView = (ImageView) convertView.findViewById(R.id.book_item_xml_imageview_book_cover);
			mViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.book_item_xml_textview_name);
			mViewHolder.authorTextView = (TextView) convertView.findViewById(R.id.book_item_xml_textview_author);
			mViewHolder.isbnTextView = (TextView) convertView.findViewById(R.id.book_item_xml_textview_isbn);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ItemViewHolder) convertView.getTag();
		}
		
		mViewHolder.nameTextView.setText(mBookList.get(position).getName());
		mViewHolder.authorTextView.setText(mBookList.get(position).getAuthor());
		mViewHolder.isbnTextView.setText(mBookList.get(position).getISBN());
		return convertView;
	}
}
