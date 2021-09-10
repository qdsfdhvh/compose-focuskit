tasks.register("Delete", Delete::class) {
    delete(rootProject.buildDir)
}

val androidCompileSdkVersion by extra(31)
val androidBuildToolsVersion by extra("31.0.0")
val androidTargetSdkVersion by extra(31)
val androidMinSdkVersion by extra(21)
val androidSourceCompatibility by extra(JavaVersion.VERSION_11)
val androidTargetCompatibility by extra(JavaVersion.VERSION_11)
val jvmTargetVersion by extra("1.8")
val composeVersion by extra("1.0.1")
val activityComposeVersion by extra("1.3.1")
val isUploadBintray by extra(true)

plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("android") apply false
    kotlin("plugin.parcelize") apply false
    id("com.diffplug.spotless").version("5.12.5")
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude(
                "$buildDir/**/*.kt",
                "bin/**/*.kt",
                "buildSrc/**/*.kt"
            )
            ktlint("0.41.0").userData(
                mapOf(
                    "indent_size" to "2",
                    "continuation_indent_size" to "2"
                )
            )
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint("0.41.0").userData(
                mapOf(
                    "indent_size" to "2",
                    "continuation_indent_size" to "2"
                )
            )
        }
    }
}