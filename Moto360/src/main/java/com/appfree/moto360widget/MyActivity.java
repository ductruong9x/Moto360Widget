package com.appfree.moto360widget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appfree.moto360widget.network.NetworkOperator;
import com.appfree.moto360widget.service.ClockService;
import com.appfree.moto360widget.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyActivity extends Activity {
    private NetworkOperator operator;
    public static final String ACTION_UPDATE_WEATHER = "com.appfree.moto360widget.UPDATE_WEATHER";
    private TextView tvTime, tvTemp, tvmax, tvmin, tvsunrise, tvsunset, tvdeg, tvwindspeed, tvhumidity, tvcityname;
    private ImageButton btnSetting,btnRe;
    private ImageView imgWeather;
    private String cityname = "";
    private IntentFilter intentFilter;
    private String temp = "";
    private String icon = "";
    private String max = "";
    private String min = "";
    private String humidity = "";
    private String windspeed = "";
    private String deg = "";
    private String sunrise = "";
    private String sunset = "";
    private String unit = "";
    private int auto = 0;
    private AdView adView;
    private String UNIT_ID="ca-app-pub-1857950562418699/8022510362";
    private InterstitialAd interstitialAd;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
            String formattedTime = dft.format(c.getTime());
            tvTime.setText(formattedTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);
            tintManager.setNavigationBarTintResource(R.color.white);
        }
        danhGia();
        tvTime = (TextView) findViewById(R.id.tvtime);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvmax = (TextView) findViewById(R.id.tvmax);
        tvmin = (TextView) findViewById(R.id.tvmin);
        tvsunrise = (TextView) findViewById(R.id.tvSunrise);
        tvsunset = (TextView) findViewById(R.id.tvsunset);
        tvdeg = (TextView) findViewById(R.id.tvdeg);
        tvwindspeed = (TextView) findViewById(R.id.tvwindspeed);
        tvhumidity = (TextView) findViewById(R.id.tvhumidity);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting);
        tvcityname = (TextView) findViewById(R.id.tvcity);
        btnRe=(ImageButton)findViewById(R.id.btnRe);
        adView=(AdView)findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId(UNIT_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        SharedPreferences sharedPreferences = getSharedPreferences("content", MODE_PRIVATE);


        auto = sharedPreferences.getInt("auto", 2);
        if (auto == 1) {

        } else if (auto == 2) {
            setAutogetWeather3h(this);
        } else if (auto == 3) {
            setAutogetWeather6h(this);
        }

        cityname = sharedPreferences.getString("city", "News York");
        temp = sharedPreferences.getString("temp", "°C");
        icon = sharedPreferences.getString("icon", "01d");
        max = sharedPreferences.getString("max", "0");
        min = sharedPreferences.getString("min", "0");
        humidity = sharedPreferences.getString("humidity", "0");
        windspeed = sharedPreferences.getString("windspeed", "0");
        deg = sharedPreferences.getString("deg", "0");
        sunrise = sharedPreferences.getString("sunrise", "6:20");
        sunset = sharedPreferences.getString("sunset", "18:00");
        unit = sharedPreferences.getString("unit", "F");
        tvcityname.setText(cityname);
        if (TextUtils.equals(unit, "F")) {
            tvTemp.setText(getString(R.string.temp) + " " + temp + "°F");
            tvmax.setText(getString(R.string.max) + " " + max + "°F");
            tvmin.setText(getString(R.string.min) + " " + min + "°F");
        } else {
            tvTemp.setText(getString(R.string.temp) + " " + temp + "°C");
            tvmax.setText(getString(R.string.max) + " " + max + "°C");
            tvmin.setText(getString(R.string.min) + " " + min + "°C");
        }
        tvsunrise.setText(sunrise);
        tvsunset.setText(sunset);
        tvhumidity.setText(humidity);
        tvwindspeed.setText(getString(R.string.windspeed) + " " + windspeed + "km/h");
        tvdeg.setText(getString(R.string.deg) + " " + deg);
        setIcon(icon);
        operator = NetworkOperator.getInstance().init(this);
       // getCurrentWeather();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
        String formattedTime = dft.format(c.getTime());
        tvTime.setText(formattedTime);

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        this.registerReceiver(broadcastReceiver, intentFilter);


        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentWeather();
                Toast.makeText(MyActivity.this,"Updating",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentWeather();
    }

    private void setIcon(String icon) {
        if (TextUtils.equals(icon, "01d")) {
            imgWeather.setImageResource(R.drawable.clear_sky_day);
        } else if (TextUtils.equals(icon, "01n")) {
            imgWeather.setImageResource(R.drawable.clear_sky_night);
        } else if (TextUtils.equals(icon, "02d")) {
            imgWeather.setImageResource(R.drawable.few_clouds_day);
        } else if (TextUtils.equals(icon, "02n")) {
            imgWeather.setImageResource(R.drawable.few_clouds_night);
        } else if (TextUtils.equals(icon, "03d")) {
            imgWeather.setImageResource(R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "03n")) {
            imgWeather.setImageResource(R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "04d")) {
            imgWeather.setImageResource(R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "04n")) {
            imgWeather.setImageResource(R.drawable.scattered_clouds);
        } else if (TextUtils.equals(icon, "09d")) {
            imgWeather.setImageResource(R.drawable.shower_rain);
        }else if (TextUtils.equals(icon, "09n")) {
            imgWeather.setImageResource(R.drawable.shower_rain);
        }else if (TextUtils.equals(icon, "10d")) {
            imgWeather.setImageResource(R.drawable.rain_day);
        }else if (TextUtils.equals(icon, "10n")) {
            imgWeather.setImageResource(R.drawable.rain_night);
        }else if (TextUtils.equals(icon, "11d")) {
            imgWeather.setImageResource(R.drawable.thunderstorm);
        }else if (TextUtils.equals(icon, "11n")) {
            imgWeather.setImageResource(R.drawable.thunderstorm);
        }else if (TextUtils.equals(icon, "13d")) {
            imgWeather.setImageResource(R.drawable.snow_day);
        }else if (TextUtils.equals(icon, "13n")) {
            imgWeather.setImageResource(R.drawable.snow_night);
        }else if (TextUtils.equals(icon, "50d")) {
            imgWeather.setImageResource(R.drawable.mist);
        }else if (TextUtils.equals(icon, "50n")) {
            imgWeather.setImageResource(R.drawable.mist);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void getCurrentWeather() {
        operator.getCurrentWeather(cityname, getSuccess(), getError());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    int code = response.getInt("cod");
                    if (code == 200) {
                        JSONObject main = response.getJSONObject("main");
                        JSONArray weather = response.getJSONArray("weather");
                        JSONObject iconjson = weather.getJSONObject(0);
                        JSONObject jsonwind = response.getJSONObject("wind");
                        JSONObject sys = response.getJSONObject("sys");
                        String icon = iconjson.getString("icon");
                        double temp = main.getDouble("temp");
                        double max = main.getDouble("temp_max");
                        double min = main.getDouble("temp_min");
                        int humidity = main.getInt("humidity");
                        double windspeed = jsonwind.getDouble("speed");
                        double deg = jsonwind.getDouble("deg");
                        int sunrise = sys.getInt("sunrise");
                        int sunset = sys.getInt("sunset");
                        String rise = Util.convertDate(sunrise);
                        String set = Util.convertDate(sunset);
                        SharedPreferences sharedPreferences = getSharedPreferences("content", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        unit=sharedPreferences.getString("unit","F");
                        cityname = sharedPreferences.getString("city", "News York");
                        NumberFormat formatter = new DecimalFormat("#0.0");
                        if (TextUtils.equals(unit, "F")) {
                            tvmax.setText(getString(R.string.max) + " " + formatter.format(max) + "°F");
                            tvmin.setText(getString(R.string.min) + " " + formatter.format(min) + "°F");
                            tvTemp.setText(getString(R.string.temp) + " " + formatter.format(temp) + "°F");
                        } else {
                            tvmax.setText(getString(R.string.max) + " " + formatter.format(max) + "°C");
                            tvmin.setText(getString(R.string.min) + " " + formatter.format(min) + "°C");
                            tvTemp.setText(getString(R.string.temp) + " " + formatter.format(temp) + "°C");
                        }
                        tvhumidity.setText(String.valueOf(humidity));
                        tvwindspeed.setText(getString(R.string.windspeed) + formatter.format(windspeed) + "km/h");
                        tvdeg.setText(getString(R.string.deg) + " " + formatter.format(deg));
                        tvsunrise.setText(rise);
                        tvsunset.setText(set);
                        tvcityname.setText(cityname);
                        setIcon(icon.toLowerCase());

                        editor.putString("temp", String.valueOf(formatter.format(temp)));
                        editor.putString("max", String.valueOf(formatter.format(max)));
                        editor.putString("min", String.valueOf(formatter.format(min)));
                        editor.putString("humidity", String.valueOf(humidity));
                        editor.putString("windspeed", String.valueOf(formatter.format(windspeed)));
                        editor.putString("deg", String.valueOf(formatter.format(deg)));
                        editor.putString("icon", icon.toLowerCase());
                        editor.putString("sunrise", rise);
                        editor.putString("sunset", set);
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setAction(ClockService.ACTION_UPDATE);
                        sendBroadcast(intent);
                        Toast.makeText(MyActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyActivity.this, "Do not search city name", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public static void setAutogetWeather3h(Context context) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ClockService.class);
        i.setAction(ACTION_UPDATE_WEATHER);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1 * 60 * 60 * 3 * 1000, pi);
    }

    public static void setAutogetWeather6h(Context context) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ClockService.class);
        i.setAction(ACTION_UPDATE_WEATHER);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1 * 60 * 60 * 6 * 1000, pi);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyActivity.this);
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setTitle(R.string.title);
        dialog.setMessage(R.string.content);
        dialog.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                interstitialAd.show();
            }
        });
        dialog.setNeutralButton(R.string.more, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri
                        .parse("market://details?id=com.gamefree.choosecolor"));
                startActivity(goToMarket);

            }
        });
        dialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    public void danhGia() {
        SharedPreferences getPre = getSharedPreferences("setting", MODE_PRIVATE);
        int i = getPre.getInt("VOTE", 0);
        SharedPreferences pre;
        SharedPreferences.Editor edit;
        switch (i) {
            case 0:
                pre = getSharedPreferences("setting", MODE_PRIVATE);
                edit = pre.edit();
                edit.putInt("VOTE", 1);
                edit.commit();
                break;
            case 1:
                pre = getSharedPreferences("setting", MODE_PRIVATE);
                edit = pre.edit();
                edit.putInt("VOTE", i + 1);
                edit.commit();
                break;
            case 2:
                pre = getSharedPreferences("setting", MODE_PRIVATE);
                edit = pre.edit();
                edit.putInt("VOTE", i + 1);
                edit.commit();
                break;
            case 3:
                pre = getSharedPreferences("setting", MODE_PRIVATE);
                edit = pre.edit();
                edit.putInt("VOTE", i + 1);
                edit.commit();
                break;
            case 4:
                pre = getSharedPreferences("setting", MODE_PRIVATE);
                edit = pre.edit();
                edit.putInt("VOTE", i + 1);
                edit.commit();
                break;
            case 5:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Vote Application");
                dialog.setMessage("You can vote for Quick Reboot");
                dialog.setIcon(R.drawable.ic_launcher);
                dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse("market://details?id="
                                        + getPackageName()));
                        startActivity(goToMarket);
                        SharedPreferences pre = getSharedPreferences("setting", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pre.edit();
                        edit.putInt("VOTE", 6);
                        edit.commit();
                    }
                });
                dialog.setNeutralButton("Do not show", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pre = getSharedPreferences("setting",
                                MODE_PRIVATE);
                        SharedPreferences.Editor edit = pre.edit();
                        edit.putInt("VOTE", 6);
                        edit.commit();
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
                break;
        }
    }
}
