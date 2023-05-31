package by.gsu.bal.curse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import by.gsu.bal.curse.DB;
import by.gsu.bal.curse.R;
import by.gsu.bal.curse.models.Scooter;

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

        DB.scootersRef.child(scooterCode).get().addOnCompleteListener(task -> {
            rentedScooter = task.getResult().getValue(Scooter.class);
            tvModelName.setText(rentedScooter.getModelName());
            tvScooterCode.setText(rentedScooter.getCode());
            tvChargePercentage.setText(rentedScooter.getCharge().toString() + '%');
        });
    }

    public void onClickBtnRent(View view) {
        Intent intent = new Intent();
        intent.putExtra("rentedScooter", rentedScooter);
        setResult(-1, intent);
        // todo: манипуляции со станциями и самокатами
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
