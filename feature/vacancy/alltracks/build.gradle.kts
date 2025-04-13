plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.vacancy.alltracks"
}

dependencies {
    implementation(projects.data.track)
    implementation(projects.data.vacancy)
}