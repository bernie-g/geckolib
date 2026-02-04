plugins {
    java
    `maven-publish`
    idea
    eclipse
}

val geckolib = extensions.create(
    "geckolib",
    GeckoLibBuildPlugin::class.java,
    project
)!!

project.version = geckolib.modVersion
project.base.archivesName = "geckolib-${project.name}-${geckolib.mcVersion}"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(geckolib.javaVersion.version()))

    withSourcesJar()
    withJavadocJar()
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

eclipse {
    classpath {
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
    flatDir {
        dir("libs")
    }
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename { "$it.txt" }
    }

    manifest {
        attributes(mapOf(
                "Specification-Title"     to geckolib.modDisplayName,
                "Specification-Vendor"    to geckolib.modAuthors,
                "Specification-Version"   to geckolib.modVersion,
                "Implementation-Title"    to geckolib.modDisplayName,
                "Implementation-Version"  to geckolib.modVersion,
                "Implementation-Vendor"   to geckolib.modAuthors,
                "Built-On-Minecraft"      to geckolib.mcVersion,
                "MixinConfigs"            to "${geckolib.modId}.mixins.json"
        ))
}
}

tasks.withType<ProcessResources>().configureEach {
    withCommonSource { from(it.resources) }

    val expandProps = mapOf(
            "version"                       to geckolib.modVersion,
            "group"                         to project.group,
            "minecraft_version"             to geckolib.mcVersion,
            "java_version"                  to geckolib.javaVersion,
            "forge_version"                 to geckolib.forgeVersion,
            "forge_loader_range"            to geckolib.fmlVersion,
            "forge_version_range"           to geckolib.forgeVersion.range(),
            "minecraft_version_range"       to geckolib.mcVersion.range(),
            "fabric_loader_version"         to geckolib.fabricVersion,
            "fabric_loader_version_range"   to geckolib.fabricVersionRange,
            "fabric_api_version"            to geckolib.fabricApiVersion,
            "fabric_api_version_range"      to geckolib.fabricApiVersionRange,
            "neoforge_version_range"        to geckolib.neoforgeVersion.range(),
            "neoforge_loader_range"         to geckolib.neoforgeLoaderVersion.range(),
            "mod_display_name"              to geckolib.modDisplayName,
            "mod_authors"                   to geckolib.modAuthors,
            "mod_contributors"              to geckolib.modContributors,
            "mod_id"                        to geckolib.modId,
            "mod_license"                   to geckolib.modLicense,
            "mod_description"               to geckolib.modDescription
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }

    inputs.properties(expandProps)
}

tasks.withType<Wrapper>().configureEach {
    // Set to ALL and refresh gradle TWICE if you want to receive the full gradle source set
    // Only really useful for working on the buildscripts, otherwise saves downloading the entire gradle cache
    distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"
    this.options.release.set(java.toolchain.languageVersion.get().asInt())

}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}

tasks.named<JavaCompile>("compileJava").configure {
    withCommonSource { source(it.allSource) }
}

tasks.named<Jar>("sourcesJar").configure {
    withCommonSource { from(it.allSource) }
}

tasks.withType<Javadoc>().configureEach {
    withCommonSource { source(it.allJava) }
}

publishing {
    repositories {
        val cloudsmithUsername = providers.environmentVariable("CLOUDSMITH_USERNAME");
        val cloudsmithPassword = providers.environmentVariable("CLOUDSMITH_PASSWORD");

        if (!cloudsmithUsername.isPresent || !cloudsmithPassword.isPresent) {
            mavenLocal()
        }
        else {
            maven {
                name = "Cloudsmith"
                url = uri("https://maven.cloudsmith.io/geckolib3/geckolib/")

                credentials {
                    username = cloudsmithUsername.get()
                    password = cloudsmithPassword.get()
                }
            }
        }
    }
}

fun withCommonSource(consumer: (SourceSet) -> Unit) {
    if (project.name != "common")
        project(":common").sourceSets.getByName("main")?.let { consumer(it) }
}