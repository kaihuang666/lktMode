package com.kai.lktMode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Display;

import androidx.core.app.NotificationCompat;

public class StartReceiver extends BroadcastReceiver {
    private String[] modes={"省电模式","均衡模式","游戏模式","极限模式"};
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            if ((Boolean) Preference.get(context,"autoBoot","Boolean")) {
                Intent serviceIntent = new Intent(context, CommandService.class);
                int mode = (int) Preference.get(context, "default", "int");
                serviceIntent.putExtra("mode", mode + 1);
                context.startService(serviceIntent);
                requestNotification(context, mode);
            }
            if ((Boolean)Preference.get(context,"autoLock","Boolean")){
                Intent intent1=new Intent(context,AutoService.class);
                context.startService(intent1);
            }
        }
    }
    private void requestNotification(Context context,int mode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "lkt_boot";
            String channelName = "开机启动";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
            showNotification(context,mode);
        }else {
            showNotificationApi23(context,mode);
        }

    }
    private void showNotification(Context context, int mode) {
        String channelId = "lkt_boot";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("已切换到默认模式:"+modes[mode])
                .build();
        notificationManager.notify(1, notification);

    }
    private void showNotificationApi23(Context context,int mode){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("已切换到默认模式:"+modes[mode])
                .build();
        notificationManager.notify(1, notification);

    }

}
