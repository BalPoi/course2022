package by.gsu.bal.curse.activities;

import static by.gsu.bal.curse.ScooterService.isScooterAvailable;
import static by.gsu.bal.curse.ScooterService.updateScooterStatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import by.gsu.bal.curse.DB;
import by.gsu.bal.curse.R;
import by.gsu.bal.curse.models.Scooter;
import by.gsu.bal.curse.models.ScooterStatus;

public class ScooterRentActivity extends AppCompatActivity {

    private static final String TAG = "ScooterRentActivity";
    private TextView tvModelName;
    private TextView tvScooterCode;
    private TextView tvChargePercentage;
    private TextView tvDownPayment;
    private TextView tvRentalCost;
    private Button btnRent;
    private Scooter rentedScooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scooter_rent);

        initViews();
        Intent intent = getIntent();
        String scooterCode = intent.getStringExtra("scooterCode");
        Log.i(TAG, "onCreate: scooterCode=" + scooterCode);

        DB.scootersRef.child(scooterCode).get().addOnCompleteListener(task -> {
            rentedScooter = task.getResult().getValue(Scooter.class);
            tvModelName.setText(rentedScooter.getModelName());
            tvScooterCode.setText(rentedScooter.getCode());
            tvChargePercentage.setText(rentedScooter.getCharge().toString() + '%');
            Log.i(TAG, "onCreate: " + rentedScooter);
        });

    }

    public void onClickBtnRent(View view) {
        Intent intent = new Intent();
        if (isScooterAvailable(rentedScooter.getCode())) {
            intent.putExtra("rentedScooter", rentedScooter);
            setResult(RESULT_OK, intent);
            updateScooterStatus(rentedScooter, ScooterStatus.BUSY);
            Log.i(TAG, "onClickBtnRent: scooter rented");
        } else {
            Toast.makeText(this, "Этот самокат уже недоступен", Toast.LENGTH_SHORT).show();      // fixme
            setResult(RESULT_CANCELED, intent);
            Log.i(TAG, "onClickBtnRent: scooter busy");
        }
        finish();
    }

    private void initViews() {
        tvModelName = findViewById(R.id.tvModelName);
        tvScooterCode = findViewById(R.id.tvScooterCode);
        tvChargePercentage = findViewById(R.id.tvChargePercentage);
        tvDownPayment = findViewById(R.id.tvDownPayment);
        tvRentalCost = findViewById(R.id.tvRentalCost);
        btnRent = findViewById(R.id.btnRent);
    }
}
