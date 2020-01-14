package com.example.bexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class SubmitBookActivity extends AppCompatActivity {
    private JSONObject book;





    private void fillBookInfo(JSONObject bookInfo) throws JSONException, MalformedURLException {

        // ISBN is a primary key, no references to different books
        for (Iterator<String> it = bookInfo.keys(); it.hasNext(); ) {
            String s = it.next();
            Log.d("JSON", s);

        }

        String urlImage = bookInfo.getJSONObject("imageLinks").getString("thumbnail");
        String title = bookInfo.getString("title");
        String resume = bookInfo.getString("description");
        TextView titleView = findViewById(R.id.bookTitle);
        TextView resumeView = findViewById(R.id.bookResume);
        ImageView imgBook = findViewById(R.id.bookImage);
        Log.d("JSON", "avant " +  urlImage);
        urlImage = urlImage.replaceAll("&zoom=[0-9]+&", "&zoom=2&");
        Log.d("JSON", "apres " + urlImage);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this).load(urlImage).apply(options).into(imgBook);
        //new UrlLoadingTask(imgBook).execute(new URL(urlImage));
        titleView.setText(title);
        resumeView.setText(resume);
        makeTextViewResizable(resumeView, 3, "Voir Plus", true);
        Log.d("JSON", book.toString());
        Log.d("JSON", bookInfo.toString());
        Log.d("JSON", urlImage.toString());
    }

    public void addToDatabase(View view) throws JSONException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("firebase", "J'ajoute un document");
        db.collection("books")
                .document(book.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier"))
                .set(toMap(book))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Le document a été ajouté", Toast.LENGTH_LONG).show();
                        Log.d("success", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Le document n'a pas pu être ajouté", Toast.LENGTH_LONG).show();
                        Log.w("failure", "Error writing document", e);
                    }
                });
    }


    private static final int FILLED_BOOK_REQUEST = 1990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);
        Intent intent = getIntent();
        intent.getStringExtra("book");

        book = null;
        try {
            book = new JSONObject(intent.getStringExtra("book"));

            /*
            Intent intent3 = new Intent(SubmitBookActivity.this,FillFormBook.class);
            intent3.putExtra("isbn", getIntent().getStringExtra("isbn"));
            startActivityForResult(intent3, FILLED_BOOK_REQUEST);
            return;
            */
            String isbn = getIntent().getStringExtra("isbn");
            Toast.makeText(getApplicationContext(), "" + book.getInt("totalItems"), Toast.LENGTH_LONG).show();
            if (book.getInt("totalItems") == 0) {
                Intent intent2 = new Intent(SubmitBookActivity.this,FillFormBook.class);
                intent2.putExtra("isbn", isbn );
                startActivityForResult(intent2, FILLED_BOOK_REQUEST);
            } else {
                book = book.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
                Log.d("JSON", book.toString());
                try {
                    fillBookInfo(book);
                } catch (MalformedURLException e) {
                    Toast.makeText(getApplicationContext(), "URLException", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                addBookUserJSON(book, isbn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fillBookInfoAfterRequest(Intent data){
        Bitmap img = (Bitmap) data.getParcelableExtra("img");
        String title = data.getStringExtra("title");
        String author = data.getStringExtra("author");
        String desc = data.getStringExtra("desc");
        TextView titleView = findViewById(R.id.bookTitle);
        TextView resumeView = findViewById(R.id.bookResume);
        ImageView imgBook = findViewById(R.id.bookImage);
        titleView.setText(title);
        resumeView.setText(desc);
        imgBook.setImageBitmap(img);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == FILLED_BOOK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                fillBookInfoAfterRequest(data);

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
