plugins {
    id("geckolib-convention")

    alias(libs.plugins.moddevgradle)
}

version = libs.versions.geckolib.get()

base {
    archivesName = "geckolib-common-${libs.versions.minecraft.asProvider().get()}"
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    accessTransformers.files.setFrom("src/main/resources/META-INF/accesstransformer.cfg")

    interfaceInjectionData {
        from("src/main/resources/META-INF/interface_injections.json")
        publish(file("src/main/resources/META-INF/interface_injections.json"))
    }

    parchment.minecraftVersion.set(libs.versions.parchment.minecraft.get())
    parchment.mappingsVersion.set(libs.versions.parchment.asProvider().get())
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
    //compileOnly(libs.iris)

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