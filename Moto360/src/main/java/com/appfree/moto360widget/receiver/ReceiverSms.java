package com.appfree.moto360widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appfree.moto360widget.service.ClockService;

public class ReceiverSms extends BroadcastReceiver {
    public ReceiverSms() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent();
        intent1.setAction(ClockService.ACTION_UPDATE);
        context.sendBroadcast(intent1);

    }
}
