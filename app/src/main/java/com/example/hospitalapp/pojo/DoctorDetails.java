package com.example.hospitalapp.pojo;

import java.io.Serializable;
import java.util.List;

public class DoctorDetails implements Serializable {

    private String doctorID;

    private String image;
    private String name;
    private String degree;
    private String speciality;
    private String registrationNumber;

    private List<String> dateList;

    private String startTime;
    private String endTime;
    private String patientCount;
    private String block;
    private String floor;
    private String room;
    private String fees;
    private String phone;

    public DoctorDetails() {
    }

    public DoctorDetails(String doctorID, String image, String name, String degree, String speciality, String registrationNumber, List<String> dateList, String startTime, String endTime, String patientCount, String block, String floor, String room, String fees, String phone) {
        this.doctorID = doctorID;
        this.image = image;
        this.name = name;
        this.degree = degree;
        this.speciality = speciality;
        this.registrationNumber = registrationNumber;
        this.dateList = dateList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.patientCount = patientCount;
        this.block = block;
        this.floor = floor;
        this.room = room;
        this.fees = fees;
        this.phone = phone;
    }

    public DoctorDetails(String image, String name, String degree, String speciality, String registrationNumber, List<String> dateList, String startTime, String endTime, String patientCount, String block, String floor, String room, String fees, String phone) {
        this.image = image;
        this.name = name;
        this.degree = degree;
        this.speciality = speciality;
        this.registrationNumber = registrationNumber;
        this.dateList = dateList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.patientCount = patientCount;
        this.block = block;
        this.floor = floor;
        this.room = room;
        this.fees = fees;
        this.phone = phone;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(String patientCount) {
        this.patientCount = patientCount;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}