package com.kai.lktMode;

import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    List<Item> items=new ArrayList<>();
    private ListAdapter adapter;
    private String[] modes={"省电模式","均衡模式","游戏模式","极限模式"};
    private String busyBoxInfo="";
    private ProgressDialog downloadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolBar();
        final Intent intent=getIntent();
        downloadDialog= new ProgressDialog(this,R.style.AppDialog);
        downloadDialog.setCancelable(false);
        downloadDialog.setTitle("正在下载");
        Item item=new Item("LKT模块","点击安装");
        Item item1=new Item("BusyBox模块","点击安装");
        Item item2=new Item("默认模式","省电模式");
        Item item3=new Item("通知栏磁贴","未授权");
        items.add(item);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new ListAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new ListAdapter.OnItemClick() {
            @Override
            public void onClick(int i) {
                switch (i){
                    case 0:
                        if ((Boolean) Preference.get(SettingActivity.this,"version","Boolean")){
                            AlertDialog dialog=new AlertDialog.Builder(SettingActivity.this,R.style.AppDialog)
                                    .setMessage(intent.getStringExtra("passage"))
                                    .setTitle("配置文件")
                                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .create();
                            dialog.show();
                        }else {
                            installLKT();
                        }

                        break;
                    case 1:
                        if((Boolean) Preference.get(SettingActivity.this,"busybox","Boolean")){
                            try{
                                if (busyBoxInfo.isEmpty()) {
                                    RootTools.getShell(false).add(new Command(0, "busybox --help") {
                                        @Override
                                        public void commandOutput(int id, String line) {
                                            super.commandOutput(id, line);
                                            busyBoxInfo+=line+"\n";
                                        }

                                        @Override
                                        public void commandCompleted(int id, int exitcode) {
                                            super.commandCompleted(id, exitcode);
                                            showBusyboxInfo();
                                        }
                                    });
                                }else {
                                    showBusyboxInfo();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            installBusybox();
                        }

                        break;
                    case 2:

                        AlertDialog dialog1=new AlertDialog.Builder(SettingActivity.this,R.style.AppDialog)
                                .setTitle("设置默认模式")
                                .setSingleChoiceItems(modes, (int) Preference.get(SettingActivity.this, "default", "int"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Preference.save(SettingActivity.this,"default",Integer.valueOf(i));
                                        updateList();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create();
                        dialog1.show();
                        break;
                    case 3:
                        setting();
                        break;

                }

            }
        });
        updateList();
    }
    private void updateList(){
        //Toast.makeText(this,""+Preference.get(this,"default","int"),Toast.LENGTH_SHORT).show();
        items.clear();
        Item item=new Item("LKT模块",((Boolean) Preference.get(this,"version","Boolean"))?"已安装":"点击安装");
        Item item1=new Item("BusyBox模块",((Boolean) Preference.get(this,"busybox","Boolean"))?"已安装":"点击安装");
        Item item2=new Item("默认模式",modes[(int)Preference.get(this,"default","int")]);
        Item item3=new Item("通知栏磁贴","已授权");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            item3.setSubtitle("版本过低");
        }else if (!Settings.canDrawOverlays(SettingActivity.this)){
            item3.setSubtitle("未授权");
        }

        items.add(item);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        adapter.notifyDataSetChanged();

    }
    private void showBusyboxInfo(){
        AlertDialog dialog=new AlertDialog.Builder(SettingActivity.this,R.style.AppDialog)
                .setMessage(busyBoxInfo)
                .setTitle("Busybox版本信息")
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }
    private void installBusybox(){
        final AlertDialog dialog=new AlertDialog.Builder(SettingActivity.this,R.style.AppDialog)
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
                                Toast.makeText(SettingActivity.this,"安装BusyBox应用，并打开安装脚本，完成后请重启手动挡",Toast.LENGTH_LONG).show();
                            }catch(ActivityNotFoundException e){
                                Toast.makeText(SettingActivity.this, "找不到应用市场", Toast.LENGTH_SHORT).show();
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
                                                MainActivity.installStyleB(SettingActivity.this);
                                            }

                                            @Override
                                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                            }

                                            @Override
                                            protected void error(BaseDownloadTask task, Throwable e) {
                                                downloadDialog.dismiss();

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
    private void setting(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(SettingActivity.this)) {
                Toast.makeText(this,"已授权",Toast.LENGTH_SHORT).show();
            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
                Toast.makeText(SettingActivity.this,"需要取得权限以使用悬浮窗",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

        }else {
            Toast.makeText(this,"版本过低",Toast.LENGTH_SHORT).show();
        }

    }
    private void installLKT(){
        final AlertDialog dialog1=new AlertDialog.Builder(SettingActivity.this,R.style.AppDialog)
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
                                        MainActivity.installStyleL(SettingActivity.this);
                                    }

                                    @Override
                                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                    }

                                    @Override
                                    protected void error(BaseDownloadTask task, Throwable e) {
                                        downloadDialog.dismiss();
                                        e.printStackTrace();
                                        Toast.makeText(SettingActivity.this,e.toString(),Toast.LENGTH_LONG).show();
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

    private void initToolBar(){
        Toolbar toolbar=findViewById(R.id.simple_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            updateList();
        }
    }
}
