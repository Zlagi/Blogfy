# Blogfy

[![Build (API)](https://github.com/Zlagi/blogfy-api/actions/workflows/run-build.yml/badge.svg)](https://github.com/Zlagi/blogfy-api/actions/workflows/run-build.yml)
[![Android lint](https://github.com/Zlagi/Blogfy/actions/workflows/Lint.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Lint.yml)
[![Testing](https://github.com/Zlagi/Blogfy/actions/workflows/Testing.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Testing.yml)
[![Release](https://github.com/Zlagi/Blogfy/actions/workflows/Release.yml/badge.svg)](https://github.com/Zlagi/Blogfy/actions/workflows/Release.yml)

**Blogfy** is a complete Kotlin-stack blog post taking 🖊️ application 📱 built to demonstrate a use of Kotlin programming language in *server-side* and *Modern Android development* tools. Dedicated to all Android Developers with ❤️. 

## 💡 About the Project

### 🔹 [Blogfy API](/blogfy-api)

This is a *REST API* built using Ktor Framework deployed on *Heroku*.  
Navigate to the link in below 👇

https://github.com/Zlagi/blogfy-api.

### 🔹 [Blogfy Android Application](/blogfy-android)

***You can Install and test latest Blogfy Android app from below 👇***

[![Blogfy](https://img.shields.io/badge/Blogfy✅-APK-red.svg?style=for-the-badge&logo=android)](https://github.com/Zlagi/Blogfy/releases/tags)


The codebase containes the following features:

- [x] Basic and social media authentication.
- [x] Bottom navigation bar, bottom sheet, loading and confirmation dialogs. 🎨
- [x] Create, update, delete blog.
- [x] Push notifications.
- [x] Fetch blogs with pagination support.
- [x] Animate blog search results using spring physics. 💫
- [x] Expose search suggestions before making search and possibility to clear them all or individually. 👀
- [x] Fetch user account properties periodically.
- [x] Update user password.
- [x] Refresh access token periodically (20 minutes). 🔄
- [x] Revoke refresh token when the user sign out.
- [x] Tests (mockk for viewModels and dataSources, fakes for repository *for demonstration purposes*).

## Screenshots ✨

<div align="center">
  <img src="https://user-images.githubusercontent.com/63319103/160729444-465fc255-c887-4117-8092-9db08726a1b7.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160729653-a896cc1a-327d-426b-bf0a-15982303e041.png" width="230px" />  
  <img src="https://user-images.githubusercontent.com/63319103/160729716-761b2a07-3d3b-4345-bdbf-a2a30aed9daa.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160729893-42b5974c-92ab-4741-8227-cfd282562e66.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730896-269e954e-d48c-4163-87a1-183bb15bd068.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160729978-46b19ef2-62c5-47e1-be6d-4a7581ca63b3.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730232-ce4795f4-83e7-47dc-97ba-9422357d9003.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730390-4eefa837-2b37-4c84-b766-a124dd5bc52b.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730511-be3146f1-1109-46bf-971f-44bc2c54cca4.png" width="230px" />  
  <img src="https://user-images.githubusercontent.com/63319103/160730582-32884cf2-dff7-4ace-9e84-ac376aad4a63.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730682-342c3918-8528-413c-956b-2a73777df5a7.png" width="230px" />
  <img src="https://user-images.githubusercontent.com/63319103/160730761-26ba7a6b-b789-45ae-b021-5f8def46a92a.png" width="230px" /> 
</div>

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
  - [Jetpack Security](https://developer.android.com/topic/security/) Provides abstractions for encrypting and decrypting SharedPreferences and Files.
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
  - [One Signal](https://onesignal.com/) - An Api for Push Notifications, Email, SMS & In-App...
  - [Kluent ](https://github.com/MarkusAmshove/Kluent) - "Fluent Assertions" library written specifically for Kotlin.

## CI pipeline

CI is utilizing [GitHub Actions](https://github.com/features/actions). Complete GitHub Actions config is located in the [.github/workflows](.github/workflows) folder.

### PR Verification

Series of workflows runs (in parallel) for every opened PR and after merging PR to `main` branch:
* `./gradlew lintDebug` - runs Android lint
* `./gradlew testDebugUnitTest` - run unit tests

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
