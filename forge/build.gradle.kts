import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.minecraftforge.gradle.userdev.tasks.JarJar

plugins {
    id("java")
    id("eclipse") //both are required for the ideCopyResourcesTask
    id("idea")
    id("maven-publish")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.spongepowered.mixin") version "0.7.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

val minecraft_version: String by project
val mappings_mc_version: String by project
val parchment_version: String by project
val mod_id: String by project
val forge_version: String by project

base {
    archivesName = "geckolib-forge-${minecraft_version}"
}

jarJar.enable()

minecraft {
    mappings("parchment", "${mappings_mc_version}-${parchment_version}-${minecraft_version}")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    // This property allows configuring Gradle's ProcessResources task(s) to run on IDE output locations before launching the game.
    // It is REQUIRED to be set to true for this template to function.
    // See https://docs.gradle.org/current/dsl/org.gradle.language.jvm.tasks.ProcessResources.html
    copyIdeResources = true
    runs {
        sourceSets.forEach {
            val dir = layout.buildDirectory.dir("sourcesSets/$it.name")
            it.output.setResourcesDir(dir)
            it.java.destinationDirectory = dir
        }

        create("client") {
            workingDirectory(project.file("runs/" + name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("${mod_id}-forge4.0-Client")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")

            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${mod_id}.mixins.json")

            mods {
                create(mod_id) {
                    source(sourceSets.getByName("main"))
                    source(project(":common").sourceSets.getByName("main"))
                }
            }
        }

        create("clientAlt") {
            parent(minecraft.runs.named("client").get())
            workingDirectory(project.file("runs/"+ name))
            taskName("${mod_id}-forge4.0-Client-2")
            args("--username", "Alt")
        }

        create("server") {
            workingDirectory(project.file("runs/"+ name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("${mod_id}-forge4.0-Server")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${mod_id}.mixins.json")

            mods {
                create(mod_id) {
                    source(project(":common").sourceSets.main.get())
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    compileOnly(project(":common"))
    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")
    compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")
    implementation(jarJar("io.github.llamalad7:mixinextras-forge:0.3.5")) {
        jarJar.ranged(this, "[0.3.5,)") //TODO figure out jarJar again
    }
}

tasks.named<Jar>("jar").configure {
    archiveClassifier.set("slim")
}

tasks.named<JarJar>("jarJar").configure {
    archiveClassifier.set("")
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

mixin {
    add(sourceSets.getByName("main"), "${mod_id}.refmap.json")
    config("${mod_id}.mixins.json")
}

modrinth {
		token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
		projectId = "8BmcQJ2H"
		versionNumber.set(project.version.toString())
		versionName = "Forge ${minecraft_version}"
		uploadFile.set(tasks.named<JarJar>("jarJar"))
		changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))
		gameVersions.set(listOf(minecraft_version))
		loaders.set(listOf("Fabric"))
        debugMode = true
        //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jarJar)
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(minecraft_version)
    mainFile.addJavaVersion("Java 17")
    mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
    debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                jarJar.component(this)
                artifactId = base.archivesName.get()
            }
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

