package com.example.aop_onactivityresult;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content,new OneFragment())
                .commit();
    }

}
