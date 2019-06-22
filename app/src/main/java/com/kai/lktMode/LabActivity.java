package com.kai.lktMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LabActivity extends AppCompatActivity {
    private ListLabAdapter adapter;
    private List<Item> items=new ArrayList<>();
    private String[] checks={"autoBoot","autoLock","gameMode"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        initList();
        initToolBar();
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new ListLabAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new ListLabAdapter.OnItemClick() {
            @Override
            public void onClick(int i) {
                switch (i){
                    case 0:showDialog("打开<开机自启>每次开机将会自动切换到默认模式；该操作将会开启一个开机广播接收器，这可能会消耗一些资源；如果你的LKT调度经常出现开机失效的情况，建议你开启此选项。");break;
                    case 1:showDialog("打开<锁屏自动恢复>每次锁屏会自动切换到省电模式，亮屏后则切换到默认模式；该操作将会开启一个额外的服务来监听熄屏、亮屏操作，这可能会消耗一些资源；如果你经常忘记切换回低功耗调度，强烈建议开启此选项来增加续航。");break;
                    case 2:Toast.makeText(LabActivity.this,"该功能还存在BUG，暂未开放",Toast.LENGTH_SHORT).show();break;
                }
            }
        });
        adapter.setOnItemCheck(new ListLabAdapter.OnItemCheck() {
            @Override
            public void onCheck(int i, Boolean isChecked) {
                Preference.save(LabActivity.this,checks[i],isChecked);
                switch (i){
                    case 1:
                        if (isChecked){
                            Intent intent=new Intent(LabActivity.this,AutoService.class);
                            startService(intent);
                        }
                        break;
                }
            }
        });
        updateList();
        initGame();
    }
    private void updateList(){
        items.get(0).setChecked((Boolean)Preference.get(LabActivity.this,"autoBoot","Boolean"));
        items.get(1).setChecked((Boolean)Preference.get(LabActivity.this,"autoLock","Boolean"));
        items.get(2).setChecked((Boolean)Preference.get(LabActivity.this,"gameMode","Boolean"));
        adapter.notifyDataSetChanged();
    }
    private void initList(){
        Item item=new Item("开机自启",false);
        Item item1=new Item("锁屏省电",false);
        Item item2=new Item("游戏加速",false);
        items.add(item);
        items.add(item1);
        items.add(item2);
    }
    private void initGame(){
        List<Item> items=new ArrayList<>();
        RecyclerView recyclerView=findViewById(R.id.gameList);
        items.add(new Item("com.tencent.tmgp.sgame",false));
        items.add(new Item("com.tencent.tmgp.sgame",false));
        items.add(new Item("com.tencent.tmgp.sgame",false));
        items.add(new Item("com.tencent.tmgp.sgame",false));
        items.add(new Item("com.tencent.tmgp.sgame",false));
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        ListGameAdapter adapter=new ListGameAdapter(LabActivity.this,items);
        recyclerView.setAdapter(adapter);
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
    private void showDialog(String str){
        new AlertDialog.Builder(LabActivity.this,R.style.AppDialog)
                .setNegativeButton("了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setTitle("功能说明")
                .setMessage(str)
                .create().show();
    }
}
