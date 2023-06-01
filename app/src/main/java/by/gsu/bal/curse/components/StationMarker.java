package by.gsu.bal.curse.components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import by.gsu.bal.curse.models.Station;

public class StationMarker implements ClusterItem {
    private Station station;

    public StationMarker(Station station) {
        this.station = station;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return new LatLng(station.getLat(), station.getLon());
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    @Nullable
    @Override
    public Float getZIndex() {
        return 0F;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getCountStatus() {
        Long freeSlots = station.getFreeSlots();
        Long totalSlots = station.getSlotsNum();
        return totalSlots - freeSlots + "/" + totalSlots;
    }
}
