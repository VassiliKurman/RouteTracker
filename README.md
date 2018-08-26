# RouteTracker

This is Project 7 in the Android Developer Nanodegree programme developwed by Udacity in partnership with Google: Capstone Project Stage 2.
Project requirements document from stage 1 can be found in: [Capstone-Project](https://github.com/VassiliKurman/Capstone-Project)

## Project Specification

Capstone, Stage 2 - Build

## Common Project Requirements
* App conforms to common standards found in the Android Nanodegree General Project Guidelines
* App is written solely in the Java Programming Language
* App utilizes stable release versions of all libraries, Gradle, and Android Studio.

## Core Platform Development
* App integrates a third-party library.
* App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash.
* App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
* App keeps all strings in a `strings.xml` file and enables RTL layout switching on all layouts.
* App provides a widget to provide relevant information to the user on the home screen.

## Google Play Services

* App integrates two or more Google services. Google service integrations can be a part of Google Play Services or Firebase.
* Each service imported in the `build.gradle` is used in the app.
* If `Location` is used, the app customizes the user’s experience by using the device's location.
* If `Admob` is used, the app displays test ads. If `Admob` was not used, student meets specifications.
* If `Analytics` is used, the app creates only one analytics instance. If `Analytics` was not used, student meets specifications.
* If `Maps` is used, the map provides relevant information to the user. If `Maps` was not used, student meets specifications.
* If `Identity` is used, the user’s identity influences some portion of the app. If `Identity` was not used, student meets specifications.

## Material Design
* App theme extends `AppCompat`.
* App uses an app bar and associated toolbars.
* App uses standard and simple transitions between activities.

## Building
* App builds from a clean repository checkout with no additional configuration.
* App builds and deploys using the `installRelease` `Gradle` task.
* App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
* All app dependencies are managed by `Gradle`.

## Data Persistence
* App stores data locally either by implementing a ContentProvider OR using Firebase Realtime Database OR using Room. No third party frameworks nor Persistence Libraries may be used.
* Must implement at least __one__ of the three:
  * If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter or JobDispacter.
  * If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so.
  * It it performs short duration, on-demand requests(such as search), app uses an AsyncTask.
* If Content provider is used, the app uses a Loader to move its data to its views.
* If Room is used then LiveData and ViewModel are used when required and no unnecessary calls to the database are made.

## Project setup
* google-services.json file needed to put in RouteTracker/app/src folder to work with Firebase
* api_keys.xml file needed in res/values package to work with Google Maps API in following format: ```<resources><string name="GEO_API_KEY">YourKeyHere</string></resources>```
