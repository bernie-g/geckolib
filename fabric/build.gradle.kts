import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.loom)
}

val geckolib = extensions.getByType<GeckoLibBuildPlugin>()

dependencies {
    minecraft(libs.loom.minecraft)
    implementation(libs.fabric)
    implementation(libs.fabric.api)
    compileOnly(project(":common"))

    // ExampleMod
    //modLocalRuntime(libs.examplemod.fabric)
}

loom {
	accessWidenerPath = file("src/main/resources/geckolib.classtweaker")

    runs {
        configureEach {
            configName = "Fabric ${name.capitalized()}"
            runDir("runs/$name")
            ideConfigGenerated(true)
        }

        named("client") {
            client()
            programArg("--username=Dev")
        }

        named("server") {
            server()
        }
    }
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/accesstransformer.cfg")
    exclude("**/interface_injections.json")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.jar)
    projectId = "8BmcQJ2H"
    versionName = "Fabric ${geckolib.mcVersion}-$version"
    versionType = "release"
    loaders.set(listOf("fabric"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(geckolib.mcVersion.version()))
    changelog.set(rootProject.file("changelog.md").readText(Charsets.UTF_8))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jar)
    mainFile.displayName = "Fabric $version"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Fabric")
    mainFile.addGameVersion(geckolib.mcVersion)
    mainFile.addJavaVersion("Java ${geckolib.javaVersion}")
    mainFile.addEnvironment("Client", "Server")
    mainFile.changelog = rootProject.file("changelog.md").readText(Charsets.UTF_8)
    mainFile.changelogType = "markdown"

    //debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publications {
        create<MavenPublication>("geckolib") {
            from(components["java"])
            artifactId = base.archivesName.get()
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}