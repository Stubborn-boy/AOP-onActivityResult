package com.example.aop_onactivityresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        Intent data = new Intent();
        data.putExtra("source", "来自TwoActivity");
        setResult(Activity.RESULT_OK, data);
    }

}
