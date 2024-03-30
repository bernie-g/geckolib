import net.fabricmc.loom.task.RemapJarTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.loom)
}

version = libs.versions.geckolib.get()
val mcVersion = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()
val fabricVersion = libs.versions.fabric.asProvider().get()
val fapiVersion = libs.versions.fabric.api.get()

java {
    sourceCompatibility =  JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesName = "geckolib-fabric-${mcVersion}"
}

repositories {
    //mavenCentral {
    //    content {
    //        includeGroup("com.google.code.findbugs")
    //    }
    //}
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroupAndSubgroups("org.parchmentmc")
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
   // implementation("com.google.code.findbugs:jsr305:3.0.1") //Provides the Nullable annotations
}

loom {
	accessWidenerPath = project(":common").file("src/main/resources/geckolib.accesswidener")
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
    versionNumber.set("${version}")
    versionName = "Fabric ${mcVersion}"
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))
    gameVersions.set(listOf(mcVersion))
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
    mainFile.addGameVersion(mcVersion)
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