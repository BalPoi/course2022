package by.gsu.bal.curse;

import java.time.format.DateTimeFormatter;

public final class Constants {

    public static final String POSTS_KEY = "posts";
    public static final String USERS_KEY = "users";
    public static final String STATIONS_KEY = "stations";
    public static final String SCOOTERS_KEY = "scooters";
    public static final String DB_URL = "https://curse2022-fa309-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String IMG_STORAGE_URL = "gs://curse2022-fa309.appspot.com";
    public static final DateTimeFormatter ISO8601 = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private Constants() {
    }
}
