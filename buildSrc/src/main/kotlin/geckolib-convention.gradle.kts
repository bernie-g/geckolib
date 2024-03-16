import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    `java`
    `maven-publish`
    `idea`
    `eclipse`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
    withJavadocJar()
}

val mod_display_name: String by project
val mod_authors: String by project
val minecraft_version: String by project
val forge_version_range: String by project
val forge_loader_version_range: String by project
val minecraft_version_range: String by project
val fabric_api_version: String by project
val fabric_loader_version: String by project
val mod_id: String by project
val mod_license: String by project
val mod_description: String by project
val neoforge_version: String by project
val neoforge_loader_range: String by project


tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${mod_display_name}" }
    }
    manifest {
        attributes(mapOf(
                "Specification-Title"     to mod_display_name,
                "Specification-Vendor"    to mod_authors,
                "Specification-Version"   to archiveVersion,
                "Implementation-Title"    to mod_display_name,
                "Implementation-Version"  to archiveVersion,
                "Implementation-Vendor"   to mod_authors,
                "Built-On-Minecraft"      to minecraft_version
        ))
    }
}

tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"
    this.options.getRelease().set(17)
}

tasks.withType<ProcessResources>().configureEach {
    val expandProps = mapOf(
            "version" to version,
            "group" to project.group, //Else we target the task's group.
            "minecraft_version" to minecraft_version,
            "forge_version" to forge_version_range,
            "forge_loader_range" to forge_loader_version_range,
            "forge_version_range" to forge_version_range,
            "minecraft_version_range" to minecraft_version_range,
            "fabric_api_version" to fabric_api_version,
            "fabric_loader_version" to fabric_loader_version,
            "mod_display_name" to mod_display_name,
            "mod_authors" to mod_authors,
            "mod_id" to mod_id,
            "mod_license" to mod_license,
            "mod_description" to mod_description,
            "neoforge_version_range" to neoforge_version,
            "neoforge_loader_range" to neoforge_loader_range
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)

    doLast {
        val jsonMinifyStart = System.currentTimeMillis()
        var jsonMinified = 0
        var jsonBytesSaved = 0
        fileTree(outputs.files.asPath) {
            include("**/*.json")
            forEach {
                jsonMinified++
                val oldLength = it.length()
                it.writeText(JsonOutput.toJson(JsonSlurper().parse(it)), Charsets.UTF_8)
                jsonBytesSaved += (oldLength - it.length()).toInt()
                //println(it.name)
            }
        }
        println("Minified " + jsonMinified + " json files. Saved " + jsonBytesSaved + " bytes. Took " + (System.currentTimeMillis() - jsonMinifyStart) + "ms.")
    }
}

// Disables Gradle's custom module metadata from being published to maven. The
// metadata includes mapped dependencies which are not reasonably consumable by
// other mod developers.
tasks.withType<GenerateModuleMetadata>().configureEach {

    enabled = false
}

publishing {
    repositories {
        if (System.getenv("cloudUsername") == null && System.getenv("cloudPassword") == null) {
            mavenLocal()
        }
        else maven {
            name = "cloudsmith"
            url = uri("https://maven.cloudsmith.io/geckolib3/geckolib/")
            credentials {
                username = System.getenv("cloudUsername")
                password = System.getenv("cloudPassword")
            }
        }
    }
}