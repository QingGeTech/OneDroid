plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinKapt)
    idea
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

android {
    namespace = "tech.qingge.onedroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.qingge.onedroid"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.fragmentKtx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.hilt)
    implementation(libs.xxPermission)
    implementation(libs.glide)
    implementation(libs.androidx.activity)
    implementation(libs.mlkitTextRecognitionChinese)
    implementation(libs.photoView)
    implementation(libs.opencv)
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)
    implementation(project(":TextEditor"))
    implementation(project(":axml"))
    kapt(libs.hiltCompiler)

    implementation(libs.baksmali)
    implementation(libs.dexlib2)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

}