package com.example.library.aspect;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.library.ActivityResultManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ActivityResultAspect {

    @After("execution(* android.app.Activity.onActivityResult(..))")
    public void onActivityResultMethod(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        int resultCode = (int) args[1];
        Intent data = (Intent) args[2];
        Activity activity = (Activity) joinPoint.getTarget();
        ActivityResultManager.getInstance().afterActivityResult(activity, requestCode, resultCode, data);
    }

}
