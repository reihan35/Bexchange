package com.example.bexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bexchange.R;
import com.example.bexchange.NotABookException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class SubmitBookActivity extends AppCompatActivity {


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("MOI JE SUIS ICI");
            e.printStackTrace();
            return null;
        }
    }

    private void fillBookInfo(JSONObject book) throws JSONException, NotABookException {
        if(book.getInt("totalItems") == 0){
            throw new NotABookException();
        }
        else{
            // ISBN is a primary key, no references to different books
            JSONObject bookInfo = book.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
            for (Iterator<String> it = bookInfo.keys(); it.hasNext(); ) {
                String s = it.next();
                Log.d("JSON", s);

            }
            String urlImage = bookInfo.getJSONObject("imageLinks").getString("smallThumbnail");
            String title = bookInfo.getString("title");
            String resume = bookInfo.getString("description");
            TextView titleView = findViewById(R.id.bookTitle);
            TextView resumeView = findViewById(R.id.bookResume);
            ImageView imgBook = findViewById(R.id.bookImage);
          //  Log.d("s","??????????????" + LoadImageFromWebOperations(urlImage).getIntrinsicWidth());
            imgBook.setImageDrawable(LoadImageFromWebOperations(urlImage));
            titleView.setText(title);
            resumeView.setText(resume);
            Log.d("JSON", book.toString());
            Log.d("JSON", bookInfo.toString());
            Log.d("JSON", urlImage.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);
        Intent intent = getIntent();
        intent.getStringExtra("book");
        JSONObject book = null;
        try {
            book = new JSONObject(intent.getStringExtra("book"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON", book.toString());
        try {
            fillBookInfo(book);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NotABookException e) {
            e.printStackTrace();
        }
    }
}
