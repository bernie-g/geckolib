import net.minecraftforge.jarjar.gradle.JarJar
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("geckolib-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.forge.jarjar)
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

jarJar.register {
    archiveClassifier = null
}

minecraft {
    useDefaultAccessTransformer()
    
    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("runs/${name}"))
            systemProperty("forge.logging.console.level", "debug")
        }
        
        register("client") {
            args("--username", "Dev")
        }
        
        register("client2") {
            args("--username", "Dev2")
        }
        
        register("server")
    }
}

repositories {
    @Suppress("DEPRECATION")
    minecraft.mavenizer(this@repositories)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    mavenCentral()
    
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
    implementation(minecraft.dependency(libs.forge))
    compileOnly(project(":common"))
    
    compileOnly(libs.mixinextras.common)
    testCompileOnly(libs.mixinextras.common)
    runtimeOnly(libs.mixinextras.forge)
    implementation(libs.jopt.simple)
    
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })
    annotationProcessor(libs.mixinextras.common)
    annotationProcessor(libs.forge.eventbusvalidator)
    
    "jarJar"(libs.mixinextras.forge)

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(libs.examplemod.forge.get())
}

//Make the result of the jarJar task the one with no classifier instead of no classifier and "all"
tasks.named<Jar>("jar").configure {
    archiveClassifier.set("slim")
}

tasks.withType<ProcessResources>() {
    exclude("**/accesstransformer-nf.cfg")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    
    uploadFile.set(tasks.named<JarJar>("jarJar"))
    projectId.set(properties["modrinthProjectId"] as String)
    versionName = "Forge $mcVersion"
    loaders.set(listOf("forge"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(mcVersion))
    
    if (rootProject.file("changelog.txt").exists())
        changelog.set(rootProject.file("changelog.txt").readText(Charsets.UTF_8))
    
    debugMode = true
    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(properties["curseforgeProjectId"], tasks.named<JarJar>("jarJar"))
    mainFile.displayName = "${properties["modDisplayName"]} Forge $mcVersion ${project.version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java ${libs.versions.java.get()}")
    mainFile.addEnvironment("Client", "Server")
    
    if (rootProject.file("changelog.txt").exists())
        mainFile.changelog = rootProject.file("changelog.txt").readText(Charsets.UTF_8)
    
    debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>(modId) {
                from(components["jarJar"])
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

