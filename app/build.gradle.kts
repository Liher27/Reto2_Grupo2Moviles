plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Added KSP plugin for Room
}

android {
    namespace = "com.example.reto2_grupo2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.reto2_grupo2"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core and Compatibility Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Google Maps
    implementation(libs.google.maps.sdk)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room (using KSP for better performance)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    ksp("androidx.room:room-compiler:${libs.versions.roomKtx.get()}") // Use the Room KSP compiler

    // Network and JSON
    implementation(libs.socket.io.client)
    implementation(libs.engine.io.client)
    implementation(libs.gson)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Desugar JDK Libraries for backward compatibility
    coreLibraryDesugaring(libs.desugar.jdk.libs)



}
