package com.example.bexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bexchange.R;
import com.example.bexchange.NotABookException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import static com.example.bexchange.Util.JSONUtil.toMap;
import static com.example.bexchange.Util.TextViewUtil.makeTextViewResizable;

public class SubmitBookActivity extends AppCompatActivity {
    private JSONObject book;

    private static class UrlLoadingTask extends AsyncTask<URL, Void, Bitmap> {
        private final ImageView updateView;
        private boolean isCancelled = false;
        private InputStream urlInputStream;

        private UrlLoadingTask(ImageView updateView) {
            this.updateView = updateView;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            try {
                URLConnection con = params[0].openConnection();
                // can use some more params, i.e. caching directory etc
                con.setUseCaches(true);
                this.urlInputStream = con.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);
            } catch (IOException e) {
                Log.w(SubmitBookActivity.class.getName(), "failed to load image from " + params[0], e);
                return null;
            } finally {
                if (this.urlInputStream != null) {
                    try {
                        this.urlInputStream.close();
                    } catch (IOException e) {
                        ; // swallow
                    } finally {
                        this.urlInputStream = null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (!this.isCancelled) {
                // hope that call is thread-safe
                this.updateView.setImageBitmap(result);
            }
        }

        /*
         * just remember that we were cancelled, no synchronization necessary
         */
        @Override
        protected void onCancelled() {
            this.isCancelled = true;
            try {
                if (this.urlInputStream != null) {
                    try {
                        this.urlInputStream.close();
                    } catch (IOException e) {
                        ;// swallow
                    } finally {
                        this.urlInputStream = null;
                    }
                }
            } finally {
                super.onCancelled();
            }
        }
    }

    public void setImageBook(Bitmap result) {
        ImageView imgBook = findViewById(R.id.bookImage);
        Drawable d = new BitmapDrawable(getResources(), result);
        imgBook.setImageDrawable(d);
    }

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
        new UrlLoadingTask(imgBook).execute(new URL(urlImage));
        //  Log.d("s","??????????????" + LoadImageFromWebOperations(urlImage).getIntrinsicWidth());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);
        Intent intent = getIntent();
        intent.getStringExtra("book");
        book = null;
        try {
            book = new JSONObject(intent.getStringExtra("book"));
            if (book.getInt("totalItems") == 0) {
                Intent intent2 = new Intent(SubmitBookActivity.this,FillFormBookV2.class);
                startActivity(intent2);
            } else {
                book = book.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON", book.toString());
        try {
            fillBookInfo(book);
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
