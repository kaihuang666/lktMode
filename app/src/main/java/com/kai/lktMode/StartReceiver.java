package com.kai.lktMode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CommandService.class);
        i.putExtra("mode",1);
        context.startService(i);
    }
}
