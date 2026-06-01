package tz.co.kiwelu.water.ui.meter;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.network.RetrofitClient;

public class AddCustomerActivity extends AppCompatActivity {
    private EditText etName, etMobile, etAddress, etZone;
    private Button btnSave;
    private ProgressBar progressBar;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_customer);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Register Customer"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        etName      = findViewById(R.id.etName);
        etMobile    = findViewById(R.id.etMobile);
        etAddress   = findViewById(R.id.etAddress);
        etZone      = findViewById(R.id.etZone);
        btnSave     = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String name   = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Name and mobile are required", Toast.LENGTH_SHORT).show(); return;
        }
        progressBar.setVisibility(View.VISIBLE); btnSave.setEnabled(false);
        Map<String, Object> body = new HashMap<>();
        body.put("name", name); body.put("mobile", mobile);
        body.put("address", etAddress.getText().toString().trim());
        body.put("zone", etZone.getText().toString().trim());

        RetrofitClient.getApi().addCustomer(body).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override public void onResponse(Call<ApiResponse<Map<String, Object>>> c, Response<ApiResponse<Map<String, Object>>> r) {
                progressBar.setVisibility(View.GONE); btnSave.setEnabled(true);
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    String cid = String.valueOf(r.body().data.get("customer_id"));
                    Toast.makeText(AddCustomerActivity.this, "Customer registered! ID: " + cid, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddCustomerActivity.this, r.body() != null ? r.body().message : "Failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Map<String, Object>>> c, Throwable t) {
                progressBar.setVisibility(View.GONE); btnSave.setEnabled(true);
                Toast.makeText(AddCustomerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
