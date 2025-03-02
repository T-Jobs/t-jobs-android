plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.core.ui"
}

dependencies {
    api(libs.androidx.core.ktx)
}