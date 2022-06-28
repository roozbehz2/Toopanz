package com.roozbeh.toopan.communication.netDetector;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

public class NetDetector {

    private static AppExecutors appExecutors;
    private static Context context;

    /**
     * Initialization should be done in Application class and only one time.
     *
     * @param context Application context
     */
    public static void init(Context context) {
        appExecutors = AppExecutors.getInstance();
        NetDetector.context = context.getApplicationContext();
    }

    public static synchronized void check(ConnectivityCallback callback) {
        appExecutors.getNetworkIO().execute(() -> {
            if (isNetworkAvailable()) {
                HttpsURLConnection connection = null;
                try {
                    /*connection = (HttpsURLConnection)
                            new URL("https://www.google.com/").openConnection();
                    connection.setRequestProperty("User-Agent", "Android");
                    connection.setRequestProperty("Connection", "close");
                    connection.setConnectTimeout(1000);
                    connection.connect();

                    boolean isConnected = connection.getResponseCode() == 200
                            && connection.getContentLength() == 0;*/

                    boolean isConnected = isConnected();

                    postCallback(callback, isConnected);
                    //connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    postCallback(callback, false);
                    //if (connection != null) connection.disconnect();
                }
            } else {
                postCallback(callback, false);
            }
        });
    }

    public static boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }


    private static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (cap == null) return false;
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network n : networks) {
                NetworkInfo nInfo = cm.getNetworkInfo(n);
                if (nInfo != null && nInfo.isConnected()) return true;
            }
        } else {
            NetworkInfo[] networks = cm.getAllNetworkInfo();
            for (NetworkInfo nInfo : networks) {
                if (nInfo != null && nInfo.isConnected()) return true;
            }
        }

        return false;
    }

    private static void postCallback(ConnectivityCallback callBack, boolean isConnected) {
        appExecutors.mainThread().execute(() -> callBack.onDetected(isConnected));
    }
    private static void CancelPostCallback(ConnectivityCallback callBack, boolean isConnected) {
        appExecutors.mainThread().execute(() -> callBack.onDetected(isConnected));
    }

    public interface ConnectivityCallback {
        void onDetected(boolean isConnected);
    }
}
