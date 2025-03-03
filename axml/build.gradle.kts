plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "mt.modder.hub.axml"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

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
}

dependencies {

    implementation(fileTree("libs"))
    implementation(libs.guava)
}