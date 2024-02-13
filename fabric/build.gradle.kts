import net.fabricmc.loom.task.RemapJarTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
    id("fabric-loom") version "1.5-SNAPSHOT"
}

val minecraft_version: String by project
val mappings_mc_version: String by project
val parchment_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project

java {
    sourceCompatibility =  JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesName = "geckolib-fabric-${minecraft_version}"
}

repositories {
    mavenCentral {
        content {
            includeGroup("com.google.code.findbugs")
        }
    }
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${mappings_mc_version}:${parchment_version}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    compileOnly(project(":common"))
    implementation("com.google.code.findbugs:jsr305:3.0.1") //Provides the Nullable annotations
}

loom {
	accessWidenerPath = project(":common").file("src/main/resources/geckolib.accesswidener")
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/"+name)
            programArg("--username=Dev")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/"+name)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    source(project(":common").sourceSets.getByName("main").allSource)
}

tasks.named<Jar>("sourcesJar").configure {
    from(project(":common").sourceSets.getByName("main").allSource)
}

tasks.withType<Javadoc>().configureEach {
    source(project(":common").sourceSets.getByName("main").allJava)
}

tasks.withType<ProcessResources>().configureEach {
   from(project(":common").sourceSets.getByName("main").resources)
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    projectId = "8BmcQJ2H"
    versionNumber.set(project.version.toString())
    versionName = "Fabric ${minecraft_version}"
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))
    gameVersions.set(listOf(minecraft_version))
    versionType = "release"
    loaders.set(listOf("fabric"))
    debugMode = true
    dependencies {
        required.project("fabric-api")
    }

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.remapJar)
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(minecraft_version)
    mainFile.addJavaVersion("Java 17")
    mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
    debugMode = true
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