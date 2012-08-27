package com.virtuoussoftware.apptly.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * SyncService
 * Created by Aaron Sarazan on 8/26/12
 * <p/>
 * Copyright 2012 Virtuous Software. All rights reserved.
 */
public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
