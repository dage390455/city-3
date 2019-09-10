package com.sensoro.smartcity.factory;

import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.model.MerchantSubModel;

import java.util.ArrayList;
import java.util.List;

public class MerchantSubFactory {
    public static List<MerchantSubModel> createMerchantSubList(UserInfo userInfo) {
        final List<MerchantSubModel> data = new ArrayList<>();
        int depth = userInfo.getDepth();
        handleUserInfo(userInfo, depth, data);
        return data;
    }


    private static void handleUserInfo(UserInfo userInfo, int depth, List<MerchantSubModel> data) {
        UserInfo[] children = userInfo.getChildren();
        if (children != null && children.length > 0) {
            for (UserInfo userInfoInner : children) {
                MerchantSubModel merchantSubModel = new MerchantSubModel();
                merchantSubModel.level = userInfoInner.getDepth() - depth;
                merchantSubModel.name = userInfoInner.getNickname();
                merchantSubModel.userInfo = userInfoInner;
                data.add(merchantSubModel);
                handleUserInfo(userInfoInner, depth, data);
            }
        }
    }
}
