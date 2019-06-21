package com.sensoro.smartcity.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ThreePhaseElectDataModel implements Serializable {
    //输入值
    public Integer inputValue;
    //实际值
    public Integer actualRatedCurrent;
    //互感器值
    public RecommendedTransformerValueModel currentTransformerValue;
    //进线
    public ArrayList<WireMaterialDiameterModel> inLineList = new ArrayList<>();
    //出线
    public ArrayList<WireMaterialDiameterModel> outLineList = new ArrayList<>();
    //推荐值
    public ArrayList<RecommendedTransformerValueModel> transformerValueList = new ArrayList<>();
}
