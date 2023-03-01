package com.example.hospitalapp.pojo;

import java.io.Serializable;

public class HospitalBooking implements Serializable {

    private String bookingID;
    private String hospitalID;
    private String doctorID;
    private String doctorName;
    private String patientID;
    private String bookingDate;
    private String patientFirstName;
    private String patientLastName;
    private String description;
    private String patientImage;
    private String hospitalName;
    private String doctorImage;
    private String doctorDegree;
    private String doctorSpeciality;
    private String time;
    private String area;
    private String city;
    private String room;
    private String floor;

    public HospitalBooking() {
    }

    public HospitalBooking(String bookingID, String hospitalID, String doctorID, String doctorName, String patientID, String bookingDate, String patientFirstName, String patientLastName, String description, String patientImage, String hospitalName, String doctorImage, String doctorDegree, String doctorSpeciality, String time, String area, String city, String room, String floor) {
        this.bookingID = bookingID;
        this.hospitalID = hospitalID;
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.patientID = patientID;
        this.bookingDate = bookingDate;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.description = description;
        this.patientImage = patientImage;
        this.hospitalName = hospitalName;
        this.doctorImage = doctorImage;
        this.doctorDegree = doctorDegree;
        this.doctorSpeciality = doctorSpeciality;
        this.time = time;
        this.area = area;
        this.city = city;
        this.room = room;
        this.floor = floor;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatientImage() {
        return patientImage;
    }

    public void setPatientImage(String patientImage) {
        this.patientImage = patientImage;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDoctorImage() {
        return doctorImage;
    }

    public void setDoctorImage(String doctorImage) {
        this.doctorImage = doctorImage;
    }

    public String getDoctorDegree() {
        return doctorDegree;
    }

    public void setDoctorDegree(String doctorDegree) {
        this.doctorDegree = doctorDegree;
    }

    public String getDoctorSpeciality() {
        return doctorSpeciality;
    }

    public void setDoctorSpeciality(String doctorSpeciality) {
        this.doctorSpeciality = doctorSpeciality;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
