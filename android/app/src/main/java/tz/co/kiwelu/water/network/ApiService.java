package tz.co.kiwelu.water.network;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.Map;
import tz.co.kiwelu.water.model.*;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse<LoginData>> login(@Body Map<String, String> body);

    @GET("dashboard")
    Call<ApiResponse<Map<String, Object>>> dashboard();

    @GET("customers")
    Call<ApiResponse<CustomersData>> getCustomers(
        @Query("page") int page,
        @Query("search") String search
    );

    @GET("customers/{id}")
    Call<ApiResponse<CustomerDetail>> getCustomer(@Path("id") int id);

    @POST("customers")
    Call<ApiResponse<Map<String, Object>>> addCustomer(@Body Map<String, Object> body);

    @GET("readings")
    Call<ApiResponse<ReadingsData>> getReadings(
        @Query("page") int page,
        @Query("status") String status
    );

    @POST("readings")
    Call<ApiResponse<Map<String, Object>>> submitReading(@Body Map<String, Object> body);

    @GET("bills")
    Call<ApiResponse<BillsData>> getBills(
        @Query("page") int page,
        @Query("status") String status
    );

    @GET("payments")
    Call<ApiResponse<PaymentsData>> getPayments(@Query("page") int page);

    @GET("profile")
    Call<ApiResponse<Map<String, Object>>> getProfile();
}
