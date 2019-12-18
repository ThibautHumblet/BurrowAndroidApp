package com.example.mobdev_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextView lblAccountEmail;
    private Button btnLogOut;
    private Button btnOpenChangePass;

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
        btnLogOut = view.findViewById(R.id.btnLogout);
        btnOpenChangePass = view.findViewById(R.id.btnOpenChangePass);

        // Update account info
        if (currentUser != null) {
            String email = currentUser.getEmail();
            lblAccountEmail.setText(email);
        }

        // Logout
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    auth.signOut();
                }
                Intent i = new Intent(getActivity(), LoginActivity.class);
                // Clear backstack
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        // Open change password dialog.
        btnOpenChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
                final LayoutInflater inflater = requireActivity().getLayoutInflater();

                final View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
                final EditText txtNewPass = dialogView.findViewById(R.id.txtNewPass);
                final EditText txtConfirmNewPass = dialogView.findViewById(R.id.txtConfirmNewPass);
                final EditText txtCurrentPass = dialogView.findViewById(R.id.txtPassword);
                final ProgressBar pbLoading = dialogView.findViewById(R.id.pbChangePass);


                builder.setView(dialogView);
                builder.setPositiveButton(R.string.change_password_positive, null);
                builder.setNegativeButton(R.string.change_password_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                final AlertDialog changePasswordDialog = builder.create();
                changePasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        Button btnConfirm = changePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String newPass = txtNewPass.getText().toString();
                                final String confirmNewPass = txtConfirmNewPass.getText().toString();
                                if (newPass.equals(confirmNewPass)) {
                                    pbLoading.setVisibility(View.VISIBLE);
                                    // Reauthenticate user
                                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), txtCurrentPass.getText().toString());
                                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Change password
                                                currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Password veranderd!", Toast.LENGTH_LONG).show();
                                                            dialogInterface.dismiss();
                                                        } else {
                                                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                                }
                            }
                        });
                    }
                });

                changePasswordDialog.show();
            }
        });
    }
}
