package com.sensoro.common.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public final class ArouterIntentModel implements Parcelable {
    public String url;
    public Intent intent;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.intent, flags);
    }

    public ArouterIntentModel() {
    }

    protected ArouterIntentModel(Parcel in) {
        this.url = in.readString();
        this.intent = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Parcelable.Creator<ArouterIntentModel> CREATOR = new Parcelable.Creator<ArouterIntentModel>() {
        @Override
        public ArouterIntentModel createFromParcel(Parcel source) {
            return new ArouterIntentModel(source);
        }

        @Override
        public ArouterIntentModel[] newArray(int size) {
            return new ArouterIntentModel[size];
        }
    };
}
