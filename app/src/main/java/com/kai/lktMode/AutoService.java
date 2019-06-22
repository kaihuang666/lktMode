package com.kai.lktMode;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AutoService extends Service {
    private ScreenReceiver receiver;
    private static int SERVICE_ID = 0;
    private String[] games={"com.tencent.tmgp.sgame"};

    public AutoService() {
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };

    Handler msgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, ProtectService.class);
            startService(innerIntent);
            startForeground(SERVICE_ID, new Notification());
        }
        registerScreenActionReceiver();
        regisrerOrentationReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerScreenActionReceiver();
    }
    private void regisrerOrentationReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(new OrientationReciver(), intentFilter);
    }

    private void registerScreenActionReceiver() {
        final IntentFilter filter = new IntentFilter();
        receiver = new ScreenReceiver();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    private void unregisterScreenActionReceiver() {
        unregisterReceiver(receiver);
    }

    private class OrientationReciver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Configuration mConfiguration = AutoService.this.getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向

            if (ori == 2) {
                Toast.makeText(AutoService.this,getTopApp(AutoService.this,0),Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(context, CommandService.class);
                //int mode = (int) Preference.get(context, "default", "int");
                serviceIntent.putExtra("mode", 3);
                serviceIntent.putExtra("isShow", false);
                context.startService(serviceIntent);
            }
            if (ori == 1) {
                //Toast.makeText(context,"游戏加速关闭",Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(context, CommandService.class);
                int mode = (int) Preference.get(context, "default", "int");
                serviceIntent.putExtra("mode", mode);
                serviceIntent.putExtra("isShow", false);
                context.startService(serviceIntent);
            }
        }
    }

    private String getTopApp(Context context,int i) {
        Log.d("search_count",String.valueOf(i));
        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                List<ActivityManager.RunningTaskInfo> rti = activityManager.getRunningTasks(1);
                packageName = rti.get(0).topActivity.getPackageName();
            } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
                if (processes.size() == 0) {
                    return packageName;
                }
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            } else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                final long end = System.currentTimeMillis();
                final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE);
                if (null == usageStatsManager) {
                    return packageName;
                }
                final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
                if (null == events) {
                    return packageName;
                }
                UsageEvents.Event usageEvent = new UsageEvents.Event();
                UsageEvents.Event lastMoveToFGEvent = null;
                while (events.hasNextEvent()) {
                    events.getNextEvent(usageEvent);
                    if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastMoveToFGEvent = usageEvent;
                    }
                }
                if (lastMoveToFGEvent != null) {
                    packageName = lastMoveToFGEvent.getPackageName();
                }
            }
        }catch (Exception ignored){
        }
        if (i==20000){
            return packageName;
        }
        if (i<20000){
            if (Arrays.asList(games).contains(packageName))
                return packageName;
            else
            return getTopApp(context,i+1);
        }
        return null;
    }


}