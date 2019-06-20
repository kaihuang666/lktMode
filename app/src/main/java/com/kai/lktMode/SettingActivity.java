package com.kai.lktMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    List<Item> items=new ArrayList<>();
    private ListAdapter adapter;
    private String[] modes={"省电模式","均衡模式","游戏模式","极限模式"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Item item=new Item("LKT模块","点击安装");
        Item item1=new Item("BusyBox模块","点击安装");
        Item item2=new Item("默认模式","省电模式");
        items.add(item);
        items.add(item1);
        items.add(item2);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new ListAdapter(items);
        recyclerView.setAdapter(adapter);
        //Toast.makeText(this,String.valueOf(Preference.get(this,"version","Boolean")),Toast.LENGTH_LONG).show();
        initDB();
    }
    private void initDB(){
        items.clear();
        Item item=new Item("LKT模块",((Boolean) Preference.get(this,"version","Boolean"))?"已安装":"点击安装");
        Item item1=new Item("BusyBox模块",((Boolean) Preference.get(this,"busybox","Boolean"))?"已安装":"点击安装");
        Item item2=new Item("默认模式",(int)Preference.get(this,"default","int")==0?"省电模式":modes[(int)Preference.get(this,"default","int")]);
        items.add(item);
        items.add(item1);
        items.add(item2);
        adapter.notifyDataSetChanged();

    }
}
