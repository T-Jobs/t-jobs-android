plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.interview"
}

dependencies {
    implementation(projects.data.interview)
    implementation(projects.data.user)
    implementation(projects.data.track)
    implementation(projects.data.candidate)
}