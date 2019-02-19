package com.sensoro.smartcity.adapter.model;

import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

public class DeployPicModel {
    public String title;
    public String content;
    public String exampleUrl;
    public ImageItem photoItem;

    public DeployPicModel(String title, String content, String exampleUrl, ImageItem photoItem) {
        this.title = title;
        this.content = content;
        this.exampleUrl = exampleUrl;
        this.photoItem = photoItem;
    }
}
