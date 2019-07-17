package com.sensoro.nameplate.model;

import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.common.server.bean.NamePlateInfo;

import java.util.ArrayList;

public class DeployNameplateModel {
    public String name;
    public ArrayList<String> tags = new ArrayList<>();
    public ArrayList<DeployPicInfo> deployPics = new ArrayList<>(1);
    public ArrayList<NamePlateInfo> bindList;

}
