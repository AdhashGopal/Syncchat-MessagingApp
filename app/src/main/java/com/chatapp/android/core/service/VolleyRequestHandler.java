package com.chatapp.android.core.service;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 10/6/2016.
 */
public class VolleyRequestHandler {
    private static Context context;
    private RequestQueue mRequestQueue;
    static VolleyRequestHandler instance;
    public static final String TAG = VolleyRequestHandler.class.getSimpleName();

    /**
     * create constructor
     */
    public VolleyRequestHandler() {
    }

    /**
     * Call volley request handler(web service)
     *
     * @param context current class
     * @return web service value
     */
    public static VolleyRequestHandler getInstance(Context context) {
        VolleyRequestHandler.context = context;
        if (instance == null) {
            instance = new VolleyRequestHandler();
//            disableSSLCertificateChecking();
        }
        return instance;
    }

    /**
     * get Request Queue for volley
     *
     * @return response value(RequestQueue)
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    /**
     * syntax for add To Request Queue
     *
     * @param req input value(req)
     * @param <T> input value(Dynamic value)
     */
    public <T> void addToRequestQueue(Request<T> req) {
        try {

            req.setTag(TAG);
            getRequestQueue().add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
        private SSLSocketFactory getSocketFactory() {

            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = getResources().openRawResource(R.raw.server);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }


                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);


                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);


                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {

                        Log.e("CipherUsed", session.getCipherSuite());
                        return hostname.compareTo("192.168.1.10")==0; //The Hostname of your server

                    }
                };


                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                SSLContext context = null;
                context = SSLContext.getInstance("TLS");

                context.init(null, tmf.getTrustManagers(), null);
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

                SSLSocketFactory sf = context.getSocketFactory();


                return sf;

            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            return  null;
        }
    */
    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
