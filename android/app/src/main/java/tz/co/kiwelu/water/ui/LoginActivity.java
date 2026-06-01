package tz.co.kiwelu.water.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.App;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;
import tz.co.kiwelu.water.ui.admin.AdminDashboardActivity;
import tz.co.kiwelu.water.ui.customer.CustomerDashboardActivity;
import tz.co.kiwelu.water.ui.meter.MeterDashboardActivity;
import tz.co.kiwelu.water.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Skip login if already logged in
        SessionManager session = App.getSession();
        if (session.isLoggedIn()) { redirectByRole(session.getRole()); return; }

        setContentView(R.layout.activity_login);

        etUsername  = findViewById(R.id.etUsername);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError     = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Please enter username and password.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        tvError.setVisibility(View.GONE);
        setLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        RetrofitClient.getApi().login(body).enqueue(new Callback<ApiResponse<Models.LoginData>>() {
            @Override
            public void onResponse(Call<ApiResponse<Models.LoginData>> call,
                                   Response<ApiResponse<Models.LoginData>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Models.LoginData d = response.body().data;
                    App.getSession().save(d.token, d.user.id, d.user.name, d.user.role, d.user.orgId);
                    redirectByRole(d.user.role);
                } else {
                    tvError.setText(response.body() != null ? response.body().message : "Login failed");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Models.LoginData>> call, Throwable t) {
                setLoading(false);
                tvError.setText("Network error: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void redirectByRole(String role) {
        Class<?> dest;
        switch (role) {
            case "meter_reader": dest = MeterDashboardActivity.class; break;
            case "customer":     dest = CustomerDashboardActivity.class; break;
            default:             dest = AdminDashboardActivity.class; break;
        }
        startActivity(new Intent(this, dest));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
