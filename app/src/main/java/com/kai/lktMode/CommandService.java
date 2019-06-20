package com.kai.lktMode;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CommandService extends IntentService {
    private String[] modes=new String[]{"省电模式", "均衡模式", "游戏模式","极限模式"};
    Handler msgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(CommandService.this, msg.getData().getString("Text"), Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };
    public CommandService(){
        super("CommandService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int mode=intent.getIntExtra("mode",1);
        showToastByMsg(CommandService.this,modes[mode-1]+"切换中",1000);
        try{
            Runtime.getRuntime().exec("su -c lkt "+mode);
        }catch (Exception e){
            e.printStackTrace();
        }
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                showToastByMsg(CommandService.this,"切换完成",1000);
                msgHandler.removeCallbacksAndMessages(Looper.getMainLooper());
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,5000);
    }
    private void cmd(String[] str){
        try{
            Runtime.getRuntime().exec(str);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showToastByMsg(final IntentService context, final CharSequence text, final int duration) {
        Bundle data = new Bundle();
        data.putString("Text", text.toString());
        Message msg = new Message();
        msg.setData(data);
        msgHandler.sendMessage(msg);
    }



}
