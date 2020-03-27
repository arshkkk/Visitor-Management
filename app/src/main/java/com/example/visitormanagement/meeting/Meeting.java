package com.example.visitormanagement.meeting;

public class Meeting {
    String hostId, visitorId, meetingId, time,date;

    public Meeting() {
    }

    public Meeting(String hostId, String visitorId, String meetingId, String time, String date) {
        this.hostId = hostId;
        this.visitorId = visitorId;
        this.meetingId = meetingId;
        this.time = time;
        this.date = date;
    }



    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
