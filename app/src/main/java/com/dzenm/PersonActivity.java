package com.dzenm;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.crash.ActivityHelper;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ActivityHelper.getInstance().push(this);

        Button crash = findViewById(R.id.btn_crash);
        crash.setOnClickListener(v -> {
            throw new NullPointerException("");
        });

    }
}