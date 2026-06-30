package com.example.csc325_firebase_webview_auth.model;

public class Person {
    private String id;
    private String name;
    private String major;
    private int age;
    private String imageUrl;

    public Person(String id, String name, String major, int age) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.age = age;
        this.imageUrl = "";
    }

    public Person(String id, String name, String major, int age, String imageUrl) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.age = age;
        this.imageUrl = imageUrl == null ? "" : imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
