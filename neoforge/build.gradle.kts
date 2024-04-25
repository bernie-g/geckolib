import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.neogradle)
}

val modId: String by project
version = libs.versions.geckolib.get()
val mcVersion = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()
val neoforgeVersion = libs.versions.neoforge.asProvider().get()

base {
    archivesName = "geckolib-neoforge-${mcVersion}"
}

subsystems {
    parchment {
        minecraftVersion = parchmentMcVersion
        mappingsVersion = parchmentVersion
    }
}

minecraft {
    accessTransformers.file("src/main/resources/META-INF/accesstransformer.cfg")
}

runs {
    configureEach {
        systemProperty("forge.logging.console.level", "debug")
        systemProperty("forge.enabledGameTestNamespaces", modId)

        modSource(project.sourceSets.getByName("main"))
        modSource(project(":common").sourceSets.getByName("main"))
    }

    create("client") {
        systemProperty("neoforge.enabledGameTestNamespaces", modId)
    }

    create("server") {
        systemProperty("neoforge.enabledGameTestNamespaces", modId)
        programArgument("--nogui")
    }
}

dependencies {
    implementation(libs.neoforge)
    compileOnly(project(":common"))

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(libs.examplemod.neoforge)
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
		versionName = "NeoForge ${mcVersion}"
		uploadFile.set(tasks.named<Jar>("jar"))
		changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
		gameVersions.set(listOf(mcVersion))
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
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java 21")
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

