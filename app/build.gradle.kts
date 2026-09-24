plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services") // Google Services plugin
    id("kotlin-parcelize")               // Kotlin Parcelize plugin
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.agroeasy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.agroeasy"
        minSdk = 24
        targetSdk = 34
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BOM for consistent versions across Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    // Firebase libraries
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")

    // Google services and location libraries
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    // ML Kit for image labeling
    implementation("com.google.mlkit:image-labeling:17.0.6")

    // Lottie animations
    implementation("com.airbnb.android:lottie:6.1.0")

    // CircleImageView for profile pictures
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Retrofit and Gson for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // YouTube player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")

    // Picasso (alternative to Glide)
    implementation("com.squareup.picasso:picasso:2.71828")

    // AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // CameraX libraries for camera functionality
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Apply the Google Services plugin at the bottom
apply(plugin = "com.google.gms.google-services")
