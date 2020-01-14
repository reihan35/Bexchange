package com.example.bexchange;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bexchange.Util.Book;
import com.example.bexchange.Util.BookAdapter;
import com.example.bexchange.Util.FirebaseUtil;

import java.util.List;

import static com.example.bexchange.Util.FirebaseUtil.getAllBooks;

public class ListOfBookActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_book);
        mListView = (ListView) findViewById(R.id.list_of_books);
        String user = getIntent().getStringExtra("user");
        initBooksForUser(user);


    }

    private void initBooksForUser(String usr){

         getAllBooks(usr, new FirebaseUtil.OnBooksReceived() {
            @Override
            public void onBooksReceived(List<Book> books) {

                for (Book b: books) {
                    Log.d("TEST", books.toString());
                }
                BookAdapter adapter = new BookAdapter(ListOfBookActivity.this, books);
                mListView.setAdapter(adapter);
            }
        });
    }

}
