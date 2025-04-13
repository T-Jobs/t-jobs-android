plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.vacancy.edit"
}

dependencies {
    implementation(projects.data.tag)
    implementation(projects.data.vacancy)
    implementation(projects.data.interview)
    implementation(projects.feature.vacancy.common)
}