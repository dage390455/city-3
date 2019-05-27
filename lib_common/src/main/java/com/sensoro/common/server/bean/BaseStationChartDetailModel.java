package com.sensoro.common.server.bean;

public class BaseStationChartDetailModel {

    private float key;

    public float getKey() {
        return key;
    }

    public void setKey(float key) {
        this.key = key;
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


}
