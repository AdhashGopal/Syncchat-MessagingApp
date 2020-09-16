# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\administrator\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include localPath and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# without progaurd old
#-dontwarn org.webrtc.*
#-dontwarn com.google.vending.*
#-dontwarn com.fasterxml.*
#-dontwarn android.net.*
#-dontwarn org.apache.http

# 13-11-2019


-dontwarn com.google.vending.*
-dontwarn com.fasterxml.*
-dontwarn android.net.*

-dontwarn com.github.siyamed.shapeimageview.**
-dontwarn com.google.protobuf.**
-dontwarn com.google.common.**
-dontwarn com.simplecityapps.**
-dontwarn org.codehaus.**
-dontwarn rx.internal.**

-keep class com.chatapp.synchat.core.model.**{*;}
#payload
-keep class com.achat.lib.model.**{*;}

-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }

-keep class com.loopj.android.** { *; }
-keep interface com.loopj.android.** { *; }

-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }

-keep class org.apache.http.** { *; }

# Viewpager indicator
-dontwarn com.viewpagerindicator.**

# Support v7, Design
-keep class android.support.v7.widget.RoundRectDrawable { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.**
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#Volley
-dontwarn com.android.volley.**
-dontwarn com.android.volley.error.**
-keep class com.android.volley.** { *; }
-keep class com.android.volley.toolbox.** { *; }
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.*
-keep class com.android.volley.toolbox.ImageLoader { *; }


#Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Picasso
-dontwarn com.squareup.okhttp.**

#Card
-keep class android.support.v7.widget.RoundRectDrawable { *; }

### Fabric
# In order to provide the most meaningful crash reports
-keepattributes SourceFile,LineNumberTable
# If you're using custom Eception
-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

### Android Architecture Components
-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#EventBus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#Shortcutbadger
-keep class me.leolin.shortcutbadger.impl.** { <init>(...); }

#Jsoup
-keep public class org.jsoup.** {
public *;
}

#Rootbeer
-keep class com.scottyab.rootbeer.** { *; }
-dontwarn com.scottyab.rootbeer.**


#Exoplayer
-keep class com.google.android.exoplayer.** {*;}

#Wang.avi:library
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

#Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep public class * extends java.lang.Exception

#Collapsingtoolbarlayout
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}


-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#IO Exception
-dontwarn com.na.**
-keep class com.na.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-printmapping mapping.txt
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}

-keep class com.shopify.** { *; }
-dontwarn com.shopify.**

-dontoptimize
-dontpreverify

-ignorewarnings

# default & basic optimization configurations
-optimizationpasses 5
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

#Gson
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }

#WebRtc
-dontwarn android.support.**
-keep class org.webrtc.**  { *; }
-keep class org.appspot.apprtc.**  { *; }
-keep class de.tavendo.autobahn.**  { *; }
-keepclasseswithmembernames class * { native <methods>; }




