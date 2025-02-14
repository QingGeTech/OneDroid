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
    namespace = "tech.qingge.androiddevtoolbox"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.qingge.androiddevtoolbox"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
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
//    implementation(libs.kodeEditor)
//    implementation(libs.kodeHighlighter)
//    implementation(libs.kodeHighlighterCore)
//    coreLibraryDesugaring(libs.desugar.jdk.libs)
//    implementation(libs.dbinspector)
    implementation(project(":TextEditor"))
    kapt(libs.hiltCompiler)

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)
}