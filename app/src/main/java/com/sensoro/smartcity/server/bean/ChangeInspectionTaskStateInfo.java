package com.sensoro.smartcity.server.bean;

public class ChangeInspectionTaskStateInfo {

    private int ok;
    private int nModified;
    private int n;
    private String lastOp;
    private String electionId;
    private int status;

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getNModified() {
        return nModified;
    }

    public void setNModified(int nModified) {
        this.nModified = nModified;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getLastOp() {
        return lastOp;
    }

    public void setLastOp(String lastOp) {
        this.lastOp = lastOp;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
