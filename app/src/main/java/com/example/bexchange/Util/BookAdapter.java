package com.example.bexchange.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bexchange.R;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    //tweets est la liste des models à afficher
    public BookAdapter(Context context, List<Book> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.small_book,parent, false);
        }

        Log.d("testbooks", "ici3");

        BookViewHolder viewHolder = (BookViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BookViewHolder();
            viewHolder.author = (TextView) convertView.findViewById(R.id.author_small_book);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title_small_book);
            viewHolder.imgBook = (ImageView) convertView.findViewById(R.id.img_small_book);
            convertView.setTag(viewHolder);
        }

        Book book = getItem(position);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);


        Glide.with(getContext()).load(book.getImgLink()).apply(options).into(viewHolder.imgBook);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.author.setText(book.getAuthor());
        viewHolder.title.setText(book.getTitle());
        return convertView;
    }

    private class BookViewHolder{
        public TextView author;
        public TextView title;
        public ImageView imgBook;
    }
}