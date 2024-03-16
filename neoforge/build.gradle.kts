import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
    id("net.neoforged.gradle.userdev") version "7.+"
}

val minecraft_version: String by project
val mappings_mc_version: String by project
val parchment_version: String by project
val mod_id: String by project
val neoforge_version: String by project

base {
    archivesName = "geckolib-neoforge-${minecraft_version}"
}

subsystems {
    parchment {
        minecraftVersion = mappings_mc_version
        mappingsVersion = parchment_version
    }
}

minecraft {
    accessTransformers.file("src/main/resources/META-INF/accesstransformer.cfg")
}

runs {
    configureEach {
        systemProperty("forge.logging.console.level", "debug")
        systemProperty("forge.enabledGameTestNamespaces", mod_id)

        modSource(project.sourceSets.getByName("main"))
        modSource(project(":common").sourceSets.getByName("main"))
    }

    create("client") {
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
    }

    create("server") {
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        programArgument("--nogui")
    }
}

dependencies {
    implementation("net.neoforged:neoforge:${neoforge_version}")
    compileOnly(project(":common"))
}

tasks.withType<JavaCompile>().matching{!it.name.startsWith("neo")}.configureEach {
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
		versionName = "NeoForge ${minecraft_version}"
		uploadFile.set(tasks.named<Jar>("jar"))
		changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
		gameVersions.set(listOf(minecraft_version))
		loaders.set(listOf("neoforge"))
        //https://github.com/modrinth/minotaur#available-properties
        debugMode = true
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jar)
    mainFile.releaseType = "release"
    mainFile.addModLoader("NeoForge")
    mainFile.addGameVersion(minecraft_version)
    mainFile.addJavaVersion("Java 17")
    mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
    debugMode = true
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

