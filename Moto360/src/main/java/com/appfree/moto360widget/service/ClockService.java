package com.appfree.moto360widget.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appfree.moto360widget.MyActivity;
import com.appfree.moto360widget.network.NetworkOperator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by truongtvd on 7/31/14.
 */
public class ClockService extends Service {
    private IntentFilter intentFilter;
    public static final String ACTION_UPDATE = "com.appfree.moto360widget.action.UPDATE";
    public static final String ACTION_DESTROY="com.appfree.moto360widget.action.DESTROY";
    private NetworkOperator operator;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent clock=new Intent();
            clock.setAction(ACTION_UPDATE);
            sendBroadcast(clock);
        }
    };
    private String cityname="";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        operator=NetworkOperator.getInstance().init(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        this.registerReceiver(broadcastReceiver,intentFilter);
        Intent clock=new Intent();
        clock.setAction(ACTION_UPDATE);
        sendBroadcast(clock);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent();
        intent.setAction(ACTION_DESTROY);
        sendBroadcast(intent);
        this.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(MyActivity.ACTION_UPDATE_WEATHER)){
                SharedPreferences sharedPreferences=getSharedPreferences("content",MODE_PRIVATE);
                cityname=sharedPreferences.getString("city", "News York");
                getCurrentWeather();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void getCurrentWeather(){
        operator.getCurrentWeather(cityname,getSuccess(),getError());
    }

    public Response.ErrorListener getError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, String extraData) {
                error.printStackTrace();
            }
        };
    }

    public Response.Listener<JSONObject> getSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response, String extraData) {
                Log.e("JSON", response.toString());
                try {
                    int code=response.getInt("cod");
                    if(code==200){
                        JSONObject main=response.getJSONObject("main");
                        JSONArray weather=response.getJSONArray("weather");
                        JSONObject iconjson=weather.getJSONObject(0);
                        String icon=iconjson.getString("icon");
                        double temp=main.getDouble("temp");

                        NumberFormat formatter = new DecimalFormat("#0.0");
                        SharedPreferences sharedPreferences=getSharedPreferences("content",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("temp",String.valueOf(formatter.format(temp)));

                        editor.putString("icon",icon.toLowerCase());
                        editor.commit();
                        Intent intent=new Intent();
                        intent.setAction(ACTION_UPDATE);
                        sendBroadcast(intent);
                    }else {
                        Toast.makeText(getApplicationContext(), "Do not update weather", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
