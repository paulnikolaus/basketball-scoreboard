plugins {
    // Standard Android Application plugin
    alias(libs.plugins.android.application)
    // Kotlin Compose compiler plugin for UI development
    alias(libs.plugins.kotlin.compose)
}

android {
    // Unique identifier for the app package
    namespace = "com.paulnikolaus.scoreboard"

    // Compiling against the latest Android 15 / API 36 (Release 36)
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.paulnikolaus.scoreboard"

        // Minimum SDK set to 26 (Android 8.0 Oreo) for modern API support
        minSdk = 26
        targetSdk = 36

        // Version tracking for Play Store releases
        versionCode = 5
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Minification/Obfuscation is disabled for now
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Set Java 11 for compatibility with modern Android builds
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Enables the Jetpack Compose UI framework
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- UI & Jetpack Compose ---
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons.extended) // Large set of icons for settings
    implementation(libs.androidx.compose.material3) // Modern Material Design components
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // --- Core Libraries ---
    implementation(libs.androidx.core.ktx) // Essential Kotlin extensions for Android
    implementation(libs.androidx.datastore.preferences) // For persisting theme/settings
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle-aware coroutine support
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel integration with Compose

    // BOM (Bill of Materials) ensures all Compose libraries use compatible versions
    implementation(platform(libs.androidx.compose.bom))

    // --- Unit Testing (JVM) ---
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test) // Testing asynchronous code/timers
    testImplementation(libs.turbine) // Clean way to test Flow emissions (like timer updates)

    // --- Instrumented Testing (Device/Emulator) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- Debugging Tools ---
    debugImplementation(libs.androidx.compose.ui.tooling) // Layout inspector support
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}