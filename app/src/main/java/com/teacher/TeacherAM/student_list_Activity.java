package com.teacher.TeacherAM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class student_list_Activity extends AppCompatActivity {

    private String startTime, endTime, subject, Branch, section, date, Today_date;
    private int lecture;
    private Dialog markDialog;
    private Button mark;
    private Integer year;
    private RecyclerView stu_recycler;
    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase;
    private String CurrentUserId;
    private RecyclerView.LayoutManager manager;
    FirebaseRecyclerAdapter<student_model, student_viewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list_);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        startTime = getIntent().getExtras().get("startTime").toString();
        endTime = getIntent().getExtras().get("endTime").toString();
        subject = getIntent().getExtras().get("subject").toString();
        Branch = getIntent().getExtras().get("branch").toString();
        section = getIntent().getExtras().get("section").toString();
        year = Integer.valueOf(getIntent().getExtras().get("year").toString());
        date = getIntent().getExtras().get("date").toString();
        lecture = Integer.parseInt(getIntent().getExtras().get("lecture").toString());

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM-dd-yy");
        Date myDate = new Date();
        Today_date = timeStampFormat.format(myDate);


        stu_recycler = findViewById(R.id.stu_recycler);
        mark = findViewById(R.id.mark);

        markDialog = new Dialog(this);
        markDialog.setContentView(R.layout.mark_attenadance_dialog);
        markDialog.setCanceledOnTouchOutside(false);

        Button ok = markDialog.findViewById(R.id.ok);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Students");

        manager = new LinearLayoutManager(this);
        stu_recycler.setLayoutManager(manager);

        FirebaseRecyclerOptions<student_model> options = new FirebaseRecyclerOptions.Builder<student_model>().setQuery(mDatabase.child(Branch).child(String.valueOf(year))
                .child(section), student_model.class).build();
        adapter = new FirebaseRecyclerAdapter<student_model, student_viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull student_viewHolder holder, int position, @NonNull student_model model) {
                String Student_name = model.getName();
                String Roll_no = model.getRollNo();
//                String Branch = model.getBranch();
                String Section = model.getSection();
                String Stream = model.getStream();
                String Email = model.getEmail();

                if (model.isState() == false ){

                    holder.present.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);

                }
                if (model.isAbsent_state() == false){
                    holder.absent.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);
                }

//                holder.present.setOnClickListener(null);
                if (model.isState() == true){

                    holder.present.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.present), android.graphics.PorterDuff.Mode.SRC_IN);

                }

                if (model.isAbsent_state() == true){
                    holder.absent.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }

//                holder.present.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        model.setState(true);
//                        model.setAbsent_state(false);
//                        holder.absent.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);
//                        holder.present.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.present), android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                });
//                holder.absent.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        model.setAbsent_state(true);
//                        model.setState(false);
//                        holder.absent.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
//                        holder.present.setColorFilter(ContextCompat.getColor(student_list_Activity.this, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                });



                holder.setData(Student_name, Roll_no, student_list_Activity.this, Branch, section, Stream, startTime, endTime, subject, mark, position, CurrentUserId, date, ok, markDialog, lecture, year, Email, Today_date, model);

            }

            @NonNull
            @Override
            public student_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout , parent , false);
                return new student_viewHolder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        stu_recycler.setAdapter(adapter);
    }
}