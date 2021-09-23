package com.teacher.TeacherAM.ui.slideshow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teacher.TeacherAM.R;
import com.teacher.TeacherAM.student_list_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private Spinner spinner,branch_spinner, year_spinner, sec_spinner, l_Spinner;
    private TextView selectDate, showdate;
    private DatePickerDialog.OnDateSetListener listener;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<Integer> yearList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> subjectList = new ArrayList<>();
    private ArrayList<String> section1List = new ArrayList<>();
    private ArrayList<String> subjectList1 = new ArrayList<>();
    private Integer Lecture[] = {1, 2, 3, 4, 5, 6, 7, 8};
    private FirebaseAuth mAuth;
    private String CurrentUserId,date;
    private  Calendar myCalendar;
    private TextView startTime, endTime;
    private int t1hour, t1minute, t2hours, t2minute;
    private Button next;
    private Date date1, date2;
    private SimpleDateFormat f12hours1, f12hours2;
    private String start_Time ,Branch_name, end_Time, year_value, section, subject, section1;
    private int lecture_value;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students");

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        branch_spinner = root.findViewById(R.id.branch_spinner);
        spinner = root.findViewById(R.id.spinner);
        year_spinner = root.findViewById(R.id.year_spinner);
        sec_spinner = root.findViewById(R.id.sec_spinner);
        l_Spinner = root.findViewById(R.id.l_Spinner);
        startTime = root.findViewById(R.id.startTime);
        endTime = root.findViewById(R.id.endTime);
        next = root.findViewById(R.id.next);
        selectDate = root.findViewById(R.id.selectDate);
        showdate = root.findViewById(R.id.showdate);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        showBranchSpinner();
        selectStartTime();
        selectEndTime();
        showSection();
        showDate();
        showLecture();
        getSectionSpinnerValue();
        getSubjectSpinnerValue();
        getLectureSpinnervalue();
        showSubject1();

//        showYearSpinner();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                subject = spinner.getSelectedItem().toString();
//                section = sec_spinner.getSelectedItem().toString();
//                lecture_value = Integer.parseInt(l_Spinner.getSelectedItem().toString());

                if (TextUtils.isEmpty(start_Time) && TextUtils.isEmpty(end_Time) && TextUtils.isEmpty(Branch_name) && TextUtils.isEmpty(year_value) && TextUtils.isEmpty(subject) &&

                TextUtils.isEmpty(section) && TextUtils.isEmpty(date)){
                    Toast.makeText(getActivity(), "Please Fill All Credentials", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(start_Time) || TextUtils.isEmpty(end_Time) || TextUtils.isEmpty(Branch_name) || TextUtils.isEmpty(year_value)
                || TextUtils.isEmpty(subject) || TextUtils.isEmpty(section) || TextUtils.isEmpty(date)){
                    if (TextUtils.isEmpty(start_Time)){
                        Toast.makeText(getActivity(), "Please Select Class Start Time", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(end_Time)){
                        Toast.makeText(getActivity(), "Please Select Class End Time", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(subject)){
                        Toast.makeText(getActivity(), "Please Select Subject", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(section)){
                        Toast.makeText(getActivity(), "Please Select Section", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(year_value)){
                        Toast.makeText(getActivity(), "Please Select Year", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(Branch_name)){
                        Toast.makeText(getActivity(), "Please Select Branch", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(date)){
                        Toast.makeText(getActivity(), "Please Select Date", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    Intent intent = new Intent(getActivity(), student_list_Activity.class);
                    intent.putExtra("startTime", start_Time);
                    intent.putExtra("endTime", end_Time);
                    intent.putExtra("branch", Branch_name);
                    intent.putExtra("year", year_value);
                    intent.putExtra("subject", subject);
                    intent.putExtra("section", section);
                    intent.putExtra("date", date);
                    intent.putExtra("lecture", lecture_value);
                    startActivity(intent);
                }


            }
        });
        
       branch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Branch_name = parent.getItemAtPosition(position).toString();

               reference.child(Branch_name).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       yearList.clear();
                       for (DataSnapshot ds : snapshot.getChildren()){
                           yearList.add(Integer.valueOf(ds.getKey()));
                       }

                       ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.show_year, yearList);
                       year_spinner.setAdapter(arrayAdapter);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

//               showSectionSpinner(Branch_name);

//               Toast.makeText(getContext(), ""+Branch_name, Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        return root;
    }

    private void showSection() {
        DatabaseReference secRef = FirebaseDatabase.getInstance().getReference("Teacher_Info");
        secRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                section1List.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    section1List.add(ds.getKey());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, section1List);
                sec_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLectureSpinnervalue() {
        l_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lecture_value = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSectionSpinnerValue() {
        sec_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showSubject1() {
        DatabaseReference subRef = FirebaseDatabase.getInstance().getReference("Teacher_Info");

        sec_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String section_value = parent.getItemAtPosition(position).toString();
                subRef.child(CurrentUserId).child(section_value).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        subjectList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            subjectList.add(ds.child("Subject").getValue().toString());
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),  android.R.layout.simple_spinner_item, subjectList);
                        spinner.setAdapter(spinnerArrayAdapter);
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

    private void getSubjectSpinnerValue() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showLecture() {
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(getActivity(),  android.R.layout.simple_spinner_item, Lecture);
        l_Spinner.setAdapter(spinnerArrayAdapter);
    }

    private void showDate() {

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM-dd-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date = sdf.format(myCalendar.getTime());

        selectDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void selectEndTime() {

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                t2hours = hourOfDay;
                                t2minute = minute;

                                String time = t2hours + ":" + t2minute;

                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    date2 = f24Hours.parse(time);

                                    f12hours2 = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );

                                    end_Time = f12hours2.format(date2);
                                    endTime.setText(f12hours2.format(date2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                timePickerDialog.updateTime(t2hours, t2minute);

                timePickerDialog.show();
            }
        });
    }

    private void selectStartTime() {
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                t1hour = hourOfDay;
                                t1minute = minute;

                                String time = t1hour + ":" + t1minute;

                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    date1 = f24Hours.parse(time);

                                    f12hours1 = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );

                                    start_Time = f12hours1.format(date1);
                                    startTime.setText(f12hours1.format(date1));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                timePickerDialog.updateTime(t1hour, t1minute);

                timePickerDialog.show();
            }
        });
    }

    private void showBranchSpinner() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    arrayList.add(ds.getKey());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_element, arrayList);
                branch_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showSectionSpinner(String branch_name) {
        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_value = parent.getItemAtPosition(position).toString();

                reference.child(branch_name).child(year_value).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sectionList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            sectionList.add(ds.getKey());
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sectionList);
                        sec_spinner.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                showSubjectSpinner(branch_name, year_value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showSubjectSpinner(String branch_name, String year_value) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AllSubjects");
        ref.child(branch_name).child(year_value).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    subjectList.add(ds.getKey());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, subjectList);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}