package com.sensoro.smartcity.constant;

public interface SearchHistoryTypeConstants {
    //搜索历史记录type
    int TYPE_SEARCH_HISTORY_WARN = 1;
    int TYPE_SEARCH_HISTORY_MALFUNCTION = 2;
    int TYPE_SEARCH_HISTORY_INSPECTION = 3;
    int TYPE_SEARCH_HISTORY_CONTRACT = 4;
    int TYPE_SEARCH_HISTORY_MERCHANT = 5;
    int TYPE_SEARCH_HISTORY_DEPLOY_RECORD = 6;
    int TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS = 7;
    int TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM = 8;
    int TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME = 9;
    int TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE = 10;
    int TYPE_SEARCH_HISTORY_DEPLOY_TAG = 11;
    int TYPE_SEARCH_CAMERALIST = 12;

    String SEARCH_HISTORY_KEY = "search_history_key";

    String SP_FILE_WARN = "search_history_warn";
    String SP_FILE_SEARCH_CAMERALIST = "search_search_cameralist";
    String SP_FILE_MALFUNCTION = "search_history_malfunction";
    String SP_FILE_INSPECTION = "search_history_inspection";
    String SP_FILE_CONTRACT = "search_history_contract";
    String SP_FILE_MERCHANT= "search_history_merchant";
    String SP_FILE_DEPLOY_RECORD= "search_history_deploy_record";


}
