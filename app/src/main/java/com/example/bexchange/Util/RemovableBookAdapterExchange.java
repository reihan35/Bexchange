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

import java.util.List;

public class RemovableBookAdapterExchange extends BookAdapter {
    List<Book> list;
    Context c = this.getContext();
    //tweets est la liste des models à afficher
    public RemovableBookAdapterExchange(Context context, List<Book> books) {
        super(context, books);
        list = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("testbooks", "ici2");
        convertView = super.getView(position,convertView, parent);
        CheckBox checkbox = convertView.findViewById(R.id.check_book);
        checkbox.setVisibility(View.VISIBLE);
        final Book book = getItem(position);
        checkbox.setTag (position);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                list.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(c, DashboardActivity2.class);
                Toast.makeText(c, "An email has been sent to ... ", Toast.LENGTH_LONG).show();
                c.startActivity(intent);
            }
        });
        checkbox.setChecked(list.get(position).isChecked());
        return convertView;
    }


}