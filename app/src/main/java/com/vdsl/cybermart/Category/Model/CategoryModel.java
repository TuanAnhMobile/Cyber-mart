package com.vdsl.cybermart.Category.Model;

public class CategoryModel {
    private String id;
    private String title;
    private String image;
    private boolean status;

    public String getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

    public CategoryModel() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryModel(String id, String title, String image, boolean status) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.status = status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
