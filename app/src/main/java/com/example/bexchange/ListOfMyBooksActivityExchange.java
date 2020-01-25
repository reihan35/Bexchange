package com.example.bexchange;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bexchange.Util.Book;
import com.example.bexchange.Util.FirebaseUtil;
import com.example.bexchange.Util.RemovableBookAdapter;
import com.example.bexchange.Util.RemovableBookAdapterExchange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.bexchange.Util.FirebaseUtil.getAllBooks;

public class ListOfMyBooksActivityExchange extends AppCompatActivity {

    private ListView mListView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<Book> lBooks = new ArrayList<>();
    String title_book_get = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testbooks", "ici");
        setContentView(R.layout.activity_list_of_my_books_ex);
        mListView = (ListView) findViewById(R.id.list_of_my_books2);
        String user = getIntent().getStringExtra("user");
        String userAuth = auth.getCurrentUser().getEmail();
        handleBooks(userAuth);
    }

    public void set_title_book(String t){
        title_book_get = t;
    }

    private void handleBooks(final String usr){

        getAllBooks(usr, new FirebaseUtil.OnBooksReceived() {
            @Override
            public void onBooksReceived(List<Book> books) {
                lBooks = books;
                RemovableBookAdapterExchange adapter = new RemovableBookAdapterExchange(ListOfMyBooksActivityExchange.this, books,usr,getIntent().getStringExtra("title_get"));
                mListView.setAdapter(adapter);
            }
        });
    }

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public void removeBooks(View v){
        int i = 0;
        for (Book b:lBooks){
            if(b.isChecked()){
                firebaseFirestore.collection("users")
                        .document(auth.getCurrentUser().getEmail())
                        .collection("books")
                        .document(b.getIsbn())
                        .delete();
            }
            i++;
        }
        Toast.makeText(getApplicationContext(), i + " documents ont été supprimés" , Toast.LENGTH_SHORT).show();
        finish();
    }



}
