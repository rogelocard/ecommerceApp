package com.example.conceptsandroidstudio;

public class MyItem {
    private int imageResource;
    private String title;

    public MyItem(int imageResource, String title) {
        this.imageResource = imageResource;
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }
}
