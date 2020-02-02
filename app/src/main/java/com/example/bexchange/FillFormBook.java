package com.example.bexchange;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.bexchange.Util.FirebaseUtil.addBookUserJSON;
import static com.example.bexchange.Util.JSONUtil.toMap;

public class FillFormBook extends Activity
{
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESUME_REQUEST = 1889;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("test", "im here");
        setContentView(R.layout.activity_fill_form_book);
        /*
        this.imageView = (ImageView)this.findViewById(R.id.bookImage);
        Button photoButton = (Button) this.findViewById(R.id.buttonPhoto);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Log.d("test", "im here3");
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });*/
    }

    public void scanResume(View view){
        Intent intent = new Intent(FillFormBook.this,FillFormBookV2.class);
        startActivityForResult(intent, RESUME_REQUEST);
    }

    public void takePicture(View view){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    */



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RESUME_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                EditText resume = findViewById(R.id.descriptionEdit);
                resume.setText(data.getData().toString());
            }
        }


        ImageView getimage = (ImageView)findViewById(R.id.book_picture);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap getphoto = (Bitmap) data.getExtras().get("data");
            getimage.setImageBitmap(getphoto);
            getimage.setVisibility(View.VISIBLE);
        }
    }


    private class UploadImg extends AsyncTask<String, Void, Void> {

        Bitmap bitmap;
        String isbn;

        UploadImg(Bitmap bitmap, String isbn) {
            // list all the parameters like in normal class define
            this.bitmap = bitmap;
            this.isbn = isbn;
        }

        @Override
        protected Void doInBackground(String... strings) {
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();


            StorageReference imgIsbnRef = storageRef.child("images/" +
                    isbn + ".jpg");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imgIsbnRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

            final StorageReference ref = storageRef.child("images/ " + isbn  + ".jpg");
            uploadTask = ref.putBytes(data);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        saveUrlImage(downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
            return null;
        }
    }

    //returns URL of the added image
    public void saveToFirebase(Bitmap bitmap, String isbn) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


        StorageReference imgIsbnRef = storageRef.child("images/" +
                        isbn + ".jpg");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgIsbnRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        final StorageReference ref = storageRef.child("images/ " + isbn  + ".jpg");
        uploadTask = ref.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveUrlImage(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
                synchronized (processed){
                    processed.notify();
                }
            }
        });

    }

    Uri uriImg = null;
    private void saveUrlImage(Uri uriImg){
        this.uriImg = uriImg;
    }

    public JSONObject bookFromInfos(String urlImage, String title, String desc, String author) throws JSONException {
        JSONObject book = new JSONObject();
        JSONObject img = new JSONObject();
        img.put("thumbnail", urlImage);
        book.put("imageLinks", img);
        book.put("title", title);
        book.put("description", desc);
        book.put("authors", author);
        return  book;
    }

    Bitmap bitmap = null;
    private AtomicBoolean processed = new AtomicBoolean(true) ;
    private class AddToDatabaseTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... infos) {
            try {
                String isbn = getIntent().getStringExtra("isbn");
                synchronized (processed) {
                    saveToFirebase(bitmap, isbn);
                    processed.wait();
                }
                Log.d("doc written", uriImg.toString());
                JSONObject book = null;
                book = bookFromInfos(uriImg.toString(), infos[0], infos[1], infos[2]);

                addBookUserJSON(book, isbn);
                Log.d("doc w", book.toString());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d("firebase", "J'ajoute un document");
                db.collection("books")
                        .document(isbn)
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
            }catch(JSONException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    public void addToDatabase(View v) throws JSONException {

        ImageView getimage = (ImageView)findViewById(R.id.book_picture);
        bitmap = ((BitmapDrawable)getimage.getDrawable()).getBitmap();

        EditText titleEdit = findViewById(R.id.titleEditText);
        EditText authorEdit = findViewById(R.id.authorEditText);
        EditText descEdit = findViewById(R.id.descriptionEdit);



        String title = titleEdit.getText().toString();
        String author = authorEdit.getText().toString();
        String desc = descEdit.getText().toString();

        boolean all_filled = true;
        if(TextUtils.isEmpty(title)) {
            titleEdit.setError("Ce champ doit être rempli");
            all_filled = false;
        }
        if(TextUtils.isEmpty(author)) {
            authorEdit.setError("Ce champ doit être rempli");
            all_filled = false;
        }
        if(TextUtils.isEmpty(desc)) {
            descEdit.setError("Ce champ doit être rempli");
            all_filled = false;
        }
        if(!all_filled){
            return;
        }



        new AddToDatabaseTask().execute(title, desc, author);

        Intent data = new Intent();
        data.putExtra("img", bitmap);
        data.putExtra("title", title);
        data.putExtra("desc", desc);
        data.putExtra("author", author);

        setResult(RESULT_OK, data);
        finish();
        /*
        String isbn = getIntent().getStringExtra("isbn");
        saveToFirebase(bitmap, isbn);
        Log.d("doc written", uriImg.toString());
        JSONObject book = null;
        book = bookFromInfos(uriImg.toString(), title.getText().toString(), desc.getText().toString(), author.getText().toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("firebase", "J'ajoute un document");
        db.collection("books")
                .document(isbn)
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
                });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
        finish();
    }


}
