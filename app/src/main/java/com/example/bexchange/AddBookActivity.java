package com.example.bexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bexchange.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;


import java.io.IOException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }

    public void scanBarCode(View v){
        Intent intent = new Intent(this,ScanBookActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==0){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                TextView tvId = (TextView) findViewById(R.id.barcode_result);

                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    Log.d("b","JE COMPRREEEEENNNNNDSSSS 3 " + barcode);
                    Log.d("b","JE COMPRREEEEENNNNNDSSSS 2 " + barcode.displayValue);


                    tvId.setText("Barcode Value : " + barcode.displayValue);
                }
                else{
                    tvId.setText("No barcode FOUND");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
