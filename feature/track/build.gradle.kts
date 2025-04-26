plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.track"
}

dependencies {
    implementation(projects.data.candidate)
    implementation(projects.data.interview)
    implementation(projects.data.track)
    implementation(projects.data.user)
}