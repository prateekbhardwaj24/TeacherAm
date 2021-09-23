package com.teacher.TeacherAM.ui.student_Attendance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teacher.TeacherAM.MainActivity;
import com.teacher.TeacherAM.R;
import com.teacher.TeacherAM.student_Attendance_activity;

import java.util.ArrayList;

public class StudentAttendance extends Fragment {

    private SeekBar seekBar;
    private Spinner section;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Spinner subject;
    private TextView percentage_criteria;
    private Button next_btn;
    private String section_value;
    private String subject_value;
    private ValueEventListener listener;
    private ArrayList<String> spinnerdata;
    private ArrayList<String> spinsub;
    private ArrayAdapter<String> adapter;
    private Integer number;
    private String value, CurrentUserId;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        seekBar = root.findViewById(R.id.perc_seekbar);
        section = root.findViewById(R.id.sec_spinner);
        subject = root.findViewById(R.id.Sub_spinner);
        radioGroup = root.findViewById(R.id.radiogroup);
        percentage_criteria = root.findViewById(R.id.perc_crit);
        next_btn = root.findViewById(R.id.Next_btn);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Teacher_Info").child(CurrentUserId);

        spinnerdata = new ArrayList<>();
        spinsub = new ArrayList<>();

        retrieveData();


        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject_value = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentage_criteria.setText(String.valueOf(progress)+ "%");
                number = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId)
                                                  {
                                                      radioButton = (RadioButton) root.findViewById(checkedId);
                                                      value = (String) radioButton.getText();
                                                  }
                                              }
        );

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(subject_value) && TextUtils.isEmpty(section_value) && number==null && TextUtils.isEmpty(value)){

                    Toast.makeText(getActivity(), "Please fill all details!", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(subject_value) || TextUtils.isEmpty(section_value) || number==null || TextUtils.isEmpty(value)){
                    if (TextUtils.isEmpty(subject_value)){
                        Toast.makeText(getActivity(), "Please enter subject", Toast.LENGTH_SHORT).show();
                    }
                    if (number == null){
                        Toast.makeText(getActivity(), "Please choose range of Percentage", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(value)){
                        Toast.makeText(getActivity(), "Please select at least 1 from above & below", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intent = new Intent(getActivity(), student_Attendance_activity.class);
                    intent.putExtra("section",section_value);
                    intent.putExtra("subject",subject_value);
                    intent.putExtra("radio", value);
                    intent.putExtra("seek",number);
                    startActivity(intent);
                }

            }
        });

        return root;
    }

    private void retrieveData() {

        listener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinnerdata.clear();

                for (DataSnapshot item:snapshot.getChildren()){
                    spinnerdata.add(item.getKey());
                }
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1,spinnerdata);
                section.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getValueSection();
    }

    private void getValueSection() {
        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section_value = parent.getItemAtPosition(position).toString();

                databaseReference.child(section_value).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spinsub.clear();
                        for (DataSnapshot item:snapshot.getChildren()){
                            spinsub.add(String.valueOf(item.getValue()));
                        }
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1,spinsub);
                        subject.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}