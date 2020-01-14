package com.example.bexchange.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.bexchange.Util.JSONUtil.toMap;

public class FirebaseUtil {

    static public JSONObject reduce_book(JSONObject book) throws JSONException {
        String urlImage = book.getJSONObject("imageLinks").getString("thumbnail");
        String title = book.getString("title");
        String author = book.getString("authors");
        book = new JSONObject();
        JSONObject img = new JSONObject();
        img.put("thumbnail", urlImage);
        book.put("imageLinks", img);
        book.put("title", title);
        book.put("authors", author);
        return book;
    }

    static public void addBookUserJSON(JSONObject book, String isbn) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("users")
                    .document(user.getEmail())
                    .collection("books")
                    .document(isbn)
                    .set(toMap(reduce_book(book)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static public void getAllBooks(String user, final OnBooksReceived callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user)
                .collection("books")
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Book> res = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        try {
                            JSONObject data = new JSONObject(document.getData());
                            String isbn = document.getId();
                            String title = data.getString("title");

                            String authors = data.getString("authors");
                            String imgLink = data.getJSONObject("imageLinks").getString("thumbnail");
                            String desc = "";
                            res.add(new Book(isbn,title,authors,desc, imgLink));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onBooksReceived(res);
                    Log.d("TEST", res.toString());
                } else {
                    Log.d("TEST", "Error getting documents: ", task.getException());
                }
            }
        });
       // return res;
    }

    public interface OnBooksReceived {
        void onBooksReceived(List<Book> books);
    }
}
