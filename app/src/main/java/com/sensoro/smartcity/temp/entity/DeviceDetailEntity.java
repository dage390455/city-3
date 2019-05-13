package com.sensoro.smartcity.temp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DeviceDetailEntity implements Parcelable {

    /**
     * cameraOrientation : 0
     * cid : 538380580
     * deviceName : 新建水电路智慧树幼儿园旁
     * deviceType : 100603
     * id : 72057600538380580
     * inOutDirection : 0
     * industry1 : 0
     * industry2 : 0
     * installationLocationDetail : 江西省南昌市新建区长堎镇智慧树幼儿园(水电路)新建区竞晖学校
     * installationMethod : 0
     * installationSite :
     * latitude : 28.687496623554157
     * longitude : 115.81558252926375
     * lyGroupId : 5b4401660021967e35d88d13
     * lyGroupName :
     * maintenancePhone :
     * sn : 137898627707
     * deviceStatus: 0
     * playUrl :
     * coverUrl :
     * pathId : ["119102"]
     */
    @SerializedName(value = "cameraOrientation")
    private int cameraOrientation;//设备朝向
    @SerializedName(value = "cid")
    private int cid;//设备羚羊云id
    @SerializedName(value = "deviceName")
    private String deviceName;//设备名称
    @SerializedName(value = "deviceType")
    private int deviceType;//设备类型
    @SerializedName(value = "deviceStatus")
    private String deviceStatus;//设备在线状态：0离线；1在线
    @SerializedName(value = "id")
    private long id;//设备id
    @SerializedName(value = "inOutDirection")
    private int inOutDirection;//设备进出方向
    @SerializedName(value = "industry1")
    private int industry1;//所属行业
    @SerializedName(value = "industry2")
    private int industry2;//建设单位所属行业
    @SerializedName(value = "installationLocationDetail")
    private String installationLocationDetail;//设备安装地址
    @SerializedName(value = "installationMethod")
    private int installationMethod;//安装方式
    @SerializedName(value = "installationSite")
    private String installationSite;//设备场所
    @SerializedName(value = "latitude")
    private double latitude;//纬度
    @SerializedName(value = "longitude")
    private double longitude;//经度
    @SerializedName(value = "lyGroupId")
    private String lyGroupId;//设备分组id
    @SerializedName(value = "lyGroupName")
    private String lyGroupName;//设备分组名称
    @SerializedName(value = "maintenancePhone")
    private String maintenancePhone;//设备联系人电话
    @SerializedName(value = "sn")
    private String sn;//设备sn码
    @SerializedName(value = "playUrl")
    private String playUrl;// 视频直播url
    @SerializedName(value = "coverUrl")
    private String coverUrl;// 封面地址
    @SerializedName(value = "pathId")
    private List<String> pathId; // 设备场所(新)

    public int getCameraOrientation() {
        return cameraOrientation;
    }

    public void setCameraOrientation(int cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getInOutDirection() {
        return inOutDirection;
    }

    public void setInOutDirection(int inOutDirection) {
        this.inOutDirection = inOutDirection;
    }

    public int getIndustry1() {
        return industry1;
    }

    public void setIndustry1(int industry1) {
        this.industry1 = industry1;
    }

    public int getIndustry2() {
        return industry2;
    }

    public void setIndustry2(int industry2) {
        this.industry2 = industry2;
    }

    public String getInstallationLocationDetail() {
        return installationLocationDetail;
    }

    public void setInstallationLocationDetail(String installationLocationDetail) {
        this.installationLocationDetail = installationLocationDetail;
    }

    public int getInstallationMethod() {
        return installationMethod;
    }

    public void setInstallationMethod(int installationMethod) {
        this.installationMethod = installationMethod;
    }

    public String getInstallationSite() {
        return installationSite;
    }

    public void setInstallationSite(String installationSite) {
        this.installationSite = installationSite;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLyGroupId() {
        return lyGroupId;
    }

    public void setLyGroupId(String lyGroupId) {
        this.lyGroupId = lyGroupId;
    }

    public String getLyGroupName() {
        return lyGroupName;
    }

    public void setLyGroupName(String lyGroupName) {
        this.lyGroupName = lyGroupName;
    }

    public String getMaintenancePhone() {
        return maintenancePhone;
    }

    public void setMaintenancePhone(String maintenancePhone) {
        this.maintenancePhone = maintenancePhone;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getPathId() {
        return pathId;
    }

    public void setPathId(List<String> pathId) {
        this.pathId = pathId;
    }

    public DeviceDetailEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cameraOrientation);
        dest.writeInt(this.cid);
        dest.writeString(this.deviceName);
        dest.writeInt(this.deviceType);
        dest.writeString(this.deviceStatus);
        dest.writeLong(this.id);
        dest.writeInt(this.inOutDirection);
        dest.writeInt(this.industry1);
        dest.writeInt(this.industry2);
        dest.writeString(this.installationLocationDetail);
        dest.writeInt(this.installationMethod);
        dest.writeString(this.installationSite);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.lyGroupId);
        dest.writeString(this.lyGroupName);
        dest.writeString(this.maintenancePhone);
        dest.writeString(this.sn);
        dest.writeString(this.playUrl);
        dest.writeString(this.coverUrl);
        dest.writeStringList(this.pathId);
    }

    protected DeviceDetailEntity(Parcel in) {
        this.cameraOrientation = in.readInt();
        this.cid = in.readInt();
        this.deviceName = in.readString();
        this.deviceType = in.readInt();
        this.deviceStatus = in.readString();
        this.id = in.readLong();
        this.inOutDirection = in.readInt();
        this.industry1 = in.readInt();
        this.industry2 = in.readInt();
        this.installationLocationDetail = in.readString();
        this.installationMethod = in.readInt();
        this.installationSite = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.lyGroupId = in.readString();
        this.lyGroupName = in.readString();
        this.maintenancePhone = in.readString();
        this.sn = in.readString();
        this.playUrl = in.readString();
        this.coverUrl = in.readString();
        this.pathId = in.createStringArrayList();
    }

    public static final Creator<DeviceDetailEntity> CREATOR = new Creator<DeviceDetailEntity>() {
        @Override
        public DeviceDetailEntity createFromParcel(Parcel source) {
            return new DeviceDetailEntity(source);
        }

        @Override
        public DeviceDetailEntity[] newArray(int size) {
            return new DeviceDetailEntity[size];
        }
    };
}
