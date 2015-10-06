# Ceaseless for Android [![Build Status](https://travis-ci.org/ceaseless-prayer/CeaselessAndroid.svg?branch=master)](https://travis-ci.org/ceaseless-prayer/CeaselessAndroid)
The Ceaseless Prayer app for Android will be built natively using material design.

# Android Setup

You may want to install [Android Studio](http://developer.android.com/tools/studio/index.html).

# Working on the app

Coming soon.

# Running in the emulator

Make sure you have the tools directory for the Android SDK in your path, e.g. `/Users/your-user-name/Library/Android/sdk/tools`.

* Run the Android Virutal Device Manager: `android avd`
* Run your Android virtual device (you should have created one via Android Setup directions linked above)

# Releasing

To build a release APK, you'll need the release.keystore from https://bitbucket.org/chrislim/ceaseless-keys. From there, using Android Studio go to: Build -> Generate Signed APK... to start the process. The keystore is protected by a password so you'll that too.
