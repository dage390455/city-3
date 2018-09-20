package com.sensoro.smartcity.model;

import java.util.Objects;

public class HomeTopModel {
    public int type;
    public int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeTopModel that = (HomeTopModel) o;
        return type == that.type &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
