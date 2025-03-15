plugins {
    alias(libs.plugins.tjob.android.library)
}

android {
    namespace = "ru.nativespeakers.data.paging"
}

dependencies {
    api(libs.androidx.paging.compose)
}