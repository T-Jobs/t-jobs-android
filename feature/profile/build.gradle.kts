plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.profile"
}

dependencies {
    implementation(projects.data.user)
    implementation(projects.data.auth)
}