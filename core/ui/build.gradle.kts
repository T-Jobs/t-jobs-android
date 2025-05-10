plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.android.library.compose)
}

android {
    namespace = "ru.nativespeakers.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.clipboard)

    api(libs.androidx.paging.compose)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    api(libs.androidx.core.ktx)
    api(libs.coil.kt.compose)
    api(libs.kotlinx.datetime)
    api(libs.shimmer.compose)
    api(libs.androidx.compose.material.iconsExtended)
}