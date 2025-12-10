import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.forge.at)
}

val modId: String by project
val mcVersion = libs.versions.minecraft.asProvider().get()
val mappingsMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion = libs.versions.parchment.asProvider().get()
val forgeVersion = libs.versions.forge.asProvider().get()

version = libs.versions.geckolib.get()

base {
    archivesName = "geckolib-forge-${mcVersion}"
}

minecraft {
    mappings("official", mappingsMcVersion)
    //mappings("parchment", "${mappingsMcVersion}-${parchmentVersion}")

    accessTransformers {
        project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    }

    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("runs/${name}"))
            systemProperty("forge.logging.console.level", "debug")
        }

        register("client") {
            args("--username", "Dev")
            args("-mixin.config=${modId}.mixins.json")
        }

        register("client2") {
            args("--username", "Dev2")
            args("-mixin.config=${modId}.mixins.json")
        }

        register("server") {
            args("-mixin.config=${modId}.mixins.json")
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
    implementation(minecraft.dependency(libs.forge))
    annotationProcessor(libs.forge.eventbusvalidator)

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(fg.deobf(libs.examplemod.forge.get()))
}

//Make the result of the jarJar task the one with no classifier instead of no classifier and "all"
tasks.named<Jar>("jar").configure {
    archiveClassifier.set("slim")
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
		versionName = "Forge ${mcVersion}"
        uploadFile.set(tasks.named<Jar>("jar"))
		changelog.set(rootProject.file("changelog.md").readText(Charsets.UTF_8))
		gameVersions.set(listOf(mcVersion))
		loaders.set(listOf("forge"))

        //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(388172, tasks.jar)
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java 17")
    mainFile.changelog = rootProject.file("changelog.md").readText(Charsets.UTF_8)

    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("geckolib") {
                from(components["java"])
                //jarJar.component(this)
                artifactId = base.archivesName.get()
            }
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}

