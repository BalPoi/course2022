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
    private Long id;
    private String city;
    private String name;
    private String address;
    private Double lat;
    private Double lon;
    private Long slotsNum;
    private Long freeSlots;
    private Long stationId;
    private Boolean isParked;
    private Boolean isBlocked;

    public Station() {
    }

    protected Station(Parcel in) {
        if (in.readByte() == 0) {id = null;} else {id = in.readLong();}
        city = in.readString();
        name = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {lat = null;} else {lat = in.readDouble();}
        if (in.readByte() == 0) {lon = null;} else {lon = in.readDouble();}
        if (in.readByte() == 0) {slotsNum = null;} else {slotsNum = in.readLong();}
        if (in.readByte() == 0) {freeSlots = null;} else {freeSlots = in.readLong();}
        if (in.readByte() == 0) {stationId = null;} else {stationId = in.readLong();}
        byte tmpIsParked = in.readByte();
        isParked = tmpIsParked == 0 ? null : tmpIsParked == 1;
        byte tmpIsBlocked = in.readByte();
        isBlocked = tmpIsBlocked == 0 ? null : tmpIsBlocked == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(city);
        dest.writeString(name);
        dest.writeString(address);
        if (lat == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lon == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lon);
        }
        if (slotsNum == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeLong(slotsNum);
        }
        if (freeSlots == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeLong(freeSlots);
        }
        if (stationId == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeLong(stationId);
        }
        dest.writeByte((byte) (isParked == null ? 0 : isParked ? 1 : 2));
        dest.writeByte((byte) (isBlocked == null ? 0 : isBlocked ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Long getSlotsNum() {
        return slotsNum;
    }

    public void setSlotsNum(Long slotsNum) {
        this.slotsNum = slotsNum;
    }

    public Long getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(Long freeSlots) {
        this.freeSlots = freeSlots;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Boolean getParked() {
        return isParked;
    }

    public void setParked(Boolean parked) {
        isParked = parked;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
}
