package com.kai.lktMode;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ProtectService extends Service {
    private static int SERVICE_ID=0;
    public ProtectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(SERVICE_ID, new Notification());
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
