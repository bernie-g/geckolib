import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.moddevgradle)
}

val geckolib = extensions.getByType<GeckoLibBuildPlugin>()

neoForge {
    version = geckolib.neoforgeVersion.version()

    accessTransformers.files.setFrom(project(":common").file("src/main/resources/META-INF/accesstransformer.cfg"))
    validateAccessTransformers = true

    interfaceInjectionData {
        from(project(":common").file("src/main/resources/META-INF/interface_injections.json"))
        publish(project(":common").file("src/main/resources/META-INF/interface_injections.json"))
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.DEBUG
        }

        mods.create(geckolib.modId).sourceSet(project.sourceSets.getByName("main"))

        register("client") {
            client()
        }

        register("client2") {
            client()
            programArguments.addAll("--username", "Player")
        }

        register("server") {
            server()
            programArgument("--nogui")
        }
    }
}

dependencies {
    compileOnly(project(":common"))

    // Only enable for testing as needed
    // Disable before publishing
    //runtimeOnly(libs.examplemod.neoforge)
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named<Jar>("jar"))
    projectId = "8BmcQJ2H"
    versionName = "NeoForge ${geckolib.mcVersion}-$version"
    versionType = "release"
    loaders.set(listOf("neoforge"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(geckolib.mcVersion.version()))
    changelog.set(rootProject.file("changelog.md").readText(Charsets.UTF_8))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jar)
    mainFile.displayName = "NeoForge $version"
    mainFile.releaseType = "release"
    mainFile.addModLoader("NeoForge")
    mainFile.addGameVersion(geckolib.mcVersion)
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

