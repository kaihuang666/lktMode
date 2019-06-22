package com.kai.lktMode;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Preference {
    public static void save(Context context,String key, Object value){
        SharedPreferences.Editor editor=context.getSharedPreferences("db",Context.MODE_PRIVATE).edit();
        if (value instanceof Boolean){
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        }else if(value instanceof String){
            editor.putString(key,(String)value);
        }else if(value instanceof Integer){
            editor.putInt(key,((Integer) value).intValue());
        }else if (value instanceof List){
            editor.putStringSet(key,new TreeSet((List<String>) value));
        }
        editor.apply();
    }
    public static Object get(Context context,String key,String type){
        SharedPreferences preferences=context.getSharedPreferences("db",Context.MODE_PRIVATE);
        switch (type){
            case "int":return preferences.getInt(key,0);
            case "Boolean":return preferences.getBoolean(key,false);
            case "String":return preferences.getString(key,"");
            case "StringSet":return preferences.getStringSet(key,null);
            default:return null;
        }

    }
    public static void clearAll(Context context){
        SharedPreferences.Editor editor=context.getSharedPreferences("db",Context.MODE_PRIVATE).edit();
        editor.putBoolean("version",false);
        editor.putBoolean("busybox",false);
        editor.apply();
    }
    public static void gameRemove(Context context,String packageName){
        List<String> list = new ArrayList((Set)get(context,"games","StringSet"));
        list.remove(packageName);
        save(context,"games",list);
    }
    public static void gameAdd(Context context,String packageName){ 
        List<String> list = new ArrayList((Set)get(context,"games","StringSet"));
        list.add(packageName);
        save(context,"games",list);
    }
}
