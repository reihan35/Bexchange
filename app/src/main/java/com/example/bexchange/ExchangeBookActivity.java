package com.example.bexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Iterator;

import static com.example.bexchange.Util.FirebaseUtil.addBookUserJSON;
import static com.example.bexchange.Util.JSONUtil.toMap;
import static com.example.bexchange.Util.TextViewUtil.makeTextViewResizable;

public class ExchangeBookActivity extends AppCompatActivity {
    private JSONObject book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_book);
        final String title = getIntent().getStringExtra("title");
        String resume = getIntent().getStringExtra("resume");
        String image = getIntent().getStringExtra("imageLink");
        TextView titleView = findViewById(R.id.bookTitle2);
        ImageView iv = findViewById(R.id.bookImage2);

        titleView.setText(title);
        TextView resumeView = findViewById(R.id.bookResume2);
        resumeView.setText(resume);
        Button bt = findViewById(R.id.exchange_book);
        RequestOptions options = new RequestOptions()
                .centerInside()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(ExchangeBookActivity.this).load(image).apply(options).into(iv);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExchangeBookActivity.this,ListOfMyBooksActivityExchange.class);
                intent.putExtra("title_get",title);
                startActivity(intent);
            }
        });


        //Glide.with(this.getApplicationContext()).load(image).apply(options).into(viewHolder.imgBook);

        /*resumeView.setText(desc);
        imgBook.setImageBitmap(img);*/


    }
}
