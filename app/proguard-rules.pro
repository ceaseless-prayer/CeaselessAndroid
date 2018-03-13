# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/uberx/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
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

# renderscript support
# http://stackoverflow.com/questions/22161832/renderscript-support-library-crashes-on-x86-devices
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class android.support.v8.renderscript.** { *; }

# Realm support
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

# OneSignal support
-dontwarn com.onesignal.**

-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

-keep public interface android.app.OnActivityPausedListener {*;}
-keep class com.onesignal.ActivityLifecycleListenerCompat** {*;}

-keep class com.onesignal.OSSubscriptionState {
    void changed(com.onesignal.OSPermissionState);
}

-keep class com.onesignal.OSPermissionChangedInternalObserver {
    void changed(com.onesignal.OSPermissionState);
}

-keep class com.onesignal.OSSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSSubscriptionState);
}

-keep class ** implements com.onesignal.OSPermissionObserver {
    void onOSPermissionChanged(com.onesignal.OSPermissionStateChanges);
}

-keep class ** implements com.onesignal.OSSubscriptionObserver {
    void onOSSubscriptionChanged(com.onesignal.OSSubscriptionStateChanges);
}

-keep class com.onesignal.shortcutbadger.impl.AdwHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.ApexHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.AsusHomeLauncher { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.DefaultBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.EverythingMeHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.HuaweiHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.LGHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.NewHtcHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.NovaHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.OPPOHomeBader { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.SamsungHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.SonyHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.VivoHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.XiaomiHomeBadger { <init>(...); }
-keep class com.onesignal.shortcutbadger.impl.ZukHomeBadger { <init>(...); }

-dontwarn com.amazon.**

# Proguard ends up removing this class even if it is used in AndroidManifest.xml so force keeping it.
-keep public class com.onesignal.ADMMessageHandler {*;}
-keep class com.onesignal.JobIntentService$* {*;}