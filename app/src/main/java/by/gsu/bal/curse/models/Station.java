package by.gsu.bal.curse.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
    private final Long id;
    private final String city;
    private final String name;
    private final String address;
    private final Double lat;
    private final Double lon;
    private final Long slotsNum;
    private final Long freeSlots;

    protected Station(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        city = in.readString();
        name = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lon = null;
        } else {
            lon = in.readDouble();
        }
        if (in.readByte() == 0) {
            slotsNum = null;
        } else {
            slotsNum = in.readLong();
        }
        if (in.readByte() == 0) {
            freeSlots = null;
        } else {
            freeSlots = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(city);
        dest.writeString(name);
        dest.writeString(address);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lon == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lon);
        }
        if (slotsNum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(slotsNum);
        }
        if (freeSlots == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(freeSlots);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
