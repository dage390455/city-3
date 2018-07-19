package com.sensoro.smartcity.model;

public class MenuPageInfo {
    public final int menuIconResId;
    public final int menuPageId;
    public final int pageTitleId;
    //
    public static final int MENU_PAGE_INDEX = 0x101;
    public static final int MENU_PAGE_ALARM = 0x102;
    public static final int MENU_PAGE_MERCHANT = 0x103;
    public static final int MENU_PAGE_POINT = 0x104;
    public static final int MENU_PAGE_STATION = 0x105;
    public static final int MENU_PAGE_CONTRACT = 0x106;

    public MenuPageInfo(int titleId, int iconId, int pageId) {
        pageTitleId = titleId;
        menuIconResId = iconId;
        menuPageId = pageId;
    }
}
