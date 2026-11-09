# IT Professional Toolkit

A standalone, offline-first Android companion for people learning and doing first-line IT support. It is being rebuilt as a native application rather than an APK-generator or a web wrapper.

## Product direction

Most mobile IT toolkits concentrate on network diagnostics: scanning, ping, DNS, port checks and similar utilities. This project takes a different centre of gravity: **helping a support technician think clearly, record what was actually checked, and hand over a problem in a useful way**.

The product voice should feel calm, practical and respectful. It should explain what a check is for, distinguish an observation from a diagnosis, and never pretend that an unrun test has passed. It is intended to support good professional judgement, not replace an organisation's procedures or an experienced engineer.

## Current MVP

- Native Android interface built with Kotlin and Jetpack Compose.
- IPv4 subnet calculator with common CIDR presets and explicit `/31` point-to-point and `/32` single-address handling.
- Subnet calculator unit tests covering typical, boundary and invalid input cases.
- Common service-port reference.
- First-line troubleshooting checklists for connectivity, slow computers and sign-in problems.
- Guided support case: capture the reported issue, business impact, checks, evidence and next action.
- Copyable, structured handover summary generated from the support case.
- Local work notes persisted on the device.
- No backend, account system, analytics, advertising or network permission in this MVP.

## What makes this project distinct

The differentiator is the workflow joining **symptom → evidence → next action → handover**, rather than the presence of a subnet calculator alone. Network calculators and port references are established, widely used categories; this project does not claim to have invented them. The product should earn its identity through its support-case workflow, plain-language guidance, responsible handling of information, and usefulness to entry-level support staff.

## Originality and third-party material

The application code, screen copy and workflow in this repository are being written specifically for this project. No competitor's source code, branding, screenshots or proprietary assets are intentionally reused. Common technical facts (for example, standard port numbers and IPv4 subnet mathematics) are factual reference material, not a claim of exclusive ownership. This statement is a development record, not a legal guarantee or a substitute for a formal code, asset and licence review before publication.

## Build

Requirements: Android Studio with Android SDK Platform 36 and JDK 17. Open this repository in Android Studio, allow Gradle sync, then run the app configuration on an Android 8.0 (API 26) or newer device/emulator.

Run unit tests with `gradle testDebugUnitTest`. For a local debug build after Gradle is available, run `gradle assembleDebug`. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

For Google Play, create a signed Android App Bundle (AAB) from Android Studio's **Build > Generate Signed Bundle / APK** workflow. Check the current Play Console target API, testing, privacy and Data safety requirements at submission time. Do not publish a debug build.

## Privacy and data handling

Notes are stored locally using Android SharedPreferences. This initial version does not transmit note content. The app currently requests no internet permission. Do not store passwords, verification codes, personal data or confidential organisational incident details in notes. Review the handover before sharing it and follow the organisation's data-handling rules.

## Product status

This is an initial MVP foundation, not a verified Play Store release. Automated tests, accessibility review, device testing, release signing, app icon, screenshots, privacy-policy review and Play Console compliance remain before publication.
