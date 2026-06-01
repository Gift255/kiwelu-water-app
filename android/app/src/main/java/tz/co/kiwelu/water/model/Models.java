package tz.co.kiwelu.water.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// ── Login ─────────────────────────────────────────────────────────────────
public class Models {

    public static class LoginData {
        public String token;
        public User user;
    }

    public static class User {
        public int id;
        public String name, username, role;
        @SerializedName("org_id") public int orgId;
    }

    // ── Customer ─────────────────────────────────────────────────────────
    public static class Customer {
        public int id;
        @SerializedName("customer_id") public String customerId;
        public String name, mobile, email, address, zone, status;
        @SerializedName("approval_status") public String approvalStatus;
        public Double lat, lng;
        @SerializedName("created_at") public String createdAt;
    }

    public static class CustomersData {
        public List<Customer> customers;
        public int total, page;
    }

    public static class CustomerDetail {
        public Customer customer;
        public List<Bill> bills;
        public List<Payment> payments;
    }

    // ── Reading ───────────────────────────────────────────────────────────
    public static class Reading {
        public int id;
        @SerializedName("customer_id") public int customerId;
        @SerializedName("customer_name") public String customerName;
        @SerializedName("cust_no") public String custNo;
        @SerializedName("reader_name") public String readerName;
        @SerializedName("previous_reading") public double previousReading;
        @SerializedName("current_reading") public double currentReading;
        @SerializedName("units_consumed") public double unitsConsumed;
        @SerializedName("reading_date") public String readingDate;
        public String status, comment;
        @SerializedName("created_at") public String createdAt;
    }

    public static class ReadingsData {
        public List<Reading> readings;
    }

    // ── Bill ─────────────────────────────────────────────────────────────
    public static class Bill {
        public int id;
        @SerializedName("customer_name") public String customerName;
        @SerializedName("cust_no") public String custNo;
        @SerializedName("bill_month") public String billMonth;
        @SerializedName("units_consumed") public double unitsConsumed;
        public double amount;
        public String status;
        @SerializedName("due_date") public String dueDate;
        @SerializedName("bill_reference") public String billReference;
    }

    public static class BillsData {
        public List<Bill> bills;
    }

    // ── Payment ───────────────────────────────────────────────────────────
    public static class Payment {
        public int id;
        @SerializedName("customer_name") public String customerName;
        @SerializedName("amount_paid") public double amountPaid;
        public double balance;
        @SerializedName("payment_date") public String paymentDate;
        @SerializedName("payment_method") public String paymentMethod;
        public String reference;
    }

    public static class PaymentsData {
        public List<Payment> payments;
    }
}
