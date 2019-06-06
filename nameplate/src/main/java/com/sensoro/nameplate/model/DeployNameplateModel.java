package com.sensoro.nameplate.model;

import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.bean.NamePlateInfo;

import java.util.ArrayList;
import java.util.List;

public class DeployNameplateModel {
    public String name;
    public List<String> tags = new ArrayList<>();
    public List<ImageItem> deployPics = new ArrayList<>(1);
    public ArrayList<NamePlateInfo> bindList;

}
