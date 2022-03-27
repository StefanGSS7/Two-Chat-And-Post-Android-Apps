package com.main.beyond.TiO;

public class Messages {

    private String Date,From,Message,Time,Type,IsSeen;

    public  Messages (){



    }

    public Messages(String date, String from, String message, String time, String type ,String isSeen) {
        Date = date;
        From = from;
        Message = message;
        Time = time;
        Type = type;
        IsSeen = isSeen;
    }



    public String getDate() { return Date; }

    public void setDate(String date) {
        Date = date;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getIsSeen() {
        return IsSeen;
    }

    public void setIsSeen(String isSeen) { IsSeen = isSeen; }
}
