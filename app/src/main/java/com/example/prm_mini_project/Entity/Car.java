package com.example.prm_mini_project.Entity;

public class Car {
    private String name;
    private int imageResourceId;
    private int position;
    private int earnings;

    public Car(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.position = 0;
        this.earnings = 0;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getEarnings() {
        return earnings;
    }

    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }
}