import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.loom)
}

val modId: String by project
val mcVersion = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()

version = libs.versions.geckolib.get()

base {
    archivesName = "geckolib-fabric-${mcVersion}"
}

repositories {
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
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
    minecraft(libs.minecraft)
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${parchmentMcVersion}:${parchmentVersion}@zip")
    })
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)
    compileOnly(project(":common"))

    // ExampleMod
    //modLocalRuntime(libs.examplemod.fabric)
}

loom {
	accessWidenerPath = file("src/main/resources/geckolib.classtweaker")

    //mixin.defaultRefmapName.set("${modId}.refmap.json")

    runs {
        named("client") {
            configName = "Fabric Client"

            client()
            ideConfigGenerated(true)
            runDir("runs/" + name)
            programArg("--username=Dev")
        }

        named("server") {
            configName = "Fabric Server"

            server()
            ideConfigGenerated(true)
            runDir("runs/" + name)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    source(project(":common").sourceSets.getByName("main").allSource)
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}

tasks.named<Jar>("sourcesJar").configure {
    from(project(":common").sourceSets.getByName("main").allSource)
}

tasks.withType<Javadoc>().configureEach {
    source(project(":common").sourceSets.getByName("main").allJava)
}

tasks.withType<ProcessResources>().configureEach {
   from(project(":common").sourceSets.getByName("main").resources)
    exclude("**/accesstransformer.cfg")
    exclude("**/interface_injections.json")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    projectId = "8BmcQJ2H"
    versionName = "Fabric $mcVersion-$version"
    versionType = "release"
    loaders.set(listOf("fabric"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(mcVersion))
    changelog.set(rootProject.file("changelog.md").readText(Charsets.UTF_8))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.remapJar)
    mainFile.displayName = "Fabric $version"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Fabric")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java 21")
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