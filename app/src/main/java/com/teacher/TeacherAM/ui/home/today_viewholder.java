package com.teacher.TeacherAM.ui.home;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.teacher.TeacherAM.R;

import java.util.Calendar;

public class today_viewholder extends RecyclerView.ViewHolder {

    private TextView subject, lectureNO, lectureTime, mark_time;


    public today_viewholder(@NonNull View itemView) {

        super(itemView);
        subject = itemView.findViewById(R.id.subject);
        lectureNO = itemView.findViewById(R.id.lectureNO);
        lectureTime = itemView.findViewById(R.id.lectureTime);
        mark_time = itemView.findViewById(R.id.mark_time);
    }

    public String getFormateDate(FragmentActivity activity, String markTime) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(markTime));

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    public void setData(int lec_No, String markingTime, String lec_tIme, String currentDate, String sub) {
        subject.setText(sub);
        lectureNO.setText("Lecture Number : "+ lec_No);
        lectureTime.setText(lec_tIme);
        mark_time.setText(markingTime);
    }
}
