package com.example.mobdev_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextView lblAccountEmail;
    private FloatingActionButton btnAccountEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);

        // Find views
        lblAccountEmail = view.findViewById(R.id.lblAccountEmail);
        btnAccountEdit = view.findViewById(R.id.btnAccountEdit);

        if (currentUser != null) {
            String email = currentUser.getEmail();
            // Update e-mail label
            lblAccountEmail.setText(email);
        }
    }
}
