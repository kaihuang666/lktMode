package com.kai.lktMode;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class LktAppWidget extends AppWidgetProvider {
    private static String ACTION="com.kai.lktUpdate";
    int[] buttonIds={R.id.battery_button,R.id.balance_button,R.id.performance_button,R.id.turbo_button};
    int[] modes={1,2,3,4};
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.deskwidget);
        for (int id:modes){
            //views.setOnClickPendingIntent(buttonIds[id-1],getPendingIntent(id,context));
            views.setOnClickPendingIntent(buttonIds[id-1],getPendingIntent(context,id));
        }
        //views.setOnClickPendingIntent(R.id.balance_button,getPendingIntent(context,2));
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }
    @TargetApi(26)
    private PendingIntent getPendingIntent(Context context,int mode){
        Intent intent = new Intent(context,CommandService.class);
        intent.putExtra("mode",mode);

        PendingIntent pendingIntent = PendingIntent.getService(context, mode,intent,PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
    private PendingIntent getPendingIntent(int i,Context context){
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("mode",i);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i,intent,PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

