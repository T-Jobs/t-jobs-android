plugins {
    alias(libs.plugins.tjob.android.feature)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.feature.track.addinterview"
}

dependencies {
    implementation(projects.core.clipboard)

    implementation(projects.data.user)
    implementation(projects.data.interview)

    implementation(projects.feature.track.common)
    implementation(projects.feature.track.details)
}