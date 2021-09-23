package com.teacher.TeacherAM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add_subject_Activity extends AppCompatActivity {

    private Button add;
    private EditText sub_name;
    private Spinner branch_spinner, year_spinner;
    private String branch_name, CurrentUserId;
    private FirebaseAuth mAuth ;
    private Integer year_value;
    private DatabaseReference mDatabase;
    private ArrayList<String> branchList = new ArrayList<>();
    Integer Year[] = {1, 2, 3, 4};
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject_);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        add = findViewById(R.id.add);
        sub_name = findViewById(R.id.sub_name);
        branch_spinner = findViewById(R.id.branch_spinner);
        year_spinner = findViewById(R.id.year_spinner);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("B_Subjects");

        showBranchSpinner();
        showYearspinner();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(sub_name.getText().toString()) || branch_spinner.getSelectedItem() == null){
                    Toast.makeText(add_subject_Activity.this, "Please Fill All Credentials", Toast.LENGTH_SHORT).show();
                }
                else {

                    String subNAme = sub_name.getText().toString().trim();
                    branch_name = branch_spinner.getSelectedItem().toString();
                    year_value = Integer.valueOf(year_spinner.getSelectedItem().toString());

                    final Map messageTextBody = new HashMap();
                    messageTextBody.put("Subject_Name", subNAme);
                    messageTextBody.put("Branch_Name", branch_name);
                    messageTextBody.put("Year_Name", year_value);

                    mDatabase.child(branch_name).child(String.valueOf(year_value)).child(CurrentUserId).child(subNAme).setValue(messageTextBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(add_subject_Activity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void showYearspinner() {
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(this,   android.R.layout.simple_spinner_item, Year);
        year_spinner.setAdapter(spinnerArrayAdapter);
    }

    private void showBranchSpinner() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                branchList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    branchList.add(ds.getKey());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(add_subject_Activity.this, android.R.layout.simple_spinner_item, branchList);
                branch_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}