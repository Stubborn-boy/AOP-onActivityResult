package com.example.aop_onactivityresult;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.library.ActivityResultManager;
import com.example.library.ResultCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OneActivity.class);
                ActivityResultManager.getInstance()
                        .startActivityForResult(MainActivity.this, intent, 100, new ResultCallBack() {
                            @Override
                            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                                if(resultCode == Activity.RESULT_OK && data!=null){
                                    String source = data.getStringExtra("source");
                                    textView.setText(source);
                                }
                            }
                        });
            }
        });
    }
}
