plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.vacancy.appliedcandidates"
}

dependencies {
    implementation(projects.data.candidate)
    implementation(projects.data.track)
    implementation(projects.data.vacancy)
    implementation(projects.feature.vacancy.common)
}