plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.hilt)
}

android {
    namespace = "ru.nativespeakers.core.token"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.androidx.dataStore.preferences)
}