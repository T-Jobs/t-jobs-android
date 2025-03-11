plugins {
    alias(libs.plugins.tjob.jvm.library)
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}