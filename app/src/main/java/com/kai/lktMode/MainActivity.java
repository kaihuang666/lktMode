package com.kai.lktMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.material.navigation.NavigationView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar mNormalToolbar;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    private AlertDialog dialog;
    private ProgressDialog downloadDialog;
    private String[] modeTitle={"省电模式","均衡模式","游戏模式","极限模式"};
    private int[] buttonID={R.id.battery,R.id.balance,R.id.performance,R.id.turbo};
    private int[] ensureID={R.id.ensure1,R.id.ensure2,R.id.ensure3,R.id.ensure4};
    private Shell shell;
    private String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET};
    private String passage;
    private boolean isLktInstalled=false;
    private boolean isBusyboxInstalled=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileDownloader.setup(this);
        mNormalToolbar=findViewById(R.id.simple_toolbar);
        initToolbar();
        initDialog();
        initButton();
        getRoot();
        if ((Boolean)Preference.get(MainActivity.this,"autoLock","Boolean")){
            Intent intent=new Intent(this,AutoService.class);
            startService(intent);
        }
        if (hasPermission()){
            //getTopApp(context);
        }else {
            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(i);
        }
    }
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }





    public void readProp(final boolean isCreate){
        Preference.clearAll(MainActivity.this);
        try {
            cmd("mkdir "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/");
            Command command=new Command(0,"cp -f /data/LKT.prop "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/LKT.prop"){
                @Override
                public void commandCompleted(int id, int exitcode) {
                    super.commandCompleted(id, exitcode);
                    if (exitcode==0){
                        try{
                            passage="";
                            FileReader reader=new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/LKT.prop");
                            BufferedReader reader1=new BufferedReader(reader);
                            String line=null;
                            while ((line=reader1.readLine())!=null){
                                passage+=line+"\n";
                            }
                            reader.close();
                            reader1.close();
                            cutVersion(passage);
                            cutMode(passage);
                            cutBusyBox(passage);
                            //Toast.makeText(MainActivity.this,passage,Toast.LENGTH_LONG).show();
                        }
                        catch (FileNotFoundException e){
                            e.printStackTrace();
                            //installLKT();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        finally {
                            dialog.dismiss();
                        }
                    }else {
                        if (!isCreate)
                            return;
                        try{
                            RootTools.getShell(false).add(new Command(0,"lkt"){
                                @Override
                                public void commandCompleted(int id, int exitcode) {
                                    super.commandCompleted(id, exitcode);
                                    if (exitcode==0) {
                                        dialog.dismiss();
                                        setVersion("已安装");
                                        setMode("unsure");
                                        setBusyBox("未完成开机配置");
                                        Toast.makeText(MainActivity.this,"还未完成开机配置，5秒后执行默认模式",Toast.LENGTH_LONG).show();
                                        TimerTask task=new TimerTask() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        reset();
                                                    }
                                                });
                                            }
                                        };
                                        Timer timer=new Timer();
                                        timer.schedule(task,5000);
                                    }else {
                                        dialog.dismiss();
                                        installLKT();
                                    }
                                }

                            });
                        }catch (Exception e){

                        }

                        //installLKT();
                    }


                }
            };
            shell.add(command);

        }catch (IOException e){
            e.printStackTrace();
        }


    }
    private void cutVersion(String str){
        String pattern = "LKT™\\s(\\S+)";
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(str);
        if (m.find( )) {
            Preference.save(MainActivity.this,"version",true);
            setVersion(m.group(1));

        } else {
            //Toast.makeText(MainActivity.this,)
        }
    }
    private void setVersion(String str){
        TextView version=findViewById(R.id.version);
        version.setText(str);

    }


    private void cutMode(String line){
        String pattern = "PROFILE\\s:\\s(\\S+)";
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find( )) {
            //Toast.makeText(this,m.group(1),Toast.LENGTH_SHORT).show();
            setMode(m.group(1));
        } else {
            //Toast.makeText(MainActivity.this,)
        }

    }
    private void setMode(String str){
        TextView mode=(TextView)findViewById(R.id.mode);
        switch (str){
            case "Battery":mode.setText("省电模式");setButton("省电模式切换中",R.id.battery,R.id.ensure1);break;
            case "Balanced":mode.setText("均衡模式");setButton("均衡模式切换中",R.id.balance,R.id.ensure2);break;
            case "Performance":mode.setText("游戏模式");setButton("游戏模式切换中",R.id.performance,R.id.ensure3);break;
            case "Turbo":mode.setText("极限模式");setButton("极限模式切换中",R.id.turbo,R.id.ensure4);break;
            case "unsure":mode.setText("未完成开机配置");break;
            default:mode.setText("错误配置");reset();break;
        }
    }
    private void reset(){
        TextView mode=(TextView)findViewById(R.id.mode);
        int index=(int)Preference.get(this,"default","int")+1;
        mode.setText(modeTitle[index-1]);
        Toast.makeText(MainActivity.this,"已切换到默认模式",Toast.LENGTH_LONG).show();
        setButton("配置错误，切换到默认模式",buttonID[index-1],ensureID[index-1]);
        try{
            shell.add(new Command(0,"su -c lkt "+index){
                @Override
                public void commandCompleted(int id, int exitcode) {
                    super.commandCompleted(id, exitcode);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void cutBusyBox(String str){
        String pattern = "BUSYBOX\\s:\\s(\\S+)";
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(str);
        if (m.find( )) {
            //showToast(m.group(1));
            Preference.save(MainActivity.this,"busybox",true);
            setBusyBox(m.group(1));
        } else {
            installBusybox();
        }
    }
    private void setBusyBox(String str){
        TextView version=findViewById(R.id.busybox_version);
        if (str.contains("#")){
            version.setText("未安装");
            installBusybox();
        }else
        version.setText(str);
    }
    private void cmd(String str){
        try{
            Runtime.getRuntime().exec(str);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
    @Download.onTaskRunning protected void running(DownloadTask task) {
        int p = task.getPercent();	//任务进度百分比
        downloadDialog.setMessage("下载进度："+p+"%");
        downloadDialog.show();
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        downloadDialog.dismiss();
        switch (task.getTaskName()){
            case "lkt_magisk.zip":

                break;
                case "busybox_magisk.zip":

                break;
        }
    }*/
    private  void disableButton(){
        for (int i:buttonID){
            ((Button)findViewById(i)).setEnabled(false);
        }
    }
    private  void enableButton(){
        for (int i:buttonID){
            ((Button)findViewById(i)).setEnabled(true);
        }
    }
    public static void installStyleB(final Context context){
        AlertDialog dialog2=new AlertDialog.Builder(context,R.style.AppDialog)
                .setTitle("busybox模块已经下载到内部储存/lktMode/lkt_magisk.zip")
                .setItems(new String[]{"使用magisk安装", "重启到rec安装", "稍后再说"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                PackageManager packageManager = context.getPackageManager();
                                Intent intent= packageManager.getLaunchIntentForPackage("com.topjohnwu.magisk");
                                context.startActivity(intent);
                                Toast.makeText(context,"请在<模块>中选择内部储存/lktMode/lkt_magisk.zip安装",Toast.LENGTH_LONG);
                                break;
                            case 1:
                                try{
                                    Runtime.getRuntime().exec(
                                            new String[]{"su","-c","reboot recovery"});
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case 2:
                                break;
                            default:break;
                        }
                    }
                })

                .create();
        dialog2.show();
    }
    public static void installStyleL(final Context context){
        AlertDialog dialog1=new AlertDialog.Builder(context,R.style.AppDialog)
                .setTitle("lkt模块已经下载到内部储存/lktMode/lkt_magisk.zip")
                .setItems(new String[]{"使用magisk安装", "重启到rec安装", "稍后再说"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                PackageManager packageManager =context.getPackageManager();
                                Intent intent= packageManager.getLaunchIntentForPackage("com.topjohnwu.magisk");
                                context.startActivity(intent);
                                Toast.makeText(context,"请在<模块>中选择内部储存/lktMode/lkt_magisk.zip安装",Toast.LENGTH_LONG);
                                break;
                            case 1:
                                try{
                                    Runtime.getRuntime().exec(
                                            new String[]{"su","-c","reboot recovery"});
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case 2:
                                break;
                            default:break;
                        }
                    }
                })

                .create();
        dialog1.show();
    }
    private void installBusybox(){
        disableButton();
        final AlertDialog dialog=new AlertDialog.Builder(MainActivity.this,R.style.AppDialog)
                .setTitle("检测到您的设备暂未安装BusyBox，这可能使模块运行不稳定")
                .setCancelable(true)
                .setItems(new String[]{"直接安装", "安装magisk模块"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        switch (i){
                            case 0: try{
                                    Uri uri = Uri.parse("market://details?id="+"stericson.busybox ");
                                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this,"安装BusyBox应用，并打开安装脚本，完成后请重启手动挡",Toast.LENGTH_LONG).show();
                                }catch(ActivityNotFoundException e){
                                    Toast.makeText(MainActivity.this, "找不到应用市场", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:downloadDialog.show();
                                FileDownloader.getImpl().create("https://files.catbox.moe/5t8g9z.zip")
                                        .setPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/busybox_magisk.zip")
                                        .setForceReDownload(true)
                                        .setListener(new FileDownloadListener() {
                                            @Override
                                            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                            }
                                            @Override
                                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                                int p=soFarBytes*100/totalBytes;
                                                downloadDialog.setMessage("下载进度："+p+"%");
                                                downloadDialog.show();
                                            }
                                            @Override
                                            protected void completed(BaseDownloadTask task) {
                                                downloadDialog.dismiss();
                                                installStyleB(MainActivity.this);
                                            }

                                            @Override
                                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                            }

                                            @Override
                                            protected void error(BaseDownloadTask task, Throwable e) {
                                                downloadDialog.dismiss();
                                                showToast("下载失败");
                                            }

                                            @Override
                                            protected void warn(BaseDownloadTask task) {
                                            }
                                        }).start();
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }
    private void installLKT(){
        disableButton();
        final AlertDialog dialog1=new AlertDialog.Builder(MainActivity.this,R.style.AppDialog)
                .setTitle("检测到您的设备暂未安装LKT模块")
                .setMessage("是否下载LKT magisk模块到您的设备？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        downloadDialog.show();
                        FileDownloader.getImpl().create("https://files.catbox.moe/9ik95m.zip")
                                .setPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/",true)
                                .setForceReDownload(true)
                                .setCallbackProgressTimes(300)
                                .setMinIntervalUpdateSpeed(400)
                                .setListener(new FileDownloadListener() {
                                    @Override
                                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    }
                                    @Override
                                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                        int p=soFarBytes*100/totalBytes;
                                        downloadDialog.setMessage("下载进度："+p+"%");
                                        downloadDialog.show();
                                    }
                                    @Override
                                    protected void completed(BaseDownloadTask task) {
                                        downloadDialog.dismiss();
                                        installStyleL(MainActivity.this);
                                    }

                                    @Override
                                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    }

                                    @Override
                                    protected void error(BaseDownloadTask task, Throwable e) {
                                        downloadDialog.dismiss();
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this,e.toString(),7000).show();
                                    }

                                    @Override
                                    protected void warn(BaseDownloadTask task) {
                                    }
                                }).start();
                        /*
                        Aria.download(this)
                                .load("https://files.catbox.moe/9ik95m.zip")     //读取下载地址
                                .setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lktMode/lkt_magisk.zip") //设置文件保存的完整路径
                                .start();   //启动下载*/


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .create();
        dialog1.show();
    }





    private void getRoot(){
        try {
            if (RootTools.isRootAvailable()){
                dialog.setMessage("获取配置中");
                dialog.show();
                shell=RootTools.getShell(true);
                requetPermission();
            }else {
                Runtime.getRuntime().exec("su");
                dialog.setMessage("正在获取root权限");
                dialog.show();
                shell=RootTools.getShell(true);
                requetPermission();
            }

        }catch (IOException |TimeoutException| RootDeniedException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"无法获取到ROOT权限",Toast.LENGTH_SHORT).show();
        }
    }
    private void initButton(){
        for (int i:buttonID){
            ((Button)findViewById(i)).setOnClickListener(this);
        }
    }
    private void setButton(String title,int id,int ensureId){
        Button button=findViewById(id);
        button.setEnabled(false);
        for(int i:buttonID){
            if (i!=id){
                ((Button)findViewById(i)).setEnabled(true);
            }
        }
        ((ImageView)findViewById(ensureId)).setVisibility(View.VISIBLE);
        for(int i:ensureID){
            if (i!=ensureId){
                ((ImageView)findViewById(i)).setVisibility(View.INVISIBLE);
            }
        }
        dialog.setMessage(title);
        //dialog.show();
    }

    @Override
    public void onClick(View view) {
        TextView mode=(TextView)findViewById(R.id.mode);
        switch (view.getId()){
            case R.id.battery:run("lkt 1");mode.setText("省电模式");setButton("省电模式切换中",R.id.battery,R.id.ensure1);break;
            case R.id.balance:run("lkt 2");mode.setText("均衡模式");setButton("均衡模式切换中",R.id.balance,R.id.ensure2);break;
            case R.id.performance:run("lkt 3");mode.setText("游戏模式");setButton("游戏模式切换中",R.id.performance,R.id.ensure3);break;
            case R.id.turbo:run("lkt 4");mode.setText("极限模式");setButton("极限模式切换中",R.id.turbo,R.id.ensure4);break;
        }
    }
    private void run(String cmd){
        //dialog.setMessage("初始化中");
        dialog.show();
        try{
            Command command=new Command(0,cmd);
            shell.add(command);
        }catch (IOException e){
            e.printStackTrace();
        }
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        timer.schedule(task,3500);
    }
    private void initLKT(){
        setButton("省电模式切换中",R.id.battery,R.id.ensure1);
        setMode("Battery");
        dialog.setMessage("初始化中");
        dialog.show();
        try{
            Command command=new Command(0,"lkt 1");
            shell.add(command);
        }catch (IOException e){
            e.printStackTrace();
        }
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        timer.schedule(task,3500);
    }
    private void initDialog(){
        dialog= new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("模式切换中")
                .build();
        downloadDialog= new ProgressDialog(this,R.style.AppDialog);
        downloadDialog.setCancelable(false);
        downloadDialog.setTitle("正在下载");
    }
    private void initToolbar() {
        //设置menu
        //设置menu的点击事件
        mNormalToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                return true;
            }
        });
        //设置左侧NavigationIcon点击事件
        mNormalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"s",Toast.LENGTH_SHORT).show();
            }
        });
        DrawerLayout drawerLayout=findViewById(R.id.drawer);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mNormalToolbar,R.string.app_name,R.string.app_name);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mActionBarDrawerToggle.setHomeAsUpIndicator(R.mipmap.ic_launcher);//channge the icon,改变图标
        mActionBarDrawerToggle.syncState();////show the default icon and sync the DrawerToggle state,如果你想改变图标的话，这句话要去掉。这个会使用默认的三杠图标
        drawerLayout.setDrawerListener(mActionBarDrawerToggle);//关联 drawerlayout
        NavigationView view=findViewById(R.id.navigationView);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int ID=menuItem.getItemId();
                switch (ID){
                    case R.id.setting:
                        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                        intent.putExtra("passage",passage);
                        startActivity(intent);
                        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                    case R.id.lab:
                        Intent intent1=new Intent(MainActivity.this,LabActivity.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });

    }

    private void showToast(Object e){
        Toast.makeText(MainActivity.this,String.valueOf(e),Toast.LENGTH_SHORT).show();
    }
    private void requetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else {
            readProp(true);
        }
    }




    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            shell=RootTools.getShell(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            readProp(false);
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //同意权限申请
                    readProp(true);

                }else { //拒绝权限申请
                    Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
