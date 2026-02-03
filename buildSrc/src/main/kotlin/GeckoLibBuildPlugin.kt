import gradle.kotlin.dsl.accessors._13c7bbbc23d2ab41b247cc58dd914463.versionCatalogs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.provideDelegate
import java.util.function.Supplier

abstract class GeckoLibBuildPlugin(val project: Project) {
    val libs = findVersionCatalogue("libs");

    val modId: String by project
    val modDisplayName: String by project
    val modAuthors: String by project
    val modContributors: String by project
    val modLicense: String by project
    val modDescription: String by project

    val modVersion = versionLookup("geckolib")
    val mcVersion = versionLookup("minecraft")
    val javaVersion = versionLookup("java")
    val neoformVersion = versionLookup("neoform")

    val forgeVersion = versionLookup("forge")
    val fmlVersion = versionLookup("forge.fml")

    val fabricVersion = versionLookup("fabric")
    val fabricVersionRange = versionLookup("fabric.range")
    val fabricApiVersion = versionLookup("fabric.api")
    val fabricApiVersionRange = versionLookup("fabric.api.range")

    val neoforgeVersion = versionLookup("neoforge")
    val neoforgeLoaderVersion = versionLookup("neoforge.loader")

    fun versionLookup(id: String): GeoVersionConstraint {
        return GeoVersionConstraint(project.providers.provider {
            libs.findVersion(id)
                .orElseThrow(Supplier {
                    NoSuchElementException("Version '${id.replace('.', '-')}' does not exist in version catalogue '${libs.name}'!")
                })
        })
    }

    fun findVersionCatalogue(name: String): VersionCatalog {
        return project.versionCatalogs.find(name).orElseThrow(Supplier {
            NoSuchElementException("No version catalogue '$name' exists!")
        })
    }

    class GeoVersionConstraint(private val constraint: Provider<VersionConstraint>) : Provider<VersionConstraint> by constraint {
        fun version(): String {
            val constraint = get()
            val version = constraint.preferredVersion

            if (version.isNullOrEmpty())
                return constraint.requiredVersion

            return version;
        }

        fun range(): String {
            val constraint = get()
            var range = constraint.requiredVersion

            if (range.isNullOrEmpty()) {
                range = constraint.strictVersion

                if (range.isNullOrEmpty())
                    throw IllegalArgumentException("Version constraint '${constraint.displayName}' does not have a required or strict version declared!")
            }

            return range
        }

        override fun toString(): String {
            return version()
        }
    }
}