package by.gsu.bal.curse.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Scooter implements Parcelable {

    public static final Creator<Scooter> CREATOR = new Creator<Scooter>() {
        @Override
        public Scooter createFromParcel(Parcel in) {
            return new Scooter(in);
        }

        @Override
        public Scooter[] newArray(int size) {
            return new Scooter[size];
        }
    };
    private String code;
    private Long charge;
    private String modelName;
    private String status;
    private String stationId;

    protected Scooter(Parcel in) {
        code = in.readString();
        if (in.readByte() == 0) {charge = null;} else {charge = in.readLong();}
        modelName = in.readString();
        status = in.readString();
        stationId = in.readString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCharge() {
        return charge;
    }

    public void setCharge(Long charge) {
        this.charge = charge;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    @Override
    public String toString() {
        return "Scooter{" +
               "code='" +
               code +
               '\'' +
               ", charge=" +
               charge +
               ", modelName='" +
               modelName +
               '\'' +
               ", status='" +
               status +
               '\'' +
               ", stationId='" +
               stationId +
               '\'' +
               '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(code);
        if (charge == null) {dest.writeByte((byte) 0);} else {
            dest.writeByte((byte) 1);
            dest.writeLong(charge);
        }
        dest.writeString(modelName);
        dest.writeString(status);
        dest.writeString(stationId);
    }

    public Scooter() {
    }
}
