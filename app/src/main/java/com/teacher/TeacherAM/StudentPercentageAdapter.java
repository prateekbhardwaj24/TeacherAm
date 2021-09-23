package com.teacher.TeacherAM;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StudentPercentageAdapter extends RecyclerView.Adapter<StudentPercentageAdapter.MyViewHolder>{

    student_Attendance_activity mainActivity;
    Context context;
    ArrayList<StudentData> list;

    public StudentPercentageAdapter(Context context, ArrayList<StudentData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StudentPercentageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemcard,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentPercentageAdapter.MyViewHolder holder, int position) {
        StudentData studentData = list.get(position);
        holder.studentname.setText(studentData.getName());
        holder.studentrollno.setText(studentData.getRollNo());
        String Subject = mainActivity.tvsub;
        String section = mainActivity.tvsec;
        int seek = mainActivity.tvseek;
        String radio = mainActivity.tvradio;
        String Email = studentData.getEmail();

        holder.useridfromEmail(Subject,section,seek,radio,Email,position,studentData);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView studentname,studentrollno,studentperc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            studentname = itemView.findViewById(R.id.name);
            studentrollno = itemView.findViewById(R.id.roll);
            studentperc = itemView.findViewById(R.id.percentStudent);
        }

        public void useridfromEmail(String subject, String section, int seek, String radio, String email, int position, StudentData studentData) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AllStudents");
            ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String id = ""+ds.child("userId").getValue().toString();

                        getPercentagefromsub( subject,section, seek,  radio, id, position,studentData);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getPercentagefromsub(String subject, String section, int seek, String radio, String id, int position, StudentData studentData) {
            DatabaseReference refer= FirebaseDatabase.getInstance().getReference("Subjects");
            refer.child(id).child(subject).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String initial_p1 = snapshot.child("Initial_Present").getValue().toString();
                    String T_p1 = snapshot.child("Total_Classes").getValue().toString();

                    int iP1 = Integer.parseInt(initial_p1);
                    int t1 = Integer.parseInt(T_p1);

                    float p = iP1;
                    float t = t1;

                    double percentage = (p/t)*100;

//                    studentperc.setText(new DecimalFormat("##.##").format(percentage) + "%");
                    studentperc.setText(initial_p1);

//                    if (percentage > seek){
//                        list.remove(studentData);
//                            notifyItemRemoved(position);
//                            notifyItemRangeChanged(position, list.size());
//                    }
//
//                    if (radio.equals("Above")){
//                        if (percentage > seek){
//
////                            studentperc.setText(new DecimalFormat("##.##").format(percentage) + "%");
//
//                        }
//                        if(percentage < seek){
//                            String email = studentData.getEmail();
//
//                            int index = list.indexOf(new StudentData());
//                            list.remove(studentData);
//                            notifyItemRemoved(position);
////                            notifyItemRangeChanged(position, list.size());
//                        }
//                    }else{
//                        if (percentage < seek){
//
////                            studentperc.setText(new DecimalFormat("##.##").format(percentage) + "%");
//
//                        }
//                        if (percentage > seek){
//                            String email = studentData.getEmail();
//
//                            int index = list.indexOf(new StudentData());
//                            list.remove(studentData);
//                            notifyItemRemoved(position);
//                            notifyItemRangeChanged(position, list.size());
//                        }
//
//                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
