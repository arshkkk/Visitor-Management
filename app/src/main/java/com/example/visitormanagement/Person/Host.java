package com.example.visitormanagement.Person;

public class Host {
    String firstName, lastName, visitorsCount, photo,phoneNumber, uid, email,fullName;

    public Host() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Host(String firstName, String lastName, String visitorsCount, String photo, String phoneNumber, String uid, String email, String fullName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.visitorsCount = visitorsCount;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getVisitorsCount() {
        return visitorsCount;
    }

    public void setVisitorsCount(String visitorsCount) {
        this.visitorsCount = visitorsCount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
