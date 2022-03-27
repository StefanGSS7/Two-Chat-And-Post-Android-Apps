package com.main.beyond.TiO;

public class Comments {

    private String Comment,Date,Time,FirstName,LastName,uID;

    public Comments(){



    }


    public Comments(String comment, String date, String time, String firstName, String lastName , String uid) {
        Comment = comment;
        Date = date;
        Time = time;
        FirstName = firstName;
        LastName = lastName;
        uID = uid;

    }

    public String getUid() {
        return uID;
    }

    public void setUid(String uid) {
        uID = uid;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
