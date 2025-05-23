plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "ru.nativespeakers.core.network"
}

dependencies {
    implementation(projects.core.token)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    api(libs.bundles.ktor.client)
}