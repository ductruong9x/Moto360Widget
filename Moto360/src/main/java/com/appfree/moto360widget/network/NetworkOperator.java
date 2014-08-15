package com.appfree.moto360widget.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import khandroid.ext.apache.http.client.utils.URIBuilder;
import khandroid.ext.apache.http.message.BasicNameValuePair;


public class NetworkOperator {
    private static final String TAG = NetworkOperator.class.getSimpleName();
    private static NetworkOperator defaultInstance;
    private Context context;
    private RequestQueue requestQueue;
    String url = "https://graph.facebook.com/fql?q=";

    public static NetworkOperator getInstance() {
        if (defaultInstance == null) {
            synchronized (NetworkOperator.class) {
                if (defaultInstance == null) {
                    defaultInstance = new NetworkOperator();
                }
            }
        }
        return defaultInstance;
    }

    public NetworkOperator init(Context context) {
        this.context = context;
        requestQueue = MyVolley.getRequestQueue();
        return this;
    }


    public void getCurrentWeather(String name, Response.Listener<JSONObject> responseSuccessListener,
                                  Response.ErrorListener responseErrorListener) {
        name = name.replace(" ", "%20");
        SharedPreferences sharedPreferences=context.getSharedPreferences("content",Context.MODE_PRIVATE);
        String unit=sharedPreferences.getString("unit","F");
        String endpoint="";
        if (TextUtils.equals(unit,"F")) {
             endpoint = "http://api.openweathermap.org/data/2.5/weather?q=" + name;
        }else {
            endpoint = "http://api.openweathermap.org/data/2.5/weather?q=" + name + "&units=metric";
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(endpoint);

            endpoint = uriBuilder.toString();
            Log.e("LINK", endpoint);
            MyGetRequest request = new MyGetRequest(context, endpoint, null, responseSuccessListener, responseErrorListener);
            requestQueue.add(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void getCurrenWeatherLocation(long lat, long longture, Response.Listener<JSONObject> responseSuccessListener,
                                         Response.ErrorListener responseErrorListener) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("content",Context.MODE_PRIVATE);
        String unit=sharedPreferences.getString("unit","F");
        String endpoint="";
        if (TextUtils.equals(unit,"F")) {
            endpoint = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longture;
        }else {
            endpoint = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longture + "&units=metric";
        }
        try {
            URIBuilder uriBuilder = new URIBuilder(endpoint);

            endpoint = uriBuilder.toString();
            Log.e("LINK", endpoint);
            MyGetRequest request = new MyGetRequest(context, endpoint, null, responseSuccessListener, responseErrorListener);
            requestQueue.add(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void likeUnlikeFBPost(String objectId, boolean like,
                                 String facebookToken,
                                 Response.Listener<JSONObject> responseSuccessListener,
                                 Response.ErrorListener responseErrorListener, String extraData) {
        String endpoint = "https://graph.facebook.com/" + objectId
                + "/likes?method=" + (like ? "POST" : "DELETE")
                + "&access_token=" + facebookToken;
        MyPostRequest jr = new MyPostRequest(context, Method.GET, endpoint,
                new ArrayList<BasicNameValuePair>(), responseSuccessListener,
                responseErrorListener);
        requestQueue.add(jr);
    }

    // Get newsfeed comments
    public void getNewsFeedComments(String id, int limit,
                                    Response.Listener<JSONObject> responseSuccessListener,
                                    Response.ErrorListener responseErrorListener) {
        String url = "https://graph.facebook.com/fql?q=";
        String params = "{'comment_data':'select id,text,likes,fromid,user_likes,time from comment WHERE post_id = "
                + id
                + " LIMIT "
                + limit
                + "', 'user_data':'select uid,name,pic from user WHERE uid IN (SELECT fromid from #comment_data)'}";
        String endpoint = "";
        try {
            endpoint = url + URLEncoder.encode(params, "UTF-8").toString();
            System.err.println(endpoint);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        MyGetRequest jr = new MyGetRequest(context, endpoint, null,
                responseSuccessListener, responseErrorListener);
        requestQueue.add(jr);
    }
}
