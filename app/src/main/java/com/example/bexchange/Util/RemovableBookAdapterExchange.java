package com.example.bexchange.Util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.bexchange.DashboardActivity2;
import com.example.bexchange.ExchangeBookActivity;
import com.example.bexchange.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RemovableBookAdapterExchange extends BookAdapter {
    List<Book> list;
    Context c = this.getContext();
    String  u = "";
    String title_book_get = "";

    //tweets est la liste des models à afficher
    public RemovableBookAdapterExchange(Context context, List<Book> books,String user,String title_book) {
        super(context, books);
        list = books;
        u = user;
        title_book_get = title_book;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("testbooks", "ici2");
        convertView = super.getView(position,convertView, parent);
       /*CheckBox checkbox = convertView.findViewById(R.id.check_book);
        checkbox.setVisibility(View.VISIBLE);*/
        final Book book = getItem(position);
        //checkbox.setTag (position);

       // checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            /*@Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                list.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
            }
        });*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(c, DashboardActivity2.class);
                Intent i = new Intent(Intent.ACTION_SEND);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{u});
                i.putExtra(Intent.EXTRA_SUBJECT, "Ask exchange for " + title_book_get);
                i.putExtra(Intent.EXTRA_TEXT   , "Hi ! I would like to exchange my book \"" + book.getTitle() + "\" with your book \"" + title_book_get + "\" What do you think about it ? \n");
                try {
                    c.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(c, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                //c.startActivity(intent);
            }
        });
        //checkbox.setChecked(list.get(position).isChecked());
        return convertView;
    }


}