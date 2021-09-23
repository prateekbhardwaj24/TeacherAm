package com.teacher.TeacherAM;

public class StudentData {
    String Name,RollNo,Email;

    public StudentData() {
    }

    public StudentData(String name, String rollNo, String email) {
        Name = name;
        RollNo = rollNo;
        Email = email;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
