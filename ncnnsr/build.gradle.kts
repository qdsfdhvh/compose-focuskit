plugins {
  id("com.android.library")
  kotlin("android")
}

val androidCompileSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra
val jvmTargetVersion: String by rootProject.extra

android {
  compileSdk = androidCompileSdkVersion
  buildToolsVersion = androidBuildToolsVersion
  defaultConfig {
    minSdk = androidMinSdkVersion
    targetSdk = androidTargetSdkVersion
    consumerProguardFiles("consumer-rules.pro")
    externalNativeBuild {
      cmake {
        cppFlags("")
      }
    }
  }
  externalNativeBuild {
    cmake {
      path("src/main/cpp/CMakeLists.txt")
      version = "3.18.1"
    }
  }
  compileOptions {
    sourceCompatibility = androidSourceCompatibility
    targetCompatibility = androidTargetCompatibility
  }
  kotlinOptions {
    jvmTarget = jvmTargetVersion
  }
}

dependencies {

}