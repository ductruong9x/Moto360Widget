package com.appfree.moto360widget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.appfree.moto360widget.MyActivity;
import com.appfree.moto360widget.R;
import com.appfree.moto360widget.SettingActivity;
import com.appfree.moto360widget.draw.DrawBitmap;
import com.appfree.moto360widget.service.ClockService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class MotoWidgetNice extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

        }
        context.startService(new Intent(context, ClockService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ClockService.ACTION_UPDATE)) {

                updateTime(context);
            }
        }
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(SettingActivity.ACTION_UPDATE_BG)) {

                updateBG(context);
            }
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        context.startService(new Intent(context, ClockService.class));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        context.stopService(new Intent(context, ClockService.class));
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.moto_widget);
        views.setImageViewBitmap(R.id.avatar, DrawBitmap.drawCircleBitmap(context));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void updateTime(Context context) {
        RemoteViews remoteViews = buildUpdate(context,0);

        ComponentName clockWidget = new ComponentName(context,
                MotoWidgetNice.class);

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        appWidgetManager.updateAppWidget(clockWidget, remoteViews);
    }

    private static void updateBG(Context context) {
        RemoteViews remoteViews = buildUpdate(context,1);

        ComponentName clockWidget = new ComponentName(context,
                MotoWidgetNice.class);

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        appWidgetManager.updateAppWidget(clockWidget, remoteViews);
    }

    private static RemoteViews buildUpdate(Context context,int updatebg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString("temp", "°C");
        String unit=sharedPreferences.getString("unit","F");
        String icon = sharedPreferences.getString("icon", "01d");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.moto_widget);
        Intent i = new Intent(context, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
                0);

        Intent ivoice=new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        PendingIntent p=PendingIntent.getActivity(context, 0, ivoice, 0);

        Uri SMS_INBOX = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
        int unreadMessagesCount = cursor.getCount();
        cursor.close();

        String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
        String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE;
        Cursor call = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection ,where, null, null);
        call.moveToFirst();
        int countcall=call.getCount();
        remoteViews.setTextViewText(R.id.tvcall,String.valueOf(countcall));
        remoteViews.setTextViewText(R.id.tvsms,String.valueOf(unreadMessagesCount));
        remoteViews.setOnClickPendingIntent(R.id.google_voice,p);
        remoteViews.setOnClickPendingIntent(R.id.avatar, pendingIntent);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
        String formattedTime = dft.format(c.getTime());
        String formattedDate = df.format(c.getTime());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String weekDay = "";
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "Mon";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "Tues";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "Wed";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "Thurs";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "Fri";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "Sat";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "Sun";
        }

        if (TextUtils.equals(unit,"C")) {
            remoteViews.setTextViewText(R.id.tvtemp, temp + "°C");
        }else {
            remoteViews.setTextViewText(R.id.tvtemp, temp + "°F");
        }
        remoteViews.setTextViewText(R.id.tvtime, formattedTime);
        remoteViews.setTextViewText(R.id.tvdate,weekDay+" "+formattedDate);
        if (updatebg==1) {
            remoteViews.setImageViewBitmap(R.id.avatar, DrawBitmap.drawCircleBitmap(context));
        }
        if (TextUtils.equals(icon, "01d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.clear_sky_day);
        } else if (TextUtils.equals(icon, "01n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.clear_sky_night);
        } else if (TextUtils.equals(icon, "02d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.few_clouds_day);
        } else if (TextUtils.equals(icon, "02n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.few_clouds_night);
        } else if (TextUtils.equals(icon, "03d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "03n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "04d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "04n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "09d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.shower_rain);
        }else if (TextUtils.equals(icon, "09n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.shower_rain);
        }else if (TextUtils.equals(icon, "10d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.rain_day);
        }else if (TextUtils.equals(icon, "10n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.rain_night);
        }else if (TextUtils.equals(icon, "11d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.thunderstorm);
        }else if (TextUtils.equals(icon, "11n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.thunderstorm);
        }else if (TextUtils.equals(icon, "13d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.snow_day);
        }else if (TextUtils.equals(icon, "13n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.snow_night);
        }else if (TextUtils.equals(icon, "50d")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.mist);
        }else if (TextUtils.equals(icon, "50n")) {
            remoteViews.setImageViewResource(R.id.iconweather,R.drawable.mist);
        }

        return remoteViews;
    }

    private static boolean checkDayNight() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 5 && calendar.get(Calendar.HOUR) <= 17) {
            //day
            return true;
        }
        return false;
    }
}


