package by.gsu.bal.curse.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static by.gsu.bal.curse.ScooterService.isScooterParked;
import static by.gsu.bal.curse.ScooterService.lockScooter;
import static by.gsu.bal.curse.ScooterService.updateScooterStatus;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import by.gsu.bal.curse.CaptureAct;
import by.gsu.bal.curse.DB;
import by.gsu.bal.curse.R;
import by.gsu.bal.curse.ScooterService;
import by.gsu.bal.curse.components.MyMarkerRenderer;
import by.gsu.bal.curse.components.StationMarker;
import by.gsu.bal.curse.models.Scooter;
import by.gsu.bal.curse.models.ScooterStatus;
import by.gsu.bal.curse.models.Station;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener,
        ClusterManager.OnClusterItemClickListener {

    private final String TAG = "MapActivity";
    TextView timer;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            timer.setText("" + Minutes + ":" + String.format("%02d", Seconds));
            handler.postDelayed(this, 0);
        }

    };
    private Scooter rentedScooter;
    private TextView tvModelName;
    private TextView tvScooterCode;
    private TextView tvChargePercentage;
    private Button btnEndRent;
    private Button btnScan;
    private GoogleMap mMap;
    private ClusterManager<StationMarker> stationMarkerClusterManager;
    private MapView mMapView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateChangeListener;
    private FirebaseUser user;
    private TextView tvCurrentUserEmail;
    private LinearLayout llRentedScooter;
    ActivityResultLauncher<Intent> rentScooterLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            (ActivityResult result) -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    // Момент после аренды самоката
                    assert result.getData() != null;
                    Log.i(TAG, "mapActivity: trying parce rentedScooter");
                    rentedScooter = result.getData().getParcelableExtra("rentedScooter");
                    Log.i(TAG, "mapActivity: rentedScooter=" + rentedScooter);
                    Log.i(TAG, "timer: init");
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    Log.i(TAG, "timer: done");

                    tvModelName.setText(rentedScooter.getModelName());
                    tvScooterCode.setText(rentedScooter.getCode());
                    tvChargePercentage.setText(rentedScooter.getCharge().toString() + '%');

                    llRentedScooter.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.GONE);
                    btnEndRent.setVisibility(View.VISIBLE);
                }
            });
    ActivityResultLauncher<ScanOptions> scanQrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // Момент после отсканирования QR кода
            Log.i(TAG, "afterScan: scanned code=" + result.getContents());
            Intent intent = new Intent(this, ScooterRentActivity.class);
            intent.putExtra("scooterCode", result.getContents());
            rentScooterLauncher.launch(intent);
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mapView);
        tvCurrentUserEmail = findViewById(R.id.tvCurrentUserEmail);
        llRentedScooter = findViewById(R.id.llRentedScooter);
        tvModelName = findViewById(R.id.tvModelName);
        tvScooterCode = findViewById(R.id.tvScooterCode);
        tvChargePercentage = findViewById(R.id.tvChargePercentage);
        btnEndRent = findViewById(R.id.btnEndRent);
        btnScan = findViewById(R.id.btnScan);
        timer = (TextView) findViewById(R.id.tvTimer);

        handler = new Handler();

        if (rentedScooter == null) {
            llRentedScooter.setVisibility(View.GONE);
            btnEndRent.setVisibility(View.GONE);
        } else {
            btnScan.setVisibility(View.GONE);
            btnEndRent.setVisibility(View.VISIBLE);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthStateChangeListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            Log.i(TAG, "mAuth change: " + (user != null ? user.getEmail() : null));
            tvCurrentUserEmail.setText(user != null ? user.getEmail() : "Гость");
            if (user == null) {
                Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                MapActivity.this.startActivity(intent);
            }
        };

        mMapView.onCreate(savedInstanceState);
        // Initialize the Google Maps instance async.
        mMapView.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        mAuth.addAuthStateListener(mAuthStateChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        if (mAuthStateChangeListener != null) {
            mAuth.removeAuthStateListener(mAuthStateChangeListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.googlemap_style)));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

            // TODO: process if permissions have not been grant
            // @Override
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.424191806718994, 31.013596531419353), 10F));

        stationMarkerClusterManager = new ClusterManager<>(this, mMap);
        MyMarkerRenderer myMarkerRenderer = new MyMarkerRenderer(this, googleMap, stationMarkerClusterManager);
        stationMarkerClusterManager.setRenderer(myMarkerRenderer);

        stationMarkerClusterManager.setOnClusterClickListener(this);
        stationMarkerClusterManager.setOnClusterItemClickListener(this);
        mMap.setOnCameraIdleListener(stationMarkerClusterManager);
        mMap.setOnMarkerClickListener(stationMarkerClusterManager);

        DB.stationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stationMarkerClusterManager.clearItems();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Station station = child.getValue(Station.class);
                    stationMarkerClusterManager.addItem(new StationMarker(station));
                }
                stationMarkerClusterManager.cluster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ignore
            }
        });
    }

    public void onClickBtnSignOut(View view) {
        mAuth.signOut();
    }

    public void onClickBtnScan(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        scanQrCodeLauncher.launch(options);
    }

    public void onClickBtnEndRent(View view) {
        if (!isScooterParked(rentedScooter)) {
            Toast.makeText(this, "Присоедините самокат к станции", Toast.LENGTH_SHORT).show();
            return;
        }
        lockScooter(rentedScooter);
        updateScooterStatus(rentedScooter, ScooterStatus.FREE);
        rentedScooter = null;
        llRentedScooter.setVisibility(View.GONE);
        btnEndRent.setVisibility(View.GONE);
        btnScan.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, PayActivity.class);
        Log.i(TAG, "onClickBtnEndRent: rent seconds=" + Seconds);
        intent.putExtra("seconds", Seconds);

        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;
        timer.setText("00:00:00");

        startActivity(intent);
    }

    void animateZoomInCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public boolean onClusterClick(Cluster cluster) {
        animateZoomInCamera(cluster.getPosition());
        return false;
    }

    @Override
    public boolean onClusterItemClick(ClusterItem item) {
        animateZoomInCamera(item.getPosition());
        return false;
    }
}
