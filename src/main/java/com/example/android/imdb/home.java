package com.example.android.imdb;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class home extends AppCompatActivity {
    private static int splash=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home=new Intent(home.this,MainActivity.class);
                startActivity(home);
                finish();

            }
        },splash);
    }
}
