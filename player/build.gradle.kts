plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.parcelize")
}

val androidCompileSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra
val jvmTargetVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra
val activityComposeVersion: String by rootProject.extra

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion
    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
    }
    compileOptions {
        sourceCompatibility = androidSourceCompatibility
        targetCompatibility = androidTargetCompatibility
    }
    kotlinOptions {
        jvmTarget = jvmTargetVersion
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = false
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    api(project(":focuskit"))
    api("androidx.compose.material:material:$composeVersion")
    api("androidx.compose.material:material-icons-extended:$composeVersion")
    api("com.google.android.exoplayer:exoplayer:2.15.0")

    // compose preview
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
}

apply(from = rootProject.file("gradle/bintray.gradle"))
