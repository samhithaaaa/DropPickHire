package com.avinash.droppickhire.pojo;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

    private String id;

    private String location;

    private String title;

    private Double latitude;

    private Double longitude;

    private String date;

    private String time;

    private List<String> degrees;

    private List<String> majors;

    private List<String> skills;

    private String noOfSubmissions;

    private String creatorID;

    private List<String> experience;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNoOfSubmissions() {
        return noOfSubmissions;
    }

    public void setNoOfSubmissions(String noOfSubmissions) {
        this.noOfSubmissions = noOfSubmissions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<String> degrees) {
        this.degrees = degrees;
    }

    public List<String> getMajors() {
        return majors;
    }

    public void setMajors(List<String> majors) {
        this.majors = majors;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }


    @Override
    public String toString() {
        return title + " - " + id + " - " + latitude + " - " + longitude +  " - " + location + " - " + date + " - " + time + " - " + noOfSubmissions + " - " + creatorID;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public List<String> getExperience() {
        return experience;
    }

    public void setExperience(List<String> experience) {
        this.experience = experience;
    }
}
