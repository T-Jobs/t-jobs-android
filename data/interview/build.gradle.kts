plugins {
    alias(libs.plugins.tjob.android.library)
    alias(libs.plugins.tjob.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "ru.nativespeakers.data.interview"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}