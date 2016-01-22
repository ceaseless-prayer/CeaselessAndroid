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