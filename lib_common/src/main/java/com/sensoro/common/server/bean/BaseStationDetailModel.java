package com.sensoro.common.server.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BaseStationDetailModel {

    private String appId;
    private String users;
    private String sn;
    private String name;

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    private String updatedTime;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public NetWork getNetwork() {
        return network;
    }

    public void setNetwork(NetWork network) {
        this.network = network;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }


    public ArrayList<String> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<String> channels) {
        this.channels = channels;
    }

    public ArrayList<String> getSelftest() {
        return selftest;
    }

    public void setSelftest(ArrayList<String> selftest) {
        this.selftest = selftest;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    private String hardwareVersion;
    private String type;
    private String status;
    private String firmwareVersion;
    private NetWork network;
    private ArrayList<String> tags;
    private ArrayList<Double> lonlatLabel;
    private ArrayList<String> channels;
    private ArrayList<String> selftest;
    private ArrayList<String> images;

    public ArrayList<NetDelay> getDataMessage() {
        return dataMessage;
    }

    public void setDataMessage(ArrayList<NetDelay> dataMessage) {
        this.dataMessage = dataMessage;
    }

    private ArrayList<NetDelay> dataMessage;

    public ArrayList<Double> getLonlatLabel() {
        return lonlatLabel;
    }

    public void setLonlatLabel(ArrayList<Double> lonlatLabel) {
        this.lonlatLabel = lonlatLabel;
    }

    public static class NetDelay implements Parcelable {

        private String timeout;

        public String getTimeout() {
            return timeout;
        }

        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        private int interval;
        private int time;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.timeout);
            dest.writeInt(this.interval);
            dest.writeInt(this.time);
        }

        public NetDelay() {
        }

        protected NetDelay(Parcel in) {
            this.timeout = in.readString();
            this.interval = in.readInt();
            this.time = in.readInt();
        }

        public static final Parcelable.Creator<NetDelay> CREATOR = new Parcelable.Creator<NetDelay>() {
            @Override
            public NetDelay createFromParcel(Parcel source) {
                return new NetDelay(source);
            }

            @Override
            public NetDelay[] newArray(int size) {
                return new NetDelay[size];
            }
        };
    }


    public static class NetWork implements Parcelable {

        private String adns;

        public String getAdns() {
            return adns;
        }

        public void setAdns(String adns) {
            this.adns = adns;
        }

        public String getPdns() {
            return pdns;
        }

        public void setPdns(String pdns) {
            this.pdns = pdns;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getGw() {
            return gw;
        }

        public void setGw(String gw) {
            this.gw = gw;
        }

        public String getNmask() {
            return nmask;
        }

        public void setNmask(String nmask) {
            this.nmask = nmask;
        }

        private String pdns;
        private String ip;
        private String gw;
        private String nmask;
        private String acm;


//    network

        public NetWork() {
        }

        public String getAcm() {
            return acm;
        }

        public void setAcm(String acm) {
            this.acm = acm;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.adns);
            dest.writeString(this.pdns);
            dest.writeString(this.ip);
            dest.writeString(this.gw);
            dest.writeString(this.nmask);
            dest.writeString(this.acm);
        }

        protected NetWork(Parcel in) {
            this.adns = in.readString();
            this.pdns = in.readString();
            this.ip = in.readString();
            this.gw = in.readString();
            this.nmask = in.readString();
            this.acm = in.readString();
        }

        public static final Creator<NetWork> CREATOR = new Creator<NetWork>() {
            @Override
            public NetWork createFromParcel(Parcel source) {
                return new NetWork(source);
            }

            @Override
            public NetWork[] newArray(int size) {
                return new NetWork[size];
            }
        };
    }
//    network

}
