import net.darkhax.curseforgegradle.Constants
import net.fabricmc.loom.task.RemapJarTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
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
	accessWidenerPath = file("src/main/resources/geckolib.accesswidener")

    runs {
        named("client") {
            displayName = "Fabric Client"

            client()
            generateRunConfig = true
            runDirectory = project.file("runs/$name")
            programArguments.add("--username=Dev")
        }

        named("server") {
            displayName = "Fabric Server"

            server()
            generateRunConfig = true
            runDirectory = project.file("runs/$name")
        }
    }
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/accesstransformer.cfg")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    projectId.set(properties["modrinthProjectId"] as String)
    versionName = "Fabric $mcVersion"
    versionType = "release"
    loaders.set(listOf("fabric"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(mcVersion))
    
    dependencies {
        required.project("fabric-api")
    }
    
    if (rootProject.file("changelog.txt").exists())
        changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))

    //debugMode = true
    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"
    
    val mainFile = upload(properties["curseforgeProjectId"], tasks.remapJar)
    mainFile.displayName = "Fabric $mcVersion ${project.version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Fabric")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java ${libs.versions.java.get()}")
    mainFile.addRelation("fabric-api", Constants.RELATION_REQUIRED)
    mainFile.addEnvironment("Client", "Server")
    
    if (rootProject.file("changelog.txt").exists())
        mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)

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