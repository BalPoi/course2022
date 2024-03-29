package by.gsu.bal.curse;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import by.gsu.bal.curse.models.Scooter;
import by.gsu.bal.curse.models.ScooterStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScooterService {
    private static final String TAG = "ScooterService";

    public static boolean isScooterAvailable(String scooterCode) {
        Task<DataSnapshot> status = DB.scootersRef.child(scooterCode).child("status").get();
        do {} while (!status.isComplete());
        Log.i(TAG, "isScooterAvailable: scooter status=" + status.getResult().toString());
        return Objects.equals(status.getResult().getValue(String.class), ScooterStatus.FREE.toString());
    }

    public static void updateScooterStatus(Scooter scooter, ScooterStatus status) {
        DB.scootersRef.child(scooter.getCode()).child("status").setValue(status.toString());
        if (status.equals(ScooterStatus.BUSY)) {
            incrementFreeSlots(scooter.getStationId());
        }
    }

    public static void incrementFreeSlots(String stationId) {
        Task<DataSnapshot> freeSlots = DB.stationsRef.child(stationId).child("freeSlots").get();
        do {} while (!freeSlots.isComplete());
        DB.stationsRef.child(stationId).child("freeSlots").setValue(freeSlots.getResult().getValue(Integer.class) + 1);
    }


    public static void unlockScooter(Scooter scooter) {
        DB.scootersRef.child(scooter.getCode()).child("isBlocked").setValue(false);
        Log.i(TAG, "unlockScooter: scooter " + scooter.getCode() + " has been blocked.");
    }

    public static void lockScooter(Scooter scooter) {
        DB.scootersRef.child(scooter.getCode()).child("isBlocked").setValue(true);
        Log.i(TAG, "lockScooter: scooter " + scooter.getCode() + " has been blocked.");
    }

    public static Boolean isScooterParked(Scooter scooter) {
        Task<DataSnapshot> isParked = DB.scootersRef.child(scooter.getCode()).child("isParked").get();
        do {} while (!isParked.isSuccessful());
        return isParked.getResult().getValue(Boolean.class);
    }
}
