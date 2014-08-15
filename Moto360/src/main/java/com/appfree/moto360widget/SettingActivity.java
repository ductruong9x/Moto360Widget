package com.appfree.moto360widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingActivity extends Activity {
    private int RESULT_LOAD_IMAGE = 1;
    public static final String ACTION_UPDATE_BG = "com.appfree.moto360widget.UPDATE_BG";
    private EditText edCityName;
    private ImageButton btnSearch;
    private Switch switchUnit;
    private RadioGroup rdoAuto;
    private Button btnBG,btnDefault;
    private RadioButton notauto, rdo3h, rdo6h;
    private int auto=0;
    private String cityname="";
    private String unit="";
    private AdView adView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        edCityName = (EditText) findViewById(R.id.edCity);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        switchUnit = (Switch) findViewById(R.id.switchUnit);
        rdoAuto = (RadioGroup) findViewById(R.id.rgAuto);
        btnBG = (Button) findViewById(R.id.btnBG);
        notauto = (RadioButton) findViewById(R.id.notauto);
        rdo3h = (RadioButton) findViewById(R.id.rdo3h);
        rdo6h = (RadioButton) findViewById(R.id.rdo6h);
        adView=(AdView)findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        btnDefault=(Button)findViewById(R.id.btnDefault);

        SharedPreferences sharedPreferences = getSharedPreferences("content", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        cityname = sharedPreferences.getString("city", "News York");
        edCityName.setText(cityname);
        unit=sharedPreferences.getString("unit","F");

        if (TextUtils.equals(unit,"F")){
            switchUnit.setChecked(true);
        }else {
            switchUnit.setChecked(false);
        }

        auto=sharedPreferences.getInt("auto",2);
        if (auto==1){
            notauto.setChecked(true);
        }else if (auto==2){
            rdo3h.setChecked(true);
        }else if (auto==3){
            rdo6h.setChecked(true);

        }

        rdoAuto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.notauto:
                        editor.putInt("auto",1);
                        editor.commit();
                        break;
                    case R.id.rdo3h:
                        editor.putInt("auto",2);
                        editor.commit();

                        break;
                    case R.id.rdo6h:
                        editor.putInt("auto",3);
                        editor.commit();
                        break;
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edCityName.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "Input city name", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    editor.putString("city", edCityName.getText().toString());
                    editor.commit();
                    Toast.makeText(SettingActivity.this, "Update city name successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        switchUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putString("unit","F");
                    editor.commit();
                }else {
                    editor.putString("unit","C");
                    editor.commit();
                }
            }
        });
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("background","");
                editor.commit();
                Intent intent=new Intent();
                intent.setAction(ACTION_UPDATE_BG);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            SharedPreferences sharedPreferences = getSharedPreferences("content", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("background", selectedImage.toString());
            editor.commit();
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_BG);
            sendBroadcast(intent);
            Log.e("URI", selectedImage.toString() + "");

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
