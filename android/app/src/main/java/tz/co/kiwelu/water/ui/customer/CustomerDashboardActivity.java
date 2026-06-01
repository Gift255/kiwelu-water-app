package tz.co.kiwelu.water.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.App;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.network.RetrofitClient;
import tz.co.kiwelu.water.ui.LoginActivity;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvBalance, tvLastBill, tvLastPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWelcome     = findViewById(R.id.tvWelcome);
        tvBalance     = findViewById(R.id.tvBalance);
        tvLastBill    = findViewById(R.id.tvLastBill);
        tvLastPayment = findViewById(R.id.tvLastPayment);

        tvWelcome.setText("Hello, " + App.getSession().getName());

        MaterialCardView cardBills = findViewById(R.id.cardBills);
        cardBills.setOnClickListener(v ->
            startActivity(new Intent(this, MyBillsActivity.class)));

        MaterialCardView cardPayments = findViewById(R.id.cardPayments);
        cardPayments.setOnClickListener(v ->
            startActivity(new Intent(this, MyPaymentsActivity.class)));

        loadDashboard();
    }

    @SuppressWarnings("unchecked")
    private void loadDashboard() {
        RetrofitClient.getApi().dashboard().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                   Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Map<String, Object> d = response.body().data;
                    double balance = d.containsKey("balance") ? (Double) d.get("balance") : 0;
                    tvBalance.setText("TZS " + String.format("%,.0f", balance));

                    List<Map<String, Object>> bills = (List<Map<String, Object>>) d.get("recent_bills");
                    if (bills != null && !bills.isEmpty()) {
                        Map<String, Object> last = bills.get(0);
                        tvLastBill.setText("TZS " + String.format("%,.0f", (Double) last.getOrDefault("amount", 0.0))
                            + " — " + last.getOrDefault("bill_month", ""));
                    } else {
                        tvLastBill.setText("No bills yet");
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(CustomerDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            App.getSession().clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
