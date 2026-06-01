package tz.co.kiwelu.water;

import android.app.Application;
import tz.co.kiwelu.water.network.RetrofitClient;
import tz.co.kiwelu.water.util.SessionManager;

public class App extends Application {
    private static SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        RetrofitClient.init(sessionManager);
    }

    public static SessionManager getSession() { return sessionManager; }
}
