package com.roozbeh.toopan.communication.netDetector;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutors {

    private static AppExecutors sInstance;
    private final Executor networkIO;
    private final Executor diskIO;
    private final Executor mainThread;


    private AppExecutors() {
        networkIO = Executors.newSingleThreadExecutor();
        diskIO = Executors.newFixedThreadPool(3);
        mainThread = new MainThreadExecutor();
    }

    public static synchronized AppExecutors getInstance() {
        if (sInstance == null) {
            sInstance = new AppExecutors();
        }
        return sInstance;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
