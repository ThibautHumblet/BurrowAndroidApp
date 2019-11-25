package com.example.mobdev_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class BorrowedMeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewGroup view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = container;
        getTestData();
        return  inflater.inflate(R.layout.fragment_borrowed_mine, container, false);
    }

    // See: https://firebase.google.com/docs/firestore/quickstart
    private void getTestData() {
        db.collection("test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore data: ", document.getId() + " => " + document.getData());

                                TextView text = view.findViewById(R.id.my_items);
                                text.setText(document.getString("lol"));
                            }
                        } else {
                            Log.w("Firestore ERROR: ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
