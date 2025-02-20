plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.hilt)
}

android {
    namespace = "ru.nativespeakers.data.auth"
}

dependencies {
    implementation(projects.core.network)
}