import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.forge.at)
}

val geckolib = extensions.getByType<GeckoLibBuildPlugin>()

minecraft {
    mappings("parchment", "${parchmentMcVersion}-${parchmentVersion}")

    accessTransformer.setFrom(project(":common").file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("runs/${name}"))
            systemProperty("forge.logging.console.level", "debug")

            args("--mixin.config=${geckolib.modId}.mixins.json")
        }

        register("client") {
            args("--username", "Dev")
        }

        register("client2") {
            args("--username", "Dev2")
        }

        register("server") {}
    }
}

repositories {
    maven(minecraft.mavenizer)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = uri("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    implementation(minecraft.dependency(libs.forge))

    annotationProcessor(libs.forge.eventbusvalidator)

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(libs.examplemod.forge.get())
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/interface_injections.json")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named<Jar>("jar"))
    projectId = "8BmcQJ2H"
    versionName = "Forge ${geckolib.mcVersion}-${geckolib.modVersion}"
    versionType = "release"
    loaders.set(listOf("forge"))
    versionNumber.set(geckolib.modVersion.version())
    gameVersions.set(listOf(geckolib.mcVersion.version()))
    changelog.set(rootProject.file("changelog.md").readText(Charsets.UTF_8))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jar)
    mainFile.displayName = "Forge ${geckolib.modVersion}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(geckolib.mcVersion.version())
    mainFile.addJavaVersion("Java ${geckolib.javaVersion}")
    mainFile.addEnvironment("Client", "Server")
    mainFile.changelog = rootProject.file("changelog.md").readText(Charsets.UTF_8)
    mainFile.changelogType = "markdown"

    //debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("geckolib") {
                from(components["java"])
                artifactId = base.archivesName.get()
            }
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}

