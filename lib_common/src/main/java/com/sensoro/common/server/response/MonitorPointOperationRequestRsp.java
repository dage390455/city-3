package com.sensoro.common.server.response;

import java.util.List;

public class MonitorPointOperationRequestRsp extends ResponseBase {

    /**
     * resultOk : 1
     * resultNotSupport : [null]
     * resultNotExist : []
     * scheduleNo : ED-20181123-163404
     */

    private int resultOk;
    private String scheduleNo;
    private List<String> resultNotSupport;
    private List<String> resultNotExist;

    public int getResultOk() {
        return resultOk;
    }

    public void setResultOk(int resultOk) {
        this.resultOk = resultOk;
    }

    public String getScheduleNo() {
        return scheduleNo;
    }

    public void setScheduleNo(String scheduleNo) {
        this.scheduleNo = scheduleNo;
    }

    public List<String> getResultNotSupport() {
        return resultNotSupport;
    }

    public void setResultNotSupport(List<String> resultNotSupport) {
        this.resultNotSupport = resultNotSupport;
    }

    public List<String> getResultNotExist() {
        return resultNotExist;
    }

    public void setResultNotExist(List<String> resultNotExist) {
        this.resultNotExist = resultNotExist;
    }
}
