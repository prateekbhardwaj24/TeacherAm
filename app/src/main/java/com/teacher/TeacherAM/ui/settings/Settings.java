package com.teacher.TeacherAM.ui.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teacher.TeacherAM.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends Fragment {

    private TextView emailId;
    private Button change_password;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_attendance, container, false);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        change_password = root.findViewById(R.id.change_password);
        emailId = root.findViewById(R.id.emailId);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserInfo = mAuth.getCurrentUser();
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingBar.setCanceledOnTouchOutside(false);


        String UserEmail = UserInfo.getEmail().toString();
        emailId.setText(UserEmail);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        return root;
    }

    private void showChangePasswordDialog() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_dialog, null);

        EditText c_password = view.findViewById(R.id.c_password);
        EditText n_password = view.findViewById(R.id.n_password);
        Button changePasswordBtn = view.findViewById(R.id.changePasswordBtn);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Current_password = c_password.getText().toString().trim();
                String New_password = n_password.getText().toString().trim();

                if (TextUtils.isEmpty(Current_password) && TextUtils.isEmpty(New_password)){
                    Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(Current_password) || TextUtils.isEmpty(New_password) || New_password.length()<6){
                    if (TextUtils.isEmpty(Current_password)){
                        Toast.makeText(getActivity(), "Please Enter Current Password", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(New_password)){
                        Toast.makeText(getActivity(), "Please Enter New Password", Toast.LENGTH_SHORT).show();
                    }
                    if (New_password.length()<6){
                        Toast.makeText(getActivity(), "Password Length Must AtLeast 6 Characters..", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    loadingBar.show();
                    loadingBar.setContentView(R.layout.progress_bar_layout);
                    ChangePassword(Current_password, New_password, dialog);
                }
            }
        });
    }

    private void ChangePassword(String current_password, String new_password, AlertDialog dialog) {

        FirebaseUser user = mAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current_password);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(new_password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingBar.dismiss();
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Password Change Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        dialog.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                dialog.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}