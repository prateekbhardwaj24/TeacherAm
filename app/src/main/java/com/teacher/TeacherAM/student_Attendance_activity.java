package com.teacher.TeacherAM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class student_Attendance_activity extends AppCompatActivity {

    private RecyclerView recycler;
    private DatabaseReference databaseReference;
    private StudentPercentageAdapter studentPercentageAdapter;
    private ArrayList<StudentData> list;
    private Intent intent;
    static String tvsec,tvsub,tvradio;
    static Integer tvseek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__attendance_activity);

        intent = getIntent();
        tvsec = intent.getStringExtra("section");
        tvsub = intent.getStringExtra("subject");
        tvseek = intent.getIntExtra("seek",0);
        tvradio = intent.getStringExtra("radio");

        recycler = findViewById(R.id.recycler);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child("EN").child("3").child(tvsec);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        studentPercentageAdapter= new StudentPercentageAdapter(this,list);
        recycler.setAdapter(studentPercentageAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    StudentData studentData = dataSnapshot.getValue(StudentData.class);
                    list.add(studentData);
                }
                studentPercentageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}