package com.example.library;

import android.content.Intent;
import android.support.annotation.Nullable;

public interface ResultCallBack {
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
