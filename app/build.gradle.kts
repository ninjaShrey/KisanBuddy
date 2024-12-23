plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Ensure the Google Services plugin is applied once
}

android {
    namespace = "com.example.kisan_buddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kisan_buddy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")  // Add Firebase Storage dependency
    implementation("com.google.firebase:firebase-appcheck:latest_version")

    implementation("com.google.android.gms:play-services-maps:19.0.0")
//    implementation("com.google.android.libraries.places:places:4.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
//    implementation("com.google.android.gms:play-services-places:18.1.0")

//    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")  // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")  // Gson Converter for Retrofit



}
