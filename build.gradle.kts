plugins {
    id("fabric-loom") version "0.7-SNAPSHOT"
    id("maven-publish")
}

group = "software.bernie.geckolib"

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "Shedaniel's Maven"
        url = uri("https://maven.shedaniel.me/")
    }

    maven {
        name = "Curseforge Maven"
        url = uri("https://www.cursemaven.com")
    }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }

    maven {
        url = uri("gcs://devan-maven")
    }

    maven {
        url = uri("https://repo.repsy.io/mvn/fadookie/particleman")
    }

    maven {
        url = uri("https://maven.blamejared.com")
    }
}

dependencies {
    minecraft("net.minecraft", "minecraft", "21w16a")
    mappings("net.fabricmc", "yarn", "21w16a+build.12", classifier = "v2")

    modImplementation("net.fabricmc", "fabric-loader", "0.11.3")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.33.1+1.17")

    // Actual dependencies
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.9.0")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.0")
    implementation("com.eliotlash.molang:molang:SNAPSHOT.12")
    implementation("com.eliotlash.mclib:mclib:SNAPSHOT.12")

    implementation(project(":geckolib-core"))
}

loom {
    accessWidener("src/main/resources/geckolib.aw")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(8)
    } else {
        sourceCompatibility = "8"
        targetCompatibility = "8"
    }
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.remapJar {
    doLast {
        input.get().asFile.delete()
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar) {
                classifier = null
            }

            artifact(tasks.remapSourcesJar) {
                classifier = "sources"
            }
        }
    }

    repositories {
        // Add repositories to publish to here.
        if (project.hasProperty("maven_url")) {
            maven {
                val mavenUrl = project.property("maven_url") as String
                url = uri(mavenUrl)
                if (mavenUrl.startsWith("http") && project.hasProperty("maven_username") && project.hasProperty("maven_password")) {
                    credentials {
                        username = project.property("maven_username") as String
                        password = project.property("maven_password") as String
                    }
                }
            }
        }
    }
}
