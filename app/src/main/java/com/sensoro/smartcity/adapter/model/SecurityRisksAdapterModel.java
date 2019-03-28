package com.sensoro.smartcity.adapter.model;

import com.google.gson.annotations.Expose;
import com.sensoro.smartcity.R;

import java.io.Serializable;
import java.util.ArrayList;

public class SecurityRisksAdapterModel implements Serializable {
    @Expose(serialize = false, deserialize = false)
    public int locationColor = R.color.c_a6a6a6;
    @Expose(serialize = false, deserialize = false)
    public int behaviorColor = R.color.c_a6a6a6;
    //
    public String place;
    public ArrayList<String> action = new ArrayList<>();
}
