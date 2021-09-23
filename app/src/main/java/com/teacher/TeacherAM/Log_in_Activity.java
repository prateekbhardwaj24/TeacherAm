package com.teacher.TeacherAM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Log_in_Activity extends AppCompatActivity {


    private EditText LogInEmail, LogInPassword;
    private FirebaseAuth fAuth;
    private TextView forgotten;
    private Button logIn;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LogInEmail = findViewById(R.id.log_in_email);
        LogInPassword = findViewById(R.id.log_in_pass);
        logIn = findViewById(R.id.log_in_btn);
        forgotten = findViewById(R.id.forgotten);

        fAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        loadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingBar.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
//        CurrentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = LogInEmail.getText().toString().trim();
                String Password = LogInPassword.getText().toString();

                String emailPattern = "[a-zA._-]+@[a-z]+\\.+[a-z]+";


                if (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password))
                {
                    Toast.makeText(Log_in_Activity.this, "Please Fill All Fields ", Toast.LENGTH_SHORT).show();


                }

                else if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password))
                {

                    if (TextUtils.isEmpty(Email))
                    {
                        Toast.makeText(Log_in_Activity.this, "Please enter email.. ", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(Password))
                    {
                        Toast.makeText(Log_in_Activity.this, "Please enter password.. ", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (Email.matches(emailPattern)){

                    loadingBar.show();
                    loadingBar.setContentView(R.layout.progress_bar_layout);


                    fAuth.signInWithEmailAndPassword(Email , Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {

                                Intent intent = new Intent(Log_in_Activity.this , MainActivity.class);
                                startActivity(intent);
                                finish();
                                loadingBar.dismiss();
                                Toast.makeText(Log_in_Activity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                String message = task.getException().toString();
                                Toast.makeText(Log_in_Activity.this, "Error ! "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(Log_in_Activity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetmail = new EditText(v.getContext());
                AlertDialog.Builder passwordDialog = new AlertDialog.Builder(v.getContext());
                passwordDialog.setTitle("Reset Password");
                passwordDialog.setMessage("Enter Your Mail To Received Reset Link");
                passwordDialog.setView(resetmail);

                passwordDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Log_in_Activity.this, "Reset Link Sent To Your Mail.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });

                passwordDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordDialog.create().show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null)
        {
            Intent intent = new Intent(Log_in_Activity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
//            Toast.makeText(this, "hlo", Toast.LENGTH_SHORT).show();
        }
    }
}