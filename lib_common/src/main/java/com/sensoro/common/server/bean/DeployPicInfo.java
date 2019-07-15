package com.sensoro.common.server.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sensoro.common.model.ImageItem;

import java.io.Serializable;

public class DeployPicInfo implements Serializable, Parcelable {
    public String title;
    public String description;
    public String imgUrl;

    @Expose(serialize = false, deserialize = false)
    public ImageItem photoItem;

    public Boolean isRequired;

    public DeployPicInfo() {
    }

    public DeployPicInfo copy() {
        DeployPicInfo deployPicInfo = new DeployPicInfo();
        deployPicInfo.title = this.title;
        deployPicInfo.description = this.description;
        deployPicInfo.isRequired = this.isRequired;
        deployPicInfo.imgUrl = this.imgUrl;

        return deployPicInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.imgUrl);
        dest.writeParcelable(this.photoItem, flags);
        dest.writeValue(this.isRequired);
    }

    protected DeployPicInfo(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.imgUrl = in.readString();
        this.photoItem = in.readParcelable(ImageItem.class.getClassLoader());
        this.isRequired = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<DeployPicInfo> CREATOR = new Parcelable.Creator<DeployPicInfo>() {
        @Override
        public DeployPicInfo createFromParcel(Parcel source) {
            return new DeployPicInfo(source);
        }

        @Override
        public DeployPicInfo[] newArray(int size) {
            return new DeployPicInfo[size];
        }
    };
}
