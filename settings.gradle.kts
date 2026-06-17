pluginManagement {
    repositories {
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net/")
            content {
                includeGroupAndSubgroups("net.minecraftforge")
            }
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
            content {
                includeGroupAndSubgroups("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/")
            content {
                includeGroupAndSubgroups("net.neoforged")
                includeGroup("codechicken")
                includeGroup("net.covers1624")
            }
        }
        maven {
            name = "Sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
            content {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "geckolib"

include("common")
include("fabric")
//include("forge")
include("neoforge")
