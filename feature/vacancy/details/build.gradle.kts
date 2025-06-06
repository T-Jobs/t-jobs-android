plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.vacancy.details"
}

dependencies {
    implementation(projects.data.candidate)
    implementation(projects.data.interview)
    implementation(projects.data.track)
    implementation(projects.data.user)
    implementation(projects.data.vacancy)

    implementation(projects.feature.vacancy.common)
    implementation(projects.feature.home)
}