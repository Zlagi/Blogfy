# Blogfy

[![Build (API)](https://github.com/Zlagi/blogfy-api/actions/workflows/run-build.yml/badge.svg)](https://github.com/Zlagi/blogfy-api/actions/workflows/run-build.yml)
[![Android Lint](https://github.com/Zlagi/Blogfy/actions/workflows/Lint.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Lint.yml)
[![Testing](https://github.com/Zlagi/Blogfy/actions/workflows/Testing.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Testing.yml)
[![Release](https://github.com/Zlagi/Blogfy/actions/workflows/Release.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Release.yml)

## 💡 About the Project

**Blogfy** is complete Kotlin-stack project that presents a modern approach to [Android](https://en.wikipedia.org/wiki/Android_(operating_system)) application development.

The goal of the project is to combine popular libraries/tools and demonstrate best developement practices by utilizing up to date tech-stack and presenting modern Android application [Architecture](#architecture) that is modular, scalable, maintainable, and testable.

This project is being maintained to match industry standards. Please check [CONTRIBUTING](CONTRIBUTING.md) page if you want to help.

### 🔹 [Blogfy API](/blogfy-api)

This is a *REST API* built using Ktor Framework deployed on *Heroku*.  
Navigate to the link in below 👇

https://github.com/Zlagi/blogfy-api.

### 🔹 [Blogfy Android Application](/blogfy-android)

***You can Install and test latest Blogfy Android app from below 👇***

[![Blogfy](https://img.shields.io/badge/Blogfy✅-APK-red.svg?style=for-the-badge&logo=android)](https://github.com/Zlagi/Blogfy/releases/tag/1)


The codebase containes the following features:

- [x] Basic and social media authentication
- [x] Bottom navigation bar, bottom sheet, loading and confirmation dialogs 🎨
- [x] Create, update, delete blog 
- [x] Push notifications. 
- [x] Fetch blogs with pagination support
- [x] Display blog search results using spring physics 💫
- [x] Expose search suggestions before making search and possibility to clear them all or individually 👀
- [x] Fetch user account properties periodically
- [x] Update user password
- [x] Refresh access token periodically (20 minutes lifetime) 🔄
- [x] Revoke refresh token when the user sign out
- [x] Tests (mockk and fakes)

## Screenshots ✨

## Built with 🛠

  - [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
  - [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
  - [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that               sequentially emits values and completes normally or with an exception.
  - [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust,           testable, and maintainable apps.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and       allows you to more easily write code that interacts with views.
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
  - [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - WorkManager is an API that makes it easy to schedule deferrable,       asynchronous tasks that are expected to run even if the app exits or the device restarts.
  - [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) Navigation refers to the interactions that allow users to     navigate across, into, and back out from the different pieces of content within your app.
  - [Jetpack Security](https://developer.android.com/topic/security/)
  - [Encrypted SharedPreference](https://developer.android.com/topic/security/data) - Used to store key-value data using encryption.
  - [Dependency Injection](https://developer.android.com/training/dependency-injection) - 
  - [Hilt-Dagger](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
  - [Hilt-ViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack) - DI for injecting `ViewModel`.
  - [Hilt-WorkManager](https://developer.android.com/training/dependency-injection/hilt-jetpack) - DI for injecting `WorkManager`.
  - [Assisted Inject with Dagger](https://github.com/square/AssistedInject) - Manually injected dependencies for your JSR 330 configuration.
  - [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
  - [Moshi](https://github.com/square/moshi) - A modern JSON library for Kotlin and Java.
  - [Moshi Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/moshi) - A Converter which uses Moshi for serialization to and from       JSON.
  - [OkHttp](https://square.github.io/okhttp/) - A library developed by Square for sending and receive HTTP-based network requests
  - [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI             components for Android.
  - [LeakCanary](https://square.github.io/leakcanary/) - Memory leak detection library for Android.
  - [Truth](https://truth.dev/) - Performing assertions in tests.
  - [Turbine](https://cashapp.github.io/turbine/) - Testing library for kotlinx.coroutines Flow.
  - [Mockk](https://mockk.io/) - Mocking library for Kotlin.
  - [Robolectric](https://mockk.io/) - Robolectric is a framework that brings fast and reliable unit tests to Android. Tests run inside the JVM on your             workstation in seconds.
  - [Firebase Storage](https://firebase.google.com/docs/storage) - Cloud Storage for Firebase is a powerful, simple, and cost-effective object storage service     built for Google scale.
  - [Firebase Authentication](https://firebase.google.com/docs/auth) - Provides backend services, easy-to-use SDKs, and ready-made UI libraries to authenticate     users to your app.
  - [Firebase Crashlytics](https://firebase.google.com/products/crashlytics) - Tracks, prioritizes & fixes stability issues that erode app quality
  - [Coil](https://coil-kt.github.io/coil/) - An image loading library for Android backed by Kotlin Coroutines.
  - [Lottie](http://airbnb.io/lottie/) - Render After Effects animations.
  - [CanHub](https://github.com/CanHub/Android-Image-Cropper) - Android image cropper.
  - [Firecoil](https://github.com/thatfiredev/firecoil) - Load images from Cloud Storage for Firebase in your Android app (through a StorageReference).
  - [One Signal](https://onesignal.com/) - For push notifications.
  - [Kluent ](https://github.com/MarkusAmshove/Kluent) - "Fluent Assertions" library written specifically for Kotlin.

## CI pipeline

CI is utilizing [GitHub Actions](https://github.com/features/actions). Complete GitHub Actions config is located in the [.github/workflows](.github/workflows) folder.

### PR Verification

Series of workflows runs (in parallel) for every opened PR and after merging PR to `main` branch:
* `./gradlew lintDebug` - runs Android lint
* `./gradlew testDebugUnitTest` - run unit tests

## Upcoming improvements
- Increase tests coverage in the data layer.
- Improve code quality

## Known issues
- Leaky memory: this issue produced when the user navigate from FeedFrgment to BlogDetailFragment before the image loading is complete and vice-versa.
- search query is not saved when the orientation is changed.
- app crashing when the bottom sheet is displayed and the orientation is changed.
- social media authentication seems not working properly

## Inspiration

This is project is a sample, to inspire you and should handle most of the common cases, but please take a look at
additional resources.

### Android projects

Other high-quality projects will help you to find solutions that work for your project:

- [NotyKT](https://github.com/PatilShreyas/NotyKT)
- [MVI-Clean-Architecture](https://github.com/yusufceylan/MVI-Clean-Architecture)
- [Filmatic](https://github.com/prof18/Filmatic)
- [Open-API-Android-App](https://github.com/mitchtabian/Open-API-Android-App)
- [Taskify](https://github.com/Vaibhav2002/Taskify)

## Contribute

Want to contribute? Check our [Contributing](CONTRIBUTING.md) docs.
