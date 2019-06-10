package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class GrantsInfo implements Serializable {


    private List<String> station;
    private List<String> contract;
    private List<String> tv;
    private List<String> inspectTemplate;
    private List<String> inspectTask;
    private List<String> inspectDevice;
    private List<String> deploy;
    private List<String> groupNotice;
    private List<String> deviceGroup;
    private List<String> indoorMap;
    private List<String> account;
    private List<String> user;
    private List<String> task;
    private List<String> notification;
    private List<String> grant;
    private List<String> deviceLog;
    private List<String> alarm;
    private List<String> device;
    private List<String> monitor;
    private List<String> malfunction;
    private List<String> control;
    private List<String> camera;
    private List<?> sysLog;


    public List<String> getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(List<String> malfunction) {
        this.malfunction = malfunction;
    }

    public List<String> getStation() {
        return station;
    }

    public void setStation(List<String> station) {
        this.station = station;
    }

    public List<String> getContract() {
        return contract;
    }

    public void setContract(List<String> contract) {
        this.contract = contract;
    }

    public List<String> getTv() {
        return tv;
    }

    public void setTv(List<String> tv) {
        this.tv = tv;
    }

    public List<String> getInspectTemplate() {
        return inspectTemplate;
    }

    public void setInspectTemplate(List<String> inspectTemplate) {
        this.inspectTemplate = inspectTemplate;
    }

    public List<String> getInspectTask() {
        return inspectTask;
    }

    public void setInspectTask(List<String> inspectTask) {
        this.inspectTask = inspectTask;
    }

    public List<String> getInspectDevice() {
        return inspectDevice;
    }

    public void setInspectDevice(List<String> inspectDevice) {
        this.inspectDevice = inspectDevice;
    }

    public List<String> getDeploy() {
        return deploy;
    }

    public void setDeploy(List<String> deploy) {
        this.deploy = deploy;
    }

    public List<String> getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(List<String> groupNotice) {
        this.groupNotice = groupNotice;
    }

    public List<String> getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(List<String> deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

    public List<String> getIndoorMap() {
        return indoorMap;
    }

    public void setIndoorMap(List<String> indoorMap) {
        this.indoorMap = indoorMap;
    }

    public List<String> getAccount() {
        return account;
    }

    public void setAccount(List<String> account) {
        this.account = account;
    }

    public List<String> getUser() {
        return user;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public List<String> getTask() {
        return task;
    }

    public void setTask(List<String> task) {
        this.task = task;
    }

    public List<String> getNotification() {
        return notification;
    }

    public void setNotification(List<String> notification) {
        this.notification = notification;
    }

    public List<String> getGrant() {
        return grant;
    }

    public void setGrant(List<String> grant) {
        this.grant = grant;
    }

    public List<String> getDeviceLog() {
        return deviceLog;
    }

    public void setDeviceLog(List<String> deviceLog) {
        this.deviceLog = deviceLog;
    }

    public List<String> getAlarm() {
        return alarm;
    }

    public void setAlarm(List<String> alarm) {
        this.alarm = alarm;
    }

    public List<String> getDevice() {
        return device;
    }

    public void setDevice(List<String> device) {
        this.device = device;
    }

    public List<String> getMonitor() {
        return monitor;
    }

    public void setMonitor(List<String> monitor) {
        this.monitor = monitor;
    }

    public List<?> getSysLog() {
        return sysLog;
    }

    public void setSysLog(List<?> sysLog) {
        this.sysLog = sysLog;
    }

    public List<String> getControl() {
        return control;
    }

    public void setControl(List<String> control) {
        this.control = control;
    }

    public List<String> getCamera() {
        return camera;
    }

    public void setCamera(List<String> camera) {
        this.camera = camera;
    }
}
