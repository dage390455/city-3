package com.sensoro.smartcity.model;

import com.github.mikephil.charting.data.Entry;

public class CityEntry extends Entry {
    private int index;

    public CityEntry(int index, float x, float y) {
        super(x, y);
        this.index = index;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
