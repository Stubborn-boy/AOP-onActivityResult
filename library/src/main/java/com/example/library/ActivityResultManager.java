package com.example.library;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class ActivityResultManager {
    private ActivityResultManager(){}
    private static ActivityResultManager instance;
    public static ActivityResultManager getInstance(){
        if(instance==null){
            synchronized (ActivityResultManager.class){
                if(instance==null){
                    instance = new ActivityResultManager();
                }
            }
        }
        return instance;
    }

    private HashMap<String, HashMap<Integer,ResultCallBack>> mMap;

    public void startActivityForResult(Activity context, Intent intent, int requestCode, ResultCallBack callBack){
        if(context==null||intent==null) return;
        if(callBack!=null){
            if(mMap==null) mMap=new HashMap<>();
            HashMap<Integer,ResultCallBack> resultMap = mMap.get(context.getClass().getName());
            if (resultMap == null) {
                resultMap = new HashMap<>();
                mMap.put(context.getClass().getName(),resultMap);
            }
            resultMap.put(requestCode, callBack);
        }
        context.startActivityForResult(intent, requestCode);
    }

    public void afterActivityResult(Activity context, int requestCode, int resultCode, Intent data){
        if(mMap!=null) {
            HashMap<Integer, ResultCallBack> resultMap = mMap.get(context.getClass().getName());
            if (resultMap != null) {
                ResultCallBack callBack = resultMap.get(requestCode);
                if (callBack != null) {
                    callBack.onActivityResult(requestCode, resultCode, data);
                    resultMap.remove(requestCode);
                    if(resultMap.size()==0){
                        mMap.remove(context.getClass().getName());
                        if(mMap.size()==0){
                            mMap=null;
                        }
                    }
                }
            }
        }
    }

}
