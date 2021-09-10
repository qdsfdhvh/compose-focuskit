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
val isUploadBintray: Boolean by rootProject.extra

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
  api("androidx.compose.ui:ui:$composeVersion")
  api("androidx.compose.runtime:runtime:$composeVersion")
  api("androidx.compose.foundation:foundation:$composeVersion")
}

if (isUploadBintray) {
  apply(from = rootProject.file("gradle/bintray.gradle"))
}
