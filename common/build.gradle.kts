plugins {
    id("geckolib-convention")

    alias(libs.plugins.moddevgradle)
}

val geckolib = extensions.getByType<GeckoLibBuildPlugin>()

neoForge {
    neoFormVersion = geckolib.neoformVersion.version()
    accessTransformers.files.setFrom("src/main/resources/META-INF/accesstransformer.cfg")

    interfaceInjectionData {
        from("src/main/resources/META-INF/interface_injections.json")
        publish(file("src/main/resources/META-INF/interface_injections.json"))
    }
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