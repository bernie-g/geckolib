import gradle.kotlin.dsl.accessors._6278ecd4aeba9585571b9ac6e140379d.java
import gradle.kotlin.dsl.accessors._6278ecd4aeba9585571b9ac6e140379d.publishing
import gradle.kotlin.dsl.accessors._6278ecd4aeba9585571b9ac6e140379d.versionCatalogs

plugins {
    `java`
    `maven-publish`
    `idea`
    `eclipse`
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    withSourcesJar()
    withJavadocJar()
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    mavenLocal {
        name = "GeckoLib Examples MavenLocal"
        content {
            includeModuleByRegex("software.bernie.geckolib", "geckolib-examples.*?")
        }
    }
    maven {
        name = "GeckoLib Examples Cloudsmith"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeModuleByRegex("software.bernie.geckolib", "geckolib-examples.*?")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val libs = project.versionCatalogs.find("libs")

val modId: String by project
val modDisplayName: String by project
val modAuthors: String by project
val modLicense: String by project
val modDescription: String by project
val modVersion = libs.get().findVersion("geckolib").get()
val mcVersion = libs.get().findVersion("minecraft").get()
val forgeVersion = libs.get().findVersion("forge").get()
val forgeVersionRange = libs.get().findVersion("forge.range").get()
val fmlVersionRange = libs.get().findVersion("forge.fml.range").get()
val mcVersionRange = libs.get().findVersion("minecraft.range").get()
val fapiVersion = libs.get().findVersion("fabric.api").get()
val fapiVersionRange = libs.get().findVersion("fabric.api.range").get()
val fabricVersion = libs.get().findVersion("fabric").get()
val fabricVersionRange = libs.get().findVersion("fabric.range").get()
val neoforgeVersionRange = libs.get().findVersion("neoforge.range").get()
val neoforgeLoaderVersionRange = libs.get().findVersion("neoforge.loader.range").get()

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${modDisplayName}" }
    }

    manifest {
        attributes(mapOf(
                "Specification-Title"     to modDisplayName,
                "Specification-Vendor"    to modAuthors,
                "Specification-Version"   to modVersion,
                "Implementation-Title"    to modDisplayName,
                "Implementation-Version"  to modVersion,
                "Implementation-Vendor"   to modAuthors,
                "Built-On-Minecraft"      to mcVersion,
                "MixinConfigs"            to "$modId.mixins.json"
        ))
    }
}

tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"
    this.options.getRelease().set(21)
}

tasks.withType<ProcessResources>().configureEach {
    val expandProps = mapOf(
            "version" to modVersion,
            "group" to project.group,
            "minecraft_version" to mcVersion,
            "forge_version" to forgeVersion,
            "forge_loader_range" to fmlVersionRange,
            "forge_version_range" to forgeVersionRange,
            "minecraft_version_range" to mcVersionRange,
            "fabric_api_version" to fapiVersion,
            "fabric_api_version_range" to fapiVersionRange,
            "fabric_loader_version" to fabricVersion,
            "fabric_loader_version_range" to fabricVersionRange,
            "mod_display_name" to modDisplayName,
            "mod_authors" to modAuthors,
            "mod_id" to modId,
            "mod_license" to modLicense,
            "mod_description" to modDescription,
            "neoforge_version_range" to neoforgeVersionRange,
            "neoforge_loader_range" to neoforgeLoaderVersionRange
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }

    inputs.properties(expandProps)
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