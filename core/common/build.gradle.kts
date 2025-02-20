plugins {
    alias(libs.plugins.tjob.jvm.library)
    alias(libs.plugins.tjob.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}