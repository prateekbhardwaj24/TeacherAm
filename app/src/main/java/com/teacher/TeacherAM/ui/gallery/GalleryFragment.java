package com.teacher.TeacherAM.ui.gallery;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private Button delete_item, cancel_dialog;
    private Dialog delete_dialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String CurrentUserId, branch_name;
    private Integer year_value;
    private RecyclerView sub_recycler;
    private GalleryViewModel galleryViewModel;
    private RecyclerView.LayoutManager manager;
    private Spinner b_spinner, y_spinner;
    private ArrayList<String> branchList = new ArrayList<>();
    private Integer Year[] = {1, 2, 3, 4};
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students");
    FirebaseRecyclerAdapter<subject_model, subject_Viewholder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sub_recycler = root.findViewById(R.id.sub_recycler);
        b_spinner = root.findViewById(R.id.b_spinner);
        y_spinner = root.findViewById(R.id.y_spinner);

        delete_dialog = new Dialog(getContext());
        delete_dialog.setContentView(R.layout.delete_dialog);
        delete_dialog.setCanceledOnTouchOutside(false);

        delete_item = delete_dialog.findViewById(R.id.delete);
        cancel_dialog = delete_dialog.findViewById(R.id.cancel);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("AllSubjects");

        manager = new LinearLayoutManager(getContext());
        sub_recycler.setLayoutManager(manager);

        showBranchSpinner();
        showYearSpinner();

        b_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branch_name = parent.getItemAtPosition(position).toString();

                y_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        year_value = Integer.valueOf(parent.getItemAtPosition(position).toString());

                        showSubject(branch_name, year_value);

//                        Toast.makeText(getContext(), ""+branch_name+" "+ year_value, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return root;
    }

    private void showSubject(String branch_name, Integer year_value) {

        FirebaseRecyclerOptions<subject_model> options = new FirebaseRecyclerOptions.Builder<subject_model>().setQuery(mDatabase.child(branch_name).child(String.valueOf(year_value)), subject_model.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<subject_model, subject_Viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull subject_Viewholder holder, int position, @NonNull subject_model model) {

                String B_name = model.getSubject();

                holder.setData(B_name, getContext(), delete_item, delete_dialog, cancel_dialog, CurrentUserId);
            }

            @NonNull
            @Override
            public subject_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_subject_layout , parent , false);
                return new subject_Viewholder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        sub_recycler.setAdapter(adapter);
    }

    private void showYearSpinner() {
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(getActivity(),  android.R.layout.simple_spinner_item, Year);
        y_spinner.setAdapter(spinnerArrayAdapter);
    }

    private void showBranchSpinner() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                branchList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    branchList.add(ds.getKey());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_element, branchList);
                b_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}