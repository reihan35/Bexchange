package com.example.bexchange.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bexchange.R;

import java.util.List;

public class RemovableBookAdapter extends BookAdapter {
    List<Book> list;
    //tweets est la liste des models à afficher
    public RemovableBookAdapter(Context context, List<Book> books) {
        super(context, books);
        list = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("testbooks", "ici2");
        convertView = super.getView(position,convertView, parent);
        CheckBox checkbox = convertView.findViewById(R.id.check_book);
        checkbox.setVisibility(View.VISIBLE);
        Book book = getItem(position);
        checkbox.setTag (position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                list.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
            }
        });

        checkbox.setChecked(list.get(position).isChecked());
        return convertView;
    }


}