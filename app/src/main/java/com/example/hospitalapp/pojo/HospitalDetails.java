package com.example.hospitalapp.pojo;

import java.io.Serializable;

public class HospitalDetails implements Serializable {

    private String hospitalID;

    private String image;
    private String name;
    private String registration;
    private String area;
    private String city;
    private String road;
    private String house;
    private String history;
    private String phone;
    private String email;
    private String password;

    public HospitalDetails() {
    }

    public HospitalDetails(String hospitalID, String image, String name, String registration, String area, String city, String road, String house, String history, String phone, String email, String password) {
        this.hospitalID = hospitalID;
        this.image = image;
        this.name = name;
        this.registration = registration;
        this.area = area;
        this.city = city;
        this.road = road;
        this.house = house;
        this.history = history;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
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

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
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

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
