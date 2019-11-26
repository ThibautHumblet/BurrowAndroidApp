package com.example.mobdev_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LoadingActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        auth = FirebaseAuth.getInstance();

        Intent i;

        if (auth.getCurrentUser() != null) {
            i = new Intent(LoadingActivity.this, MainActivity.class);
        } else {
            i = new Intent(LoadingActivity.this, LoginActivity.class);
        }

        startActivity(i);
    }
}
