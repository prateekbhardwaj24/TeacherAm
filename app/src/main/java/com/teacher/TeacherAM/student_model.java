package com.teacher.TeacherAM;

public class student_model {
    private String Branch, Name, RollNo, Section, Stream, Email;
    private boolean State, absent_state;

    public student_model() {
    }

    public student_model(String branch, String name, String rollNo, String section, String stream, String email, boolean state, boolean absent_state) {
        Branch = branch;
        Name = name;
        RollNo = rollNo;
        Section = section;
        Stream = stream;
        Email = email;
        State = state;
        this.absent_state = absent_state;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getStream() {
        return Stream;
    }

    public void setStream(String stream) {
        Stream = stream;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isState() {
        return State;
    }

    public void setState(boolean state) {
        State = state;
    }

    public boolean isAbsent_state() {
        return absent_state;
    }

    public void setAbsent_state(boolean absent_state) {
        this.absent_state = absent_state;
    }
}
