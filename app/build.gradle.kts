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
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.auth)
    implementation(projects.feature.competencies)
    implementation(projects.feature.filters)
    implementation(projects.feature.home)
    implementation(projects.feature.interview)
    implementation(projects.feature.profile)
    implementation(projects.feature.track.addinterview)
    implementation(projects.feature.track.details)
    implementation(projects.feature.vacancy.alltracks)
    implementation(projects.feature.vacancy.appliedcandidates)
    implementation(projects.feature.vacancy.create)
    implementation(projects.feature.vacancy.details)
    implementation(projects.feature.vacancy.edit)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)
}