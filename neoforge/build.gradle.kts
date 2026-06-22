import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.moddevgradle)
}

val modId: String by project
val mcVersion = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()
val neoforgeVersion = libs.versions.neoforge.asProvider().get()

version = libs.versions.geckolib.get()

base {
    archivesName = "geckolib-neoforge-${mcVersion}"
}

neoForge {
    version = neoforgeVersion

    accessTransformers.files.setFrom(project(":common").file("src/main/resources/META-INF/accesstransformer-nf.cfg"))
    parchment.minecraftVersion.set(parchmentMcVersion)
    parchment.mappingsVersion.set(parchmentVersion)

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.DEBUG
        }

        mods.create(modId).sourceSet(project.sourceSets.getByName("main"))

        create("client") {
            client()
        }

        create("server") {
            server()
            programArgument("--nogui")
        }
    }
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Geckolib Examples"
                url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
            }
        }
        filter {
            includeGroup("software.bernie.geckolib")
        }
    }
}

dependencies {
    compileOnly(project(":common"))

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(libs.examplemod.neoforge)
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    
    uploadFile.set(tasks.named<Jar>("jar"))
    projectId.set(properties["modrinthProjectId"] as String)
    versionName = "NeoForge $mcVersion"
    versionType = "release"
    loaders.set(listOf("neoforge"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(mcVersion))
    
    if (rootProject.file("changelog.txt").exists())
        changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))

    //debugMode = true
    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(properties["curseforgeProjectId"], tasks.jar)
    mainFile.displayName = "NeoForge $mcVersion ${project.version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("NeoForge")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java ${libs.versions.java.get()}")
    mainFile.addEnvironment("Client", "Server")
    
    if (rootProject.file("changelog.txt").exists())
        mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)

    //debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>(modId) {
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

