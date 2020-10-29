pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "kroto"

include(
    "kroto-http",
    "kroto-fuel",
    "kroto-ktor-server"
)