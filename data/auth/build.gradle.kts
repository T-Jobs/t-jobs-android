plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "ru.nativespeakers.data.auth"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.token)

    implementation(libs.kotlinx.serialization.json)
}