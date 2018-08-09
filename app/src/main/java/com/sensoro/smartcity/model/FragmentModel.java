package com.sensoro.smartcity.model;

import android.support.v4.app.Fragment;

import java.io.Serializable;

public class FragmentModel {
    public final Fragment fragment;
    public final Serializable data;
    public final MenuPageInfo menuPageInfo;

    public FragmentModel(MenuPageInfo menuPageInfo, Serializable data, Fragment fragment) {
        this.menuPageInfo = menuPageInfo;
        this.data = data;
        this.fragment = fragment;
    }
}
