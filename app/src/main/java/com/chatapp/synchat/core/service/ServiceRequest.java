package com.chatapp.synchat.core.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * * created by  Adhash Team on 10/5/2016.
 */
public class ServiceRequest {

    private String TAG = ServiceRequest.class.getCanonicalName();
    private Context context;
    private ServiceListener mServiceListener;
    private ServiceRequestListener serviceRequestListener;
    private StringRequest stringRequest;

    public ServiceRequest(Context context) {
        this.context = context;
    }

    /**
     * make webservice for request & getting response and if anything wrong means throw error
     *
     * @param url      input value(url)
     * @param method   input value(based on method to call API (GET,POST,etc,.))
     * @param param    input value(based on input key value)
     * @param listener (call back)
     */
    public void makeServiceRequest(final String url, int method, final HashMap<String, String> param, ServiceListener listener) {

        this.mServiceListener = listener;
        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mServiceListener.onCompleteListener(response);
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int state = ServiceListener.NOCONNECTIONERROR;
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    state = ServiceListener.NOCONNECTIONERROR;
                } else if (error instanceof AuthFailureError) {
                    state = ServiceListener.AUTHFAILUREERROR;
                } else if (error instanceof ServerError) {
                    state = ServiceListener.SERVERERROR;
                } else if (error instanceof NetworkError) {
                    state = ServiceListener.NETWORKERROR;
                } else if (error instanceof ParseError) {
                    state = ServiceListener.PARSEERROR;
                }
                mServiceListener.onErrorListener(state);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, param.toString());
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }

        };
        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * based on error code will throw error message
     * Response  / error method
     */
    public interface ServiceListener {
        int NOCONNECTIONERROR = 0;
        int AUTHFAILUREERROR = 1;
        int SERVERERROR = 2;
        int NETWORKERROR = 3;
        int PARSEERROR = 4;

        void onCompleteListener(String response);

        void onErrorListener(int state);
    }

    /**
     * make webservice for request & getting response and if anything wrong means throw error
     *
     * @param url        input value(url)
     * @param inputValue input value(inputValue)
     * @param method     input value(based on method to call API (GET,POST,etc,.))
     * @param param      input value(based on input key value)
     * @param listener   (call back)
     */
    public void makeServiceRequest(final String url, int method, final String inputValue, final HashMap<String, String> param, ServiceRequestListener listener) {

        this.serviceRequestListener = listener;
        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    serviceRequestListener.onCompleteListener(response, inputValue);
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int state = ServiceRequestListener.NOCONNECTIONERROR;
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    state = ServiceRequestListener.NOCONNECTIONERROR;
                } else if (error instanceof AuthFailureError) {
                    state = ServiceRequestListener.AUTHFAILUREERROR;
                } else if (error instanceof ServerError) {
                    state = ServiceRequestListener.SERVERERROR;
                } else if (error instanceof NetworkError) {
                    state = ServiceRequestListener.NETWORKERROR;
                } else if (error instanceof ParseError) {
                    state = ServiceRequestListener.PARSEERROR;
                }
                serviceRequestListener.onErrorListener(state);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, param.toString());
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }

        };
        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * based on error code will throw error message
     * Response  / error method
     */
    public interface ServiceRequestListener {
        int NOCONNECTIONERROR = 0;
        int AUTHFAILUREERROR = 1;
        int SERVERERROR = 2;
        int NETWORKERROR = 3;
        int PARSEERROR = 4;

        void onCompleteListener(String response, String inputValue);

        void onErrorListener(int state);
    }

}
