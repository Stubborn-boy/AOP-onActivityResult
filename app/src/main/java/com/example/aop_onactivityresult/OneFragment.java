package com.example.aop_onactivityresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.library.ActivityResultManager;
import com.example.library.ResultCallBack;

public class OneFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_one, null, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView textView = view.findViewById(R.id.textView);
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TwoActivity.class);
                ActivityResultManager.getInstance()
                        .startActivityForResult(getActivity(), intent, 200, new ResultCallBack() {
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
        Intent data = new Intent();
        data.putExtra("source", "来自OneFragment");
        getActivity().setResult(Activity.RESULT_OK, data);
    }
}
