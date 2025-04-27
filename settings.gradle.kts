pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TJob"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:ui")
include(":core:designsystem")
include(":core:common")
include(":core:token")
include(":core:network")
include(":core:model")
include(":data:auth")
include(":feature:auth")
include(":feature:home")
include(":data:user")
include(":data:candidate")
include(":data:paging")
include(":data:interview")
include(":data:track")
include(":data:vacancy")
include(":feature:filters")
include(":data:tag")
include(":feature:profile")
include(":feature:competencies")
include(":feature:vacancy")
include(":feature:vacancy:create")
include(":feature:vacancy:details")
include(":feature:vacancy:common")
include(":feature:vacancy:edit")
include(":feature:vacancy:alltracks")
include(":feature:vacancy:appliedcandidates")
include(":feature:track")
include(":feature:track:details")
include(":feature:track:addinterview")
include(":feature:track:common")
include(":core:clipboard")
