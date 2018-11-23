package com.sensoro.smartcity.constant;

public interface SearchHistoryTypeConstants {
    //搜索历史记录type
    int TYPE_SEARCH_HISTORY_WARN = 0x01;
    int TYPE_SEARCH_HISTORY_MALFUNCTION = 0x02;
    int TYPE_SEARCH_HISTORY_INSPECTION = 0x03;

    String SEARCH_HISTORY_KEY = "search_history_key";

    String SP_FILE_WARN = "search_history_warn";
    String SP_FILE_MALFUNCTION = "search_history_malfunction";
    String SP_FILE_INSPECTION = "search_history_inspection";
}
