package com.android.volley;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
public class MyHttpClient {

	final Context context=null;


	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
//	private SSLSocketFactory newSslSocketFactory() {
//		try {
//			// Get an instance of the Bouncy Castle KeyStore format
//			KeyStore trusted = KeyStore.getInstance("BKS");
//			// Get the raw resource, which contains the keystore with
//			// your trusted certificates (root and any intermediate certs)
//			InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
//			try {
//				// Initialize the keystore with the provided trusted
//				// certificates
//				// Also provide the password of the keystore
//				trusted.load(in, "123465".toCharArray());
//			} finally {
//				in.close();
//			}
//			// Pass the keystore to the SSLSocketFactory. The factory is
//			// responsible
//			// for the verification of the server certificate.
//			SSLSocketFactory sf = new SSLSocketFactory(trusted);
//			// Hostname verification from certificate
//			// http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
//			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//			return sf;
//		} catch (Exception e) {
//			throw new AssertionError(e);
//		}
//	}
}
