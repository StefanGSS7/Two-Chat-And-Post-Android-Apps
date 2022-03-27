package com.main.beyond.TiO;

public class Posts {

    private String Content,Date,FirstName,LastName,Category,Time,uid,PostName,EKSTRA,HasHelper;

    public Posts(){
    }

    public Posts(String content, String date, String FirstName, String lastName, String time, String uid, String category, String postname , String ekstra, String hasHelper) {
        Content = content;
        Date = date;
        FirstName = FirstName;

        Category = category;
        Time = time;
        uid = uid;
        PostName = postname;
        EKSTRA = ekstra;
        HasHelper = hasHelper;
    }

    public String getHasHelper() {
        return HasHelper;
    }

    public void setHasHelper(String hasHelper) {
        EKSTRA = hasHelper;
    }

    public String getEkstra() {
        return EKSTRA;
    }

    public void setEkstra(String eksTra) {
        EKSTRA = eksTra;
    }


    public String getPostName() {
        return PostName;
    }

    public void setPostName(String postname) {
        Content = postname;
    }


    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        FirstName = FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
