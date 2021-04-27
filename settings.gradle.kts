pluginManagement {
    repositories {
        jcenter()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "geckolib"

include(":geckolib-core")
project(":geckolib-core").projectDir = File("./core")
