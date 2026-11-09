# IT Professional Toolkit

A standalone, offline-first Android application for IT support and junior cybersecurity workflows. This repository is being rebuilt from the former non-functional APK-generator prototype.

## Current MVP

- Native Android UI built with Kotlin and Jetpack Compose.
- IPv4 subnet calculator: network, mask, broadcast, host range and host count.
- Quick reference for common service ports.
- Interactive first-line troubleshooting checklists.
- Local work notes persisted on the device.
- No backend, account system, analytics, advertising or network permission in this MVP.

## Build

Requirements: Android Studio with Android SDK Platform 36 and JDK 17. Open this repository in Android Studio, allow Gradle sync, then run the app configuration on an Android 8.0 (API 26) or newer device/emulator.

For a local debug build after Gradle is available, run gradle assembleDebug. The debug APK is written to app/build/outputs/apk/debug/app-debug.apk.

For Google Play, create a signed Android App Bundle (AAB) from Android Studio's Build > Generate Signed Bundle / APK workflow. Check the current Play Console target API, testing, privacy and Data safety requirements at submission time. Do not publish a debug build.

## Privacy and data handling

Notes are stored locally using Android SharedPreferences. This initial version does not transmit note content. The app currently requests no internet permission. Do not store passwords, special-category personal data, or confidential organisational incident records in notes.

## Product status

This is an initial MVP foundation, not a verified Play Store release. Automated tests, accessibility review, device testing, release signing, app icon, screenshots, privacy-policy review and Play Console compliance remain before publication.
