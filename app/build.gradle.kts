import ru.nativespeakers.convention.TjobBuildType

plugins {
    alias(libs.plugins.tjob.android.application)
    alias(libs.plugins.tjob.android.application.flavors)
    alias(libs.plugins.tjob.android.application.compose)
    alias(libs.plugins.tjob.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.nativespeakers.tjob"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.nativespeakers.tjob"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = TjobBuildType.DEBUG.applicationIdSuffix
        }
        release {
            applicationIdSuffix = TjobBuildType.RELEASE.applicationIdSuffix
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.feature.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}