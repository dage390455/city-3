package com.sensoro.common.model;

import java.util.ArrayList;
import java.util.List;

public class StatusCountModel {
    public int status;
    public String statusTitle;
    public int count;
    public boolean isMutilSelect;
    public boolean isSelect;

    public List<StatusCountModel> list = new ArrayList<>();
}
