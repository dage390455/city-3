package com.sensoro.common.model;

import java.util.List;

public class PermissionChangeSocketModel {
    private List<String> accountIds;

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }
}
