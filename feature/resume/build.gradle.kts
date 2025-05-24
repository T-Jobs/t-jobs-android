plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.resume"
}

dependencies {
    implementation(projects.data.candidate)

    implementation(projects.feature.vacancy.details)
}