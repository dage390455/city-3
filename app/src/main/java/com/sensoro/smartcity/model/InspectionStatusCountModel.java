package com.sensoro.smartcity.model;

import java.util.ArrayList;
import java.util.List;

public class InspectionStatusCountModel {
    public int status;
    public String statusTitle;
    public int count;
    public boolean isMutilSelect;
    public boolean isSelect;

    public List<InspectionStatusCountModel> list = new ArrayList<>();
}
