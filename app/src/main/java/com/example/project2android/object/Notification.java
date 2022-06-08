package com.example.project2android.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification implements Parcelable {
    private String id;
    private String title;
    private Date time;
    private String item;


    public Notification(String id, String title, String item, Date time) {
        this.item = item;
        this.time = time;
        this.title = title;
        this.id = id;
    }

    public String getItem() {
        return this.item;
    }

    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(this.time);
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.item);
        parcel.writeString(this.title);
        parcel.writeString(this.id);
        parcel.writeSerializable(this.time);
    }

    protected Notification(Parcel in) {
        this.item = in.readString();
        this.id = in.readString();
        this.title = in.readString();
        this.time = (Date) in.readSerializable();
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel parcel) {
            return new Notification(parcel);
        }

        @Override
        public Notification[] newArray(int i) {
            return new Notification[i];
        }
    };
}
