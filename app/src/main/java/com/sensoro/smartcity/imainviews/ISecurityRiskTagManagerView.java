package com.sensoro.smartcity.imainviews;


import com.sensoro.common.iwidget.IActivityIntent;

import java.util.ArrayList;

public interface ISecurityRiskTagManagerView  extends IActivityIntent {
    void updateLocationAdapter(ArrayList<String> locationData);

    void updateBehaviorAdapter(ArrayList<String> behaviorData);
}
