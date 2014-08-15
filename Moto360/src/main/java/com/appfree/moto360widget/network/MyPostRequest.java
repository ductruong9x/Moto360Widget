package com.appfree.moto360widget.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import khandroid.ext.apache.http.message.BasicNameValuePair;


public class MyPostRequest extends Request<JSONObject> {

	private List<BasicNameValuePair> params; // the request params
	private Response.Listener listener; // the response listener
    private Context context;

	public MyPostRequest(Context context, int requestMethod, String url, List<BasicNameValuePair> params, Response.Listener responseListener, Response.ErrorListener errorListener) {
		super(requestMethod, url, errorListener, url);
        this.context = context;
		// TODO Auto-generated constructor stub
        FakeX509TrustManager.allowAllSSL();
		this.params = params;
		this.listener = responseListener;
        this.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
            HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		// TODO Auto-generated method stub
		if(listener != null){
			listener.onResponse(response, extraData);
		}
	}

	@Override
	public Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> map = new HashMap<String, String>();
		for (BasicNameValuePair pair : params) {
			map.put(pair.getName(), pair.getValue());
		}
		return map;
	}

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
   //     headers.put("User-agent", Util.getUserAgent(context));
        return headers;
    }
}
