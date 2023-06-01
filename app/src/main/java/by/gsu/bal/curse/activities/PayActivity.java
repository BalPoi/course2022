package by.gsu.bal.curse.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import by.gsu.bal.curse.R;

public class PayActivity extends AppCompatActivity {

    private TextView tvBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        tvBill = findViewById(R.id.tvBill);
        int seconds = getIntent().getIntExtra("seconds", 0);
        Double bill = calcBill(seconds, 1.0, 0.1);
        tvBill.setText(String.format("%.2f", bill));
    }

    public void onClickBtnPay(View view) {
        finish();
    }

    private Double calcBill(int seconds, double downCost, double tariffCost) {
        double bill = downCost + seconds / 60.0 * tariffCost;
        bill = Math.round(bill * 100);
        return bill / 100;
    }
}
