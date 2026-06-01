package tz.co.kiwelu.water.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "kiwelu_session";
    private static final String KEY_TOKEN  = "token";
    private static final String KEY_NAME   = "name";
    private static final String KEY_ROLE   = "role";
    private static final String KEY_ORG    = "org_id";
    private static final String KEY_UID    = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void save(String token, int userId, String name, String role, int orgId) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_UID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_ROLE, role)
            .putInt(KEY_ORG, orgId)
            .apply();
    }

    public void clear() { prefs.edit().clear().apply(); }

    public String getToken()  { return prefs.getString(KEY_TOKEN, null); }
    public String getName()   { return prefs.getString(KEY_NAME, ""); }
    public String getRole()   { return prefs.getString(KEY_ROLE, ""); }
    public int    getOrgId()  { return prefs.getInt(KEY_ORG, 0); }
    public int    getUserId() { return prefs.getInt(KEY_UID, 0); }
    public boolean isLoggedIn() { return getToken() != null; }

    public boolean isAdmin()       { String r = getRole(); return r.equals("admin") || r.equals("owner"); }
    public boolean isMeterReader() { return getRole().equals("meter_reader"); }
    public boolean isCustomer()    { return getRole().equals("customer"); }
    public boolean isFinance()     { return getRole().equals("finance"); }
}
