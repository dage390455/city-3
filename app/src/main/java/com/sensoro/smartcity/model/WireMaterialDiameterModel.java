package com.sensoro.smartcity.model;

import java.io.Serializable;

public class WireMaterialDiameterModel implements Serializable {
    //  1代表铜芯 2代表铝芯
    public int material;
    //线径
    public String diameter;
    // 数量
    public int count;

    public boolean isSelected;

    public WireMaterialDiameterModel(int material, String diameter, int count) {
        this.material = material;
        this.diameter = diameter;
        this.count = count;
    }
}
