plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ecosnapwireframe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecosnapwireframe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        resValue("string", "API_KEY", project.hasProperty("API_KEY").toString())
       // buildConfigField("String", "MAPS_API_KEY",  project.hasProperty("API_KEY").toString())

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

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-maps:19.0.0")




//    implementation("androidx.camera:camera-camera2:1.5.0-alpha02")
//    implementation("androidx.camera:camera-lifecycle:1.5.0-alpha02") // Lifecycle support for CameraX
//    implementation("androidx.camera:camera-view:1.5.0-alpha02") // Optional: for CameraView
//    implementation("androidx.camera:camera-extensions:1.5.0-alpha02")
//
//    // Optional: For image analysis, include the following
//    // implementation("androidx.camera:camera-mlkit:1.1.0") // For ML Kit integration if needed
}