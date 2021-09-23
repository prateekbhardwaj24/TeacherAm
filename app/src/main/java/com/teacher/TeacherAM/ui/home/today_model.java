package com.teacher.TeacherAM.ui.home;

public class today_model {

    String Lecture_Time, Current_date, Subject, Teacher_id, mark_Time;
    int lecture_No;

    public today_model() {
    }

    public today_model(String lecture_Time, String current_date, String subject, String teacher_id, String mark_Time, int lecture_No) {
        Lecture_Time = lecture_Time;
        Current_date = current_date;
        Subject = subject;
        Teacher_id = teacher_id;
        this.mark_Time = mark_Time;
        this.lecture_No = lecture_No;
    }

    public String getLecture_Time() {
        return Lecture_Time;
    }

    public void setLecture_Time(String lecture_Time) {
        Lecture_Time = lecture_Time;
    }

    public String getCurrent_date() {
        return Current_date;
    }

    public void setCurrent_date(String current_date) {
        Current_date = current_date;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getMark_Time() {
        return mark_Time;
    }

    public void setMark_Time(String mark_Time) {
        this.mark_Time = mark_Time;
    }

    public int getLecture_No() {
        return lecture_No;
    }

    public void setLecture_No(int lecture_No) {
        this.lecture_No = lecture_No;
    }
}
