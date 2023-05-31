package by.gsu.bal.curse.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import by.gsu.bal.curse.CaptureAct;
import by.gsu.bal.curse.R;
import by.gsu.bal.curse.models.Scooter;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapActivity";
    private Scooter rentedScooter;
    private TextView tvModelName;
    private TextView tvScooterCode;
    private TextView tvChargePercentage;
    private Button btnEndRent;
    private Button btnScan;
    private GoogleMap mMap;
    private MapView mMapView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateChangeListener;
    private FirebaseUser user;
    private TextView tvCurrentUserEmail;
    private LinearLayout llRentedScooter;
    ActivityResultLauncher<Intent> rentScooterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    rentedScooter = result.getData().getParcelableExtra("rentedScooter");
                    // todo: таймер и заменить кнопку скана на окончание аренды

                    tvModelName.setText(rentedScooter.getModelName());
                    tvScooterCode.setText(rentedScooter.getCode());
                    tvChargePercentage.setText(rentedScooter.getCharge().toString() + '%');

                    llRentedScooter.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.GONE);
                    btnEndRent.setVisibility(View.VISIBLE);
                }
            });
    ActivityResultLauncher<ScanOptions> scanQrCodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

            // TODO: process if permissions have not been grant
            // @Override
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(52.424191806718994, 31.013596531419353), 10F));


        // mMap.addMarker(new MarkerOptions().)
    }

    public void onClickBtnSignOut(View view) {
        mAuth.signOut();
    }

    public void onClickBtnScan(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        scanQrCodeLauncher.launch(options);
    }

    public void onClickBtnEndRent(View view) {
        rentedScooter = null;
        llRentedScooter.setVisibility(View.GONE);
        btnEndRent.setVisibility(View.GONE);
        btnScan.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("seconds", 300); // todo: remove hardcode
        startActivity(intent);
    }
}
