package com.example.home_safer.model;

import android.graphics.Bitmap;

public class TimeLineModel {
    private String name;
    private int age;
    private Bitmap bitmap=null;//这是视频中的某一帧
    public TimeLineModel(){}
    public TimeLineModel(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public TimeLineModel(String name, Bitmap bitmap) {
        this.name = name;
        this.age = -1;
        this.bitmap=bitmap;
    }

    public TimeLineModel(String name, int age, Bitmap bitmap) {
        this.name = name;
        this.age = age;
        this.bitmap=bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap=bitmap;
    }


    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }
}
