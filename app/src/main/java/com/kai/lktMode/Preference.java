package com.kai.lktMode;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    public static void save(Context context,String key, Object value){
        SharedPreferences.Editor editor=context.getSharedPreferences("db",Context.MODE_PRIVATE).edit();
        if (value instanceof Boolean){
            editor.putBoolean(key,(Boolean) value);
        }else if(value instanceof String){
            editor.putString(key,(String)value);
        }else if(value instanceof Integer){
            editor.putInt(key,((Integer) value).intValue());
        }
        editor.apply();
    }
    public static Object get(Context context,String key,String type){
        SharedPreferences preferences=context.getSharedPreferences("db",Context.MODE_PRIVATE);
        switch (type){
            case "int":return preferences.getInt(key,0);
            case "Boolean":return preferences.getBoolean(key,false);
            case "String":return preferences.getString(key,"");
            default:return null;
        }

    }
    public static void clearAll(Context context){
        SharedPreferences.Editor editor=context.getSharedPreferences("db",Context.MODE_PRIVATE).edit();
        int mode=(int)get(context,"default","int");
        editor.clear();
        editor.apply();
        save(context,"default",mode);

    }
}
