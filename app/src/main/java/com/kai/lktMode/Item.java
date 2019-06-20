package com.kai.lktMode;

public class Item {
    private String title;
    private String subtitle;
    public Item(String title,String subtitle){
        this.title=title;
        this.subtitle=subtitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
