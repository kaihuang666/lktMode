package com.kai.lktMode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!(Boolean)Preference.get(context,"autoLock","Boolean"))
            return;
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Intent serviceIntent = new Intent(context, CommandService.class);
            int mode = (int) Preference.get(context, "default", "int");
            if (mode>0){
                serviceIntent.putExtra("mode", mode + 1);
                context.startService(serviceIntent);
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Intent serviceIntent = new Intent(context, CommandService.class);
            serviceIntent.putExtra("mode", 1);
            context.startService(serviceIntent);
        }
    }




}
