package com.example.project2android.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyCheck implements Parcelable {
    private final Date testDate;
    private final long healthStatus;


    public DailyCheck(Date testDate, long healthStatus) {
        this.testDate = testDate;
        this.healthStatus = healthStatus;
    }

    public Date getDate() {
        return this.testDate;
    }

    public int getHealthStatus() {
        return (int) this.healthStatus;
    }


    public String getStringHealthStatus() {
        String health = "No Test";
        if (healthStatus == 0) {
            health = "Negative";
        } else if (healthStatus == 1) {
            health = "Symptoms";
        } else if (healthStatus == 2) {
            health = "Positive";
        } else if (healthStatus == 3) {
            health = "Invalid";
        }
        return health;
    }

    public String getStringDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(this.testDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.healthStatus);
        parcel.writeSerializable(this.testDate);
    }

    protected DailyCheck(Parcel in) {
        this.healthStatus = in.readLong();
        this.testDate = (Date) in.readSerializable();
    }

    public static final Parcelable.Creator<DailyCheck> CREATOR = new Parcelable.Creator<DailyCheck>() {
        @Override
        public DailyCheck createFromParcel(Parcel parcel) {
            return new DailyCheck(parcel);
        }

        @Override
        public DailyCheck[] newArray(int i) {
            return new DailyCheck[i];
        }
    };
}
