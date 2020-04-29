# Ceaseless for Android [![Build Status](https://travis-ci.org/ceaseless-prayer/CeaselessAndroid.svg?branch=master)](https://travis-ci.org/ceaseless-prayer/CeaselessAndroid)
The Ceaseless Prayer app for Android will be built natively using material design.

# Download on the Play Store

You can download the app on the [Play Store](https://play.google.com/store/apps/details?id=org.theotech.ceaselessandroid)

# Translating the app

In order to translate the app it's not necessary to install anything on your computer - you can just use the translation platform [lokalise](https://app.lokalise.com/project/338603745bd8cae0042d51.84066670/) and we'll take care of the rest.

# Android Setup

You may want to install [Android Studio](http://developer.android.com/tools/studio/index.html).

# Working on the app

Coming soon.

# Running in the emulator

Make sure you have the tools directory for the Android SDK in your path, e.g. `/Users/your-user-name/Library/Android/sdk/tools`.

* Run the Android Virutal Device Manager: `android avd`
* Run your Android virtual device (you should have created one via Android Setup directions linked above)

# Releasing

To build a release APK, you'll need the release.keystore from https://bitbucket.org/chrislim/ceaseless-keys. From there, using Android Studio go to: Build -> Generate Signed APK... to start the process. The keystore is protected by a password so you'll need that too.
