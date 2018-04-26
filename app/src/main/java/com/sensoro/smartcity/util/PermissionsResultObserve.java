package com.sensoro.smartcity.util;

public interface PermissionsResultObserve {
    void onPermissionGranted();

    void onPermissionDenied();
}
