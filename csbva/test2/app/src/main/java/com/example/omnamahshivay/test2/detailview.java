package com.example.omnamahshivay.test2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class detailview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        Intent intent = getIntent();
        //String id = intent.getStringExtra("id");

        //String name = intent.getStringExtra("name");
    }
}
