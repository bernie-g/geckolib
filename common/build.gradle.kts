plugins {
    id("geckolib-convention")

    alias(libs.plugins.vanillagradle)
}

val modId: String by project
version = libs.versions.geckolib.get()
val mcVersion = libs.versions.minecraft.asProvider().get()

base {
    archivesName = "geckolib-common-${mcVersion}"
}

minecraft {
    version(mcVersion)
    accessWideners(file("src/main/resources/${modId}.accesswidener"))
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)

    // Only enable for testing as needed
    // Disable before publishing
    //implementation(libs.examplemod.common)
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