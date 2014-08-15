package com.appfree.moto360widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appfree.moto360widget.service.ClockService;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null&&intent.getAction()!=null){
            if (intent.getAction().equals(ClockService.ACTION_DESTROY)){
                Intent intent1=new Intent(context,ClockService.class);
                context.startService(intent1);
            }
        }

    }
}
