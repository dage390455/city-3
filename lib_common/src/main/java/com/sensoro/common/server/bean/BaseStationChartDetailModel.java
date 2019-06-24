package com.sensoro.common.server.bean;

public class BaseStationChartDetailModel {

    private String key;

    public String getKey() {
        return key;
    }



    public float getShell() {
        return shell;
    }

    public void setShell(float shell) {
        this.shell = shell;
    }

    public float getBoard() {
        return board;
    }

    public void setBoard(float board) {
        this.board = board;
    }

    private float shell;
    private float board;


    public void setKey(String key) {
        this.key = key;
    }
}
