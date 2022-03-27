package com.main.beyond.TiO;

public class UserMessages {

    private String FirstName,LastName,uid;

    UserMessages(){}

    public UserMessages(String firstName, String lastName, String uid) {
        FirstName = firstName;
        LastName = lastName;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
