package com.chatapp.android.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.chatapphelperclass.ChatappFontUtils;
import com.crashlytics.android.Crashlytics;


import io.fabric.sdk.android.Fabric;
//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;


/**
 *
 */
public class CoreController extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = CoreController.class
            .getSimpleName();

    private static CoreController mInstance;
    public static MessageDbController mMessageDbInstance;
    public static ContactDB_Sqlite contactDB_sqliteInstance;
    private RequestQueue mRequestQueue;
    private SessionManager sessionmgr;
    private ImageLoader mImageLoader;
    private Context mcontext;

    private Typeface avnNextLTProRegTypeface, avnNextLTProDemiTypeface;
    private Typeface robotoRegularTypeFace, robotoMediumTypeFace;

    private Typeface whatsappRegularTypeFace, whatsappBoldTypeFace;

    /**
     * Fabric inilization, SessionManager, database calling
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        PayloadLibrary.init(this);

        mInstance = this;
        mcontext = this;
        sessionmgr = SessionManager.getInstance(mcontext);

        if (sessionmgr.isLoginKeySent()) {
            mMessageDbInstance = new MessageDbController(mcontext);
            contactDB_sqliteInstance = new ContactDB_Sqlite(mcontext);
        }

        registerActivityLifecycleCallbacks(this);

      /*  PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();*/

        try {
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) {
                }
            }) {
                @Override
                public void flush() {
                }

                @Override
                public void close() {
                }

                @Override
                public void write(int b) {
                }

                @Override
                public void write(byte[] b) {
                }

                @Override
                public void write(byte[] buf, int off, int len) {
                }

                @Override
                public void print(boolean b) {
                }

                @Override
                public void print(char c) {
                }

                @Override
                public void print(int i) {
                }

                @Override
                public void print(long l) {
                }

                @Override
                public void print(float f) {
                }

                @Override
                public void print(double d) {
                }

                @Override
                public void print(char[] s) {
                }

                @Override
                public void print(String s) {
                }

                @Override
                public void print(Object obj) {
                }

                @Override
                public void println() {
                }

                @Override
                public void println(boolean x) {
                }

                @Override
                public void println(char x) {
                }

                @Override
                public void println(int x) {
                }

                @Override
                public void println(long x) {
                }

                @Override
                public void println(float x) {
                }

                @Override
                public void println(double x) {
                }

                @Override
                public void println(char[] x) {
                }

                @Override
                public void println(String x) {
                }

                @Override
                public void println(Object x) {
                }

                @Override
                public java.io.PrintStream printf(String format, Object... args) {
                    return this;
                }

                @Override
                public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) {
                    return this;
                }

                @Override
                public java.io.PrintStream format(String format, Object... args) {
                    return this;
                }

                @Override
                public java.io.PrintStream format(java.util.Locale l, String format, Object... args) {
                    return this;
                }

                @Override
                public java.io.PrintStream append(CharSequence csq) {
                    return this;
                }

                @Override
                public java.io.PrintStream append(CharSequence csq, int start, int end) {
                    return this;
                }

                @Override
                public java.io.PrintStream append(char c) {
                    return this;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Text fount style
     *
     * @return
     */
    public Typeface getAvnNextLTProRegularTypeface() {
        if (avnNextLTProRegTypeface == null) {
            //  avnNextLTProRegTypeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            avnNextLTProRegTypeface = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappFontStyle());
        }
        return avnNextLTProRegTypeface;
    }

    /**
     * Text fount style
     *
     * @return
     */
    public Typeface getAvnNextLTProBoldTypeface() {
        if (avnNextLTProRegTypeface == null) {
            avnNextLTProRegTypeface = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappFontStyle());
        }
        return avnNextLTProRegTypeface;
    }

    /**
     * Text fount style
     *
     * @return
     */
    public Typeface getAvnNextLTProDemiTypeface() {
        if (avnNextLTProDemiTypeface == null) {
            avnNextLTProDemiTypeface = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappBoldFontStyle());
        }
        return avnNextLTProDemiTypeface;
    }


    public Typeface getRobotoRegularTypeFace() {
        if (robotoRegularTypeFace == null) {
            robotoRegularTypeFace = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappFontStyle());
        }
        return robotoRegularTypeFace;
    }

    public Typeface getRobotoMediumTypeFace() {
        if (robotoMediumTypeFace == null) {
            robotoMediumTypeFace = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappFontStyle());
        }
        return robotoMediumTypeFace;
    }

    /**
     * Text fount style
     *
     * @return
     */
    public Typeface getWhatsappRegularTypeFace() {
        if (whatsappRegularTypeFace == null) {
            whatsappRegularTypeFace = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappFontStyle());
        }
        return whatsappRegularTypeFace;
    }

    /**
     * Text fount style
     *
     * @return
     */
    public Typeface getWhatsappBoldTypeFace() {
        if (whatsappBoldTypeFace == null) {
            whatsappBoldTypeFace = Typeface.createFromAsset(getAssets(), ChatappFontUtils.getWhatsappBoldFontStyle());
        }
        return whatsappBoldTypeFace;
    }

    /**
     * database object creation
     *
     * @param context
     * @return
     */
    synchronized public static ContactDB_Sqlite getContactSqliteDBintstance(Context context) {
        if (contactDB_sqliteInstance == null) {
            contactDB_sqliteInstance = new ContactDB_Sqlite(context);
        }
        return contactDB_sqliteInstance;
    }

    /**
     * Image loader
     *
     * @return response value
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * Web service (Volley inilization)
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    /**
     * synchronized data
     *
     * @return response
     */
    public static synchronized CoreController getInstance() {
        return mInstance;
    }

    /**
     * get database controller
     *
     * @param context
     * @return
     */
    public static synchronized MessageDbController getDBInstance(Context context) {
        if (mMessageDbInstance == null) {
            mMessageDbInstance = new MessageDbController(context);
        }
        return mMessageDbInstance;
    }


    /**
     * HAndling the Activity lifecycle
     *
     * @param activity current activity
     * @param bundle   pass the data
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        SessionManager.getInstance(CoreController.this).setApplicationPauseState("0");
    }

    /**
     * Handling the Activity lifecycle
     *
     * @param activity current activity
     */
    @Override
    public void onActivityDestroyed(Activity activity) {
        SessionManager.getInstance(CoreController.this).setApplicationPauseState("1");
    }

    /**
     * Handling the Activity lifecycle
     *
     * @param activity current activity
     */
    @Override
    public void onActivityPaused(Activity activity) {
        SessionManager.getInstance(CoreController.this).setApplicationPauseState("1");
    }

    /**
     * Handling the Activity lifecycle
     *
     * @param activity current activity
     */
    @Override
    public void onActivityResumed(Activity activity) {
        SessionManager.getInstance(CoreController.this).setApplicationPauseState("0");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity,
                                            Bundle outState) {

    }

    /**
     * Handling the Activity lifecycle
     *
     * @param activity current activity
     */
    @Override
    public void onActivityStarted(Activity activity) {
        SessionManager.getInstance(CoreController.this).setApplicationPauseState("0");
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    /**
     * Handling MultiDex function
     *
     * @param base current activity
     *
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);


        MultiDex.install(this);
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    /**
     * set database instance
     * @param context current activity
     */
    public static void setDBInstance(Context context) {
        mMessageDbInstance = new MessageDbController(context);
    }


    private MessageDbController sqlDBInstance;

}
