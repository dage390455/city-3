package com.applikeysolutions.cosmocalendar.listeners;

import android.support.v4.util.Pair;

import com.applikeysolutions.cosmocalendar.model.Day;

/**
 * Created by sensoro on 17/12/7.
 */

public interface OnDayRangeSelectedListener {

    void onDayRangeSelected(Pair<Day, Day> days);
}
