plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.myapplication_virtualfitting"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication_virtualfitting"
        minSdk = 33
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions{
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
    exclude(group = "com.android.support", module = "support-v4")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.google.android.material:material:1.11.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val roomVersion = "2.6.1"

    // [로컬 DB] Room 라이브러리
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")

    // 🌟 [통신] 서버(FastAPI) & 기상청 API 통신용 Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0") // 통신 에러 확인용 로깅

    // 🌟 [위치] 기상청 API 연동을 위한 위치 서비스
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 🌟 [이미지] 서버 이미지 로딩용 Glide (4.16.0 최신 버전으로 통일)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}