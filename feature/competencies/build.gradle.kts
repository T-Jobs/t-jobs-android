plugins {

    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.competencies"
}

dependencies {
    implementation(projects.feature.profile)
    implementation(projects.data.interview)
}