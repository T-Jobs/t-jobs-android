package ru.nativespeakers.convention

enum class TjobBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}