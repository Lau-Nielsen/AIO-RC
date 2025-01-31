version = "0.0.1"

project.extra["PluginName"] = "Glory Recharger"
project.extra["PluginDescription"] = "Glory Recharger"

dependencies {
    compileOnly(project(":commons"))
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
                )
            )
        }
    }

    val copyJavaClasses by creating(Copy::class) {
        dependsOn(":commons:compileJava")
        from("${project(":commons").buildDir}/classes/java/main")
        into("$buildDir/classes/java/main")
        include("**/*.class")
    }

    named("compileJava") {
        dependsOn(copyJavaClasses)
    }
}
