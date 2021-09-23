package com.teacher.TeacherAM;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class student_viewHolder extends RecyclerView.ViewHolder {

    private TextView student_name, roll_no;
    public ImageView present, absent;
    private boolean mprocesses = false;
    private boolean absentPrecess = false;
    private Dialog failed_dialog;
    private int sum =0;
    private int sum1=0;
    private  int SP = 0;
    public CheckBox check_box, absent_check_box;


    public student_viewHolder(@NonNull View itemView) {
        super(itemView);

        student_name = itemView.findViewById(R.id.student_name);
        roll_no = itemView.findViewById(R.id.roll_no);
        present = itemView.findViewById(R.id.present);
        absent = itemView.findViewById(R.id.absent);
        check_box = itemView.findViewById(R.id.check_box);
        absent_check_box = itemView.findViewById(R.id.absent_check_box);
    }

    public void setData(String s_name, String rollNo, student_list_Activity student_list_activity, String branch1, String s, String stream, String startTime, String endTime, String subject, Button mark, int position, String currentUserId, String date, Button ok, Dialog markDialog, int lecture, Integer year, String email, String today_date, student_model model) {
        student_name.setText(s_name);
        roll_no.setText(rollNo);

        failed_dialog = new Dialog(student_list_activity);
        failed_dialog.setContentView(R.layout.failed_mark_dialog);
        failed_dialog.setCanceledOnTouchOutside(false);

        Button cancel = failed_dialog.findViewById(R.id.D_cancel);

        DatabaseReference Statu_Ref = FirebaseDatabase.getInstance().getReference("Attendance");
        Statu_Ref.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Status = snapshot.child("Status").getValue().toString();
                    if (Status.equals("Present")) {

                        present.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.present), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {

                        absent.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference Statu_Ref = FirebaseDatabase.getInstance().getReference("Attendance");
                Statu_Ref.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            String statusId = snapshot.child("P_Time").getValue().toString();
                            String Status = snapshot.child("Status").getValue().toString();

                            if (Status.equals("Present")){
                                Toast.makeText(student_list_activity, "Already Marked Present", Toast.LENGTH_SHORT).show();
                            }else {

                                model.setState(true);
                                present.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.present), android.graphics.PorterDuff.Mode.SRC_IN);
                                absent.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);

                                String CurrentTime = String.valueOf(System.currentTimeMillis());
                                final Map messageTextBody = new HashMap();

                                messageTextBody.put("Subject", subject);
                                messageTextBody.put("Roll_No", rollNo);

                                messageTextBody.put("P_Time", CurrentTime);
                                messageTextBody.put("Status", "Present");
                                messageTextBody.put("Lecture_Time", startTime + "-" + endTime);
                                messageTextBody.put("Student_name", s_name);

                                DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Attendance");
                                uploadRef.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).setValue(messageTextBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        DatabaseReference getIdREf = FirebaseDatabase.getInstance().getReference("AllStudents");
                                        getIdREf.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()){
                                                    String id = ""+ds.child("userId").getValue().toString();
                                                    String  token = ""+ds.child("token").getValue().toString();

                                                    DatabaseReference checkStatusRef = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                    checkStatusRef.child(id).child(subject).child(date).child(statusId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            final Map messageTextBody1 = new HashMap();
                                                            messageTextBody1.put("subject", subject);
                                                            messageTextBody1.put("status", "Present");
                                                            messageTextBody1.put("Time", CurrentTime);
                                                            messageTextBody1.put("Class_Date", date);
                                                            messageTextBody1.put("lectureNo", lecture);
                                                            messageTextBody1.put("l_Time", startTime + "-" + endTime);

                                                            DatabaseReference stuREf = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                            stuREf.child(id).child(subject).child(date).child(String.valueOf(CurrentTime)).setValue(messageTextBody1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    DatabaseReference retrieveRecord = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                    retrieveRecord.child(id).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            String initial_p = snapshot.child("Initial_Present").getValue().toString();
                                                                            String T_p = snapshot.child("Total_Classes").getValue().toString();

                                                                            int iP = Integer.parseInt(initial_p);
                                                                            int t = Integer.parseInt(T_p);

                                                                            int p = iP + 1;

                                                                            final Map messageTextBody2 = new HashMap();
                                                                            messageTextBody2.put("Subject_Name", subject);
                                                                            messageTextBody2.put("Initial_Present", p);
                                                                            messageTextBody2.put("Total_Classes", t);
                                                                            messageTextBody2.put("uid", id);

                                                                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                            updateRef.child(id).child(subject).setValue(messageTextBody2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    updateTotalLecture(id);
                                                                                    sendsubjectNotification(id, subject, token, student_list_activity);

                                                                                    String message = s_name+" you are present in "+subject+" lecture "+lecture;
                                                                                    String status1 = "Present";

                                                                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, "Attendance Marked", message, student_list_activity);
                                                                                    notificationsSender.SendNotifications();
                                                                                    sendCreteriaNotification(id, token, student_list_activity);

                                                                                    TodayAttendance(id,subject, date, lecture, CurrentTime, startTime, endTime, status1, student_list_activity, today_date, statusId, currentUserId);

//                                                                                    Toast.makeText(student_list_activity, "Present", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });

//

                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }

                        }

                        else {
                            model.setState(true);

                            present.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.present), android.graphics.PorterDuff.Mode.SRC_IN);
                            absent.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);

                            String CurrentTime = String.valueOf(System.currentTimeMillis());
                            final Map messageTextBody = new HashMap();

                            messageTextBody.put("Subject", subject);
                            messageTextBody.put("Roll_No", rollNo);

                            messageTextBody.put("P_Time", CurrentTime);
                            messageTextBody.put("Status", "Present");
                            messageTextBody.put("Lecture_Time", startTime + "-" + endTime);
                            messageTextBody.put("Student_name", s_name);

                            DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Attendance");
                            uploadRef.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).setValue(messageTextBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

//                                    Toast.makeText(student_list_activity, ""+email, Toast.LENGTH_SHORT).show();
                                    DatabaseReference getIdREf = FirebaseDatabase.getInstance().getReference("AllStudents");
                                    getIdREf.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){

                                                String id = ""+ds.child("userId").getValue().toString();
                                                String  token = ""+ds.child("token").getValue().toString();

//                                                Toast.makeText(student_list_activity, ""+id, Toast.LENGTH_SHORT).show();

                                                final Map messageTextBody1 = new HashMap();
                                                messageTextBody1.put("subject", subject);
                                                messageTextBody1.put("status", "Present");
                                                messageTextBody1.put("Time", CurrentTime);
                                                messageTextBody1.put("Class_Date", date);
                                                messageTextBody1.put("lectureNo", lecture);
                                                messageTextBody1.put("l_Time", startTime + "-" + endTime);

                                                DatabaseReference stuREf = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                stuREf.child(id).child(subject).child(date).child(String.valueOf(CurrentTime)).setValue(messageTextBody1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DatabaseReference retrieveRecord = FirebaseDatabase.getInstance().getReference("Subjects");
                                                        retrieveRecord.child(id).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String initial_p = snapshot.child("Initial_Present").getValue().toString();
                                                                String T_p = snapshot.child("Total_Classes").getValue().toString();

                                                                int i_p = Integer.parseInt(initial_p);
                                                                int t_p = Integer.parseInt(T_p);

                                                                int p = i_p + 1;
                                                                int t = t_p + 1;

                                                                final Map messageTextBody2 = new HashMap();
                                                                messageTextBody2.put("Subject_Name", subject);
                                                                messageTextBody2.put("Initial_Present", p);
                                                                messageTextBody2.put("Total_Classes", t);
                                                                messageTextBody2.put("uid", id);

                                                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                updateRef.child(id).child(subject).setValue(messageTextBody2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        updateTotalLecture(id);
                                                                        sendsubjectNotification(id, subject, token, student_list_activity);

                                                                        String message = s_name+" you are present in "+subject+" lecture "+lecture;
                                                                        String status1 = "Present";

                                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, "Attendance Marked", message, student_list_activity);
                                                                        notificationsSender.SendNotifications();
                                                                        sendCreteriaNotification(id, token, student_list_activity);
                                                                        TodayAllAttendance(id,subject, date, lecture, CurrentTime, startTime, endTime, status1, student_list_activity, today_date, currentUserId);
//                                                                        Toast.makeText(student_list_activity, "Present", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference Statu_Ref = FirebaseDatabase.getInstance().getReference("Attendance");
                Statu_Ref.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            String statusId = snapshot.child("P_Time").getValue().toString();
                            String Status = snapshot.child("Status").getValue().toString();

                            if (Status.equals("Absent")){
                                Toast.makeText(student_list_activity, "Already Marked As Absent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                model.setAbsent_state(true);

                                absent.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                                present.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);

                                String CurrentTime = String.valueOf(System.currentTimeMillis());
                                final Map messageTextBody = new HashMap();

                                messageTextBody.put("Subject", subject);
                                messageTextBody.put("Roll_No", rollNo);

                                messageTextBody.put("P_Time", CurrentTime);
                                messageTextBody.put("Status", "Absent");
                                messageTextBody.put("Lecture_Time", startTime + "-" + endTime);
                                messageTextBody.put("Student_name", s_name);

                                DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Attendance");
                                uploadRef.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).setValue(messageTextBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        DatabaseReference getIdREf = FirebaseDatabase.getInstance().getReference("AllStudents");
                                        getIdREf.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()){
                                                    String id = ""+ds.child("userId").getValue().toString();
                                                    String  token = ""+ds.child("token").getValue().toString();

                                                    DatabaseReference checkStatusRef = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                    checkStatusRef.child(id).child(subject).child(date).child(statusId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            final Map messageTextBody1 = new HashMap();
                                                            messageTextBody1.put("subject", subject);
                                                            messageTextBody1.put("status", "Absent");
                                                            messageTextBody1.put("Time", CurrentTime);
                                                            messageTextBody1.put("Class_Date", date);
                                                            messageTextBody1.put("lectureNo", lecture);
                                                            messageTextBody1.put("l_Time", startTime + "-" + endTime);

                                                            DatabaseReference stuREf = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                            stuREf.child(id).child(subject).child(date).child(String.valueOf(CurrentTime)).setValue(messageTextBody1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    DatabaseReference retrieveRecord = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                    retrieveRecord.child(id).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            String initial_p = snapshot.child("Initial_Present").getValue().toString();
                                                                            String T_p = snapshot.child("Total_Classes").getValue().toString();

                                                                            int iP = Integer.parseInt(initial_p);
                                                                            int t = Integer.parseInt(T_p);

                                                                            int p =iP - 1;

                                                                            final Map messageTextBody2 = new HashMap();
                                                                            messageTextBody2.put("Subject_Name", subject);
                                                                            messageTextBody2.put("Initial_Present", p);
                                                                            messageTextBody2.put("Total_Classes", t);
                                                                            messageTextBody2.put("uid", id);

                                                                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                            updateRef.child(id).child(subject).setValue(messageTextBody2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    updateTotalLecture(id);
                                                                                    sendsubjectNotification(id, subject, token, student_list_activity);

                                                                                    String message = s_name+" you are absent in "+subject+" lecture "+lecture;
                                                                                    String status1 = "Absent";

                                                                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, "Attendance Marked", message, student_list_activity);
                                                                                    notificationsSender.SendNotifications();
                                                                                    sendCreteriaNotification(id, token, student_list_activity);
                                                                                    TodayAttendance(id,subject, date, lecture, CurrentTime, startTime, endTime, status1, student_list_activity, today_date, statusId, currentUserId);
//                                                                                    Toast.makeText(student_list_activity, "Absent", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });

                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }

                        }

                        else {

                            model.setAbsent_state(true);
                            absent.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                            present.setColorFilter(ContextCompat.getColor(student_list_activity, R.color.disable), android.graphics.PorterDuff.Mode.SRC_IN);

                            String CurrentTime = String.valueOf(System.currentTimeMillis());
                            final Map messageTextBody = new HashMap();

                            messageTextBody.put("Subject", subject);
                            messageTextBody.put("Roll_No", rollNo);

                            messageTextBody.put("P_Time", CurrentTime);
                            messageTextBody.put("Status", "Absent");
                            messageTextBody.put("Lecture_Time", startTime + "-" + endTime);
                            messageTextBody.put("Student_name", s_name);

                            DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Attendance");
                            uploadRef.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).child(String.valueOf(lecture)).child(rollNo).setValue(messageTextBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    DatabaseReference getIdREf = FirebaseDatabase.getInstance().getReference("AllStudents");
                                    getIdREf.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){
                                                String id = ""+ds.child("userId").getValue().toString();
                                                String  token = ""+ds.child("token").getValue().toString();

                                                final Map messageTextBody1 = new HashMap();
                                                messageTextBody1.put("subject", subject);
                                                messageTextBody1.put("status", "Absent");
                                                messageTextBody1.put("Time", CurrentTime);
                                                messageTextBody1.put("Class_Date", date);
                                                messageTextBody1.put("lectureNo", lecture);
                                                messageTextBody1.put("l_Time", startTime + "-" + endTime);

                                                DatabaseReference stuREf = FirebaseDatabase.getInstance().getReference("Student_Status");
                                                stuREf.child(id).child(subject).child(date).child(String.valueOf(CurrentTime)).setValue(messageTextBody1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DatabaseReference retrieveRecord = FirebaseDatabase.getInstance().getReference("Subjects");
                                                        retrieveRecord.child(id).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String initial_p = snapshot.child("Initial_Present").getValue().toString();
                                                                String T_p = snapshot.child("Total_Classes").getValue().toString();

                                                                int p = Integer.parseInt(initial_p);
                                                                int tP = Integer.parseInt(T_p);

                                                                int t = tP + 1;

                                                                final Map messageTextBody2 = new HashMap();
                                                                messageTextBody2.put("Subject_Name", subject);
                                                                messageTextBody2.put("Initial_Present", p);
                                                                messageTextBody2.put("Total_Classes", t);
                                                                messageTextBody2.put("uid", id);

                                                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("Subjects");
                                                                updateRef.child(id).child(subject).setValue(messageTextBody2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        updateTotalLecture(id);
                                                                        sendsubjectNotification(id, subject, token, student_list_activity);
                                                                        String message = s_name+" you are absent in "+subject+" lecture "+lecture;
                                                                        String status1 = "Absent";

                                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, "Attendance Marked", message, student_list_activity);
                                                                        notificationsSender.SendNotifications();

                                                                        sendCreteriaNotification(id, token, student_list_activity);
                                                                        TodayAllAttendance(id,subject, date, lecture, CurrentTime, startTime, endTime, status1, student_list_activity, today_date, currentUserId);
//                                                                        Toast.makeText(student_list_activity, "Absent", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Attendance");
                uploadRef.child(currentUserId).child(branch1).child(String.valueOf(year)).child(s).child(subject).child(date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(String.valueOf(lecture))) {

                            markDialog.show();

                        } else {

                            failed_dialog.show();

//                            Toast.makeText(student_list_activity, "Not mark", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed_dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markDialog.dismiss();
            }
        });

    }

    private void sendsubjectNotification(String id, String subject, String token, student_list_Activity student_list_activity) {
        DatabaseReference retrieveSubject = FirebaseDatabase.getInstance().getReference("Subjects");
        retrieveSubject.child(id).child(subject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String initial_p1 = snapshot.child("Initial_Present").getValue().toString();
                String T_p1 = snapshot.child("Total_Classes").getValue().toString();

                int iP1 = Integer.parseInt(initial_p1);
                int t1 = Integer.parseInt(T_p1);

                float p = iP1;
                float t = t1;

                double percentage = (p/t)*100;

                if (percentage<60){
                    String message = "Your attendance in "+subject +" is less than 60%";
                    FcmNotificationsSender notificationsSender2 = new FcmNotificationsSender(token, "Attendance Low", message, student_list_activity);
                    notificationsSender2.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateTotalLecture(String id) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Subjects");
        mDatabase.child(id).orderByChild("uid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    int tl = Integer.parseInt(""+ds.child("Total_Classes").getValue());
                    int tp = Integer.parseInt(""+ds.child("Initial_Present").getValue());

                    SP = SP + tp;
                    sum = sum + tl;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Total_Lecture").child(id);
                    ref.child("TL").setValue(sum);
                    ref.child("TP").setValue(SP);

                }
                sum=0;
                SP =0 ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void TodayAllAttendance(String id, String subject, String date, int lecture, String currentTime, String startTime, String endTime, String status1, student_list_Activity student_list_activity, String today_date, String currentUserId) {
        final Map data = new HashMap();
        data.put("Subject", subject);
        data.put("lecture_no", lecture);
        data.put("Lecture_Time", startTime+"-"+endTime);
        data.put("mark_time", currentTime);
        data.put("Status", status1);
        data.put("Current_date", date);
        data.put("StudentId", id);
//        data.put("TeacherId", currentUserId);

        if (today_date.equals(date)){
            DatabaseReference TodayRef = FirebaseDatabase.getInstance().getReference("Today_Attendance");
            TodayRef.child(id).child(currentTime).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Map TodayData = new HashMap();
                    TodayData.put("Subject", subject);
                    TodayData.put("lecture_No", lecture);
                    TodayData.put("Lecture_Time", startTime+"-"+endTime);
                    TodayData.put("mark_Time", currentTime);
                    TodayData.put("Current_date", date);
                    TodayData.put("Teacher_id", currentUserId);

                    DatabaseReference TeacherRef = FirebaseDatabase.getInstance().getReference("Today_Teacher_Ref");
                    TeacherRef.child(currentUserId).child(currentTime).setValue(TodayData);
                }
            });

        }

    }

    private void TodayAttendance(String id, String subject, String date, int lecture, String currentTime, String startTime, String endTime, String status1, student_list_Activity student_list_activity, String today_date, String statusId, String currentUserId) {

        Query deleteTodayRef = FirebaseDatabase.getInstance().getReference("Today_Attendance").child(id).orderByChild("mark_time").equalTo(statusId);
        deleteTodayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            final Map data = new HashMap();
                            data.put("Subject", subject);
                            data.put("lecture_no", lecture);
                            data.put("Lecture_Time", startTime+"-"+endTime);
                            data.put("mark_time", currentTime);
                            data.put("Status", status1);
                            data.put("Current_date", date);
                            data.put("StudentId", id);
//                            data.put("TeacherId", currentUserId);

                            if (today_date.equals(date)){
                                DatabaseReference TodayRef = FirebaseDatabase.getInstance().getReference("Today_Attendance");
                                TodayRef.child(id).child(currentTime).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Query TecherQuery = FirebaseDatabase.getInstance().getReference("Today_Teacher_Ref").child(currentUserId).orderByChild("mark_Time").equalTo(statusId);
                                        TecherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds1 : snapshot.getChildren()){
                                                    ds1.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Map TodayData = new HashMap();
                                                            TodayData.put("Subject", subject);
                                                            TodayData.put("lecture_No", lecture);
                                                            TodayData.put("Lecture_Time", startTime+"-"+endTime);
                                                            TodayData.put("mark_Time", currentTime);
                                                            TodayData.put("Current_date", date);
                                                            TodayData.put("Teacher_id", currentUserId);

                                                            DatabaseReference TeacherRef = FirebaseDatabase.getInstance().getReference("Today_Teacher_Ref");
                                                            TeacherRef.child(currentUserId).child(currentTime).setValue(TodayData);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendCreteriaNotification(String id, String token, student_list_Activity student_list_activity) {

        DatabaseReference totalPer = FirebaseDatabase.getInstance().getReference("Total_Lecture");
        totalPer.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float t = snapshot.child("TL").getValue().hashCode();
                float p = snapshot.child("TP").getValue().hashCode();

                double percentage = (p/t)*100;

                if (percentage<75){

                    String message = "Your overall is less than 75%";
                    FcmNotificationsSender notificationsSender1 = new FcmNotificationsSender(token, "Attendance Low", message, student_list_activity);
                    notificationsSender1.SendNotifications();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
