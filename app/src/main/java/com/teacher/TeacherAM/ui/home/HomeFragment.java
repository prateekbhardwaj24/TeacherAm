package com.teacher.TeacherAM.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teacher.TeacherAM.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView no_status;
    private FirebaseAuth mAuth;
    private String CurrentUserId, Today_date;
    private DatabaseReference mDatabase;
    private RecyclerView sub_recycler;
    private RecyclerView.LayoutManager manager;
    private FirebaseRecyclerAdapter<today_model, today_viewholder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sub_recycler = root.findViewById(R.id.sub_recycler);
        no_status = root.findViewById(R.id.no_status);

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM-dd-yy");
        Date myDate = new Date();
        Today_date = timeStampFormat.format(myDate);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Today_Teacher_Ref");

        manager = new LinearLayoutManager(getContext());
        sub_recycler.setLayoutManager(manager);

        checkData();

        FirebaseRecyclerOptions<today_model> options = new FirebaseRecyclerOptions.Builder<today_model>().setQuery(mDatabase.child(CurrentUserId), today_model.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<today_model, today_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull today_viewholder holder, int position, @NonNull today_model model) {
                int lectureNo = model.getLecture_No();
                String markTime = model.getMark_Time();
                String LectureTime = model.getLecture_Time();
                String CurrentDate = model.getCurrent_date();
                String subject = model.getSubject();

                String markingTime = holder.getFormateDate(getActivity(), markTime);

                    if (CurrentDate.equals(Today_date)){
                        holder.setData(lectureNo, markingTime, LectureTime, CurrentDate, subject);
                    }

            }

            @NonNull
            @Override
            public today_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_subject_layout, parent, false);
                return new today_viewholder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        sub_recycler.setAdapter(adapter);

        return root;
    }

    private void checkData() {


        mDatabase.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    no_status.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(), "not exist here", Toast.LENGTH_SHORT).show();
                }
                else {
//                    mDatabase.child(CurrentUserId).orderByChild("StudentId").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot ds : snapshot.getChildren()){
//                                if (!ds.exists()){
//                                    no_status.setVisibility(View.VISIBLE);
////                                    Toast.makeText(getActivity(), "not exist", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
////                                    Toast.makeText(getActivity(), "Exist", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}