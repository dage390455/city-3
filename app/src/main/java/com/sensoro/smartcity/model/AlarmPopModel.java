package com.sensoro.smartcity.model;


import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;

public class AlarmPopModel implements Serializable {
    public int requestCode;
    public int resultCode;
    public boolean fromTakePhoto;
    public ArrayList<ImageItem> imageItems;
}
