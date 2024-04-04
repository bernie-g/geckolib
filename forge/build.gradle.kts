import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.minecraftforge.gradle.userdev.tasks.JarJar

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.parchmentforgegradle)
}

val modId: String by project
version = libs.versions.geckolib.get()
val mcVersion = libs.versions.minecraft.asProvider().get()
val mappingsMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()
val forgeVersion = libs.versions.forge.asProvider().get()

base {
    archivesName = "geckolib-forge-${mcVersion}"
}

jarJar.enable()

minecraft {
    mappings("parchment", "${mappingsMcVersion}-${parchmentVersion}-${mcVersion}")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    copyIdeResources = true

    runs {
        sourceSets.forEach {
            val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

            it.output.setResourcesDir(dir)
            it.java.destinationDirectory = dir
        }

        create("client") {
            workingDirectory(project.file("runs/" + name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("geckolibClient")
            args("--username", "Dev")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")

            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${modId}.mixins.json")

            mods {
                create(modId) {
                    source(sourceSets.getByName("main"))
                    source(project(":common").sourceSets.getByName("main"))
                }
            }
        }

        create("client2") {
            parent(minecraft.runs.named("client").get())
            workingDirectory(project.file("runs/"+ name))
            taskName("geckolibClient2")
            args("--username", "Dev2")
            args("-mixin.config=${modId}.mixins.json")
        }

        create("server") {
            workingDirectory(project.file("runs/"+ name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("GeckoLib-Server")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${modId}.mixins.json")

            mods {
                create(modId) {
                    source(project(":common").sourceSets.main.get())
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.forge)
    compileOnly(project(":common"))

    if (System.getProperty("idea.sync.active") != "true")
        annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    annotationProcessor(libs.mixinextras.common)
    runtimeOnly(libs.mixinextras.common)
    runtimeOnly(libs.mixinextras.forge)
    jarJar(libs.mixinextras.forge) {
        jarJar.ranged(this, libs.versions.mixinextras.range.get())
    }
}

//Make the result of the jarJar task the one with no classifier instead of no classifier and "all"
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
    add(sourceSets.getByName("main"), "${modId}.refmap.json")
    config("${modId}.mixins.json")
}

modrinth {
		token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
		projectId = "8BmcQJ2H"
		versionNumber.set(project.version.toString())
		versionName = "Forge ${mcVersion}"
		uploadFile.set(tasks.jarJar)
		changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))
		gameVersions.set(listOf(mcVersion))
		loaders.set(listOf("forge"))
        debugMode = true
        //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jarJar)
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(mcVersion)
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

