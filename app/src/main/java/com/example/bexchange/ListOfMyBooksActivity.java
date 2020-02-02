package com.example.bexchange;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bexchange.Util.Book;
import com.example.bexchange.Util.BookAdapter;
import com.example.bexchange.Util.FirebaseUtil;
import com.example.bexchange.Util.RemovableBookAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.bexchange.Util.FirebaseUtil.getAllBooks;

public class ListOfMyBooksActivity extends AppCompatActivity {

    private ListView mListView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<Book> lBooks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testbooks", "ici");
        setContentView(R.layout.activity_list_of_my_books);
        mListView = (ListView) findViewById(R.id.list_of_my_books);
        String user = getIntent().getStringExtra("user");
        String userAuth = auth.getCurrentUser().getEmail();
        handleBooks(userAuth);



    }


    private void handleBooks(final String usr){
        getAllBooks(usr, new FirebaseUtil.OnBooksReceived() {
            @Override
            public void onBooksReceived(List<Book> books) {
                lBooks = books;
                if (books.size()==0){
                    Toast.makeText(getApplicationContext(), "No books yet !", Toast.LENGTH_LONG).show();
                    findViewById(R.id.deleteButton).setVisibility(View.GONE);
                }

                else {
                    RemovableBookAdapter adapter = new RemovableBookAdapter(ListOfMyBooksActivity.this, books,usr);
                    mListView.setAdapter(adapter);
                }
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
        Toast.makeText(getApplicationContext(), i + " book deleted " , Toast.LENGTH_SHORT).show();
        finish();
    }



}
