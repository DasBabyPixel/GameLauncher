plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.launch4j) apply false
}

tasks.register("generateRunDirectory") {
    doLast {
        mkdir("run")
    }
}

allprojects {
    group = "de.dasbabypixel.gamelauncher"
    version = "0.0.2-SNAPSHOT"
    pluginManager.withPlugin("java") {
        tasks.withType<Javadoc>().configureEach {
            options.encoding = "UTF-8"
        }
        extensions.getByType<JavaPluginExtension>().apply {
            toolchain.languageVersion = JavaLanguageVersion.of(11)
            if (pluginManager.hasPlugin("maven-publish")) {
                withSourcesJar()
                withJavadocJar()
            }
        }
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.getByType<PublishingExtension>().apply {
            repositories {
                maven {
                    name = "DarkCube"
                    credentials(PasswordCredentials::class)
                    url = uri("https://nexus.darkcube.eu/repository/dasbabypixel/")
                }
            }
        }
    }

    pluginManager.withPlugin(rootProject.libs.plugins.shadow.get().pluginId) {
        components.forEach {
            if (it is AdhocComponentWithVariants) {
                it.withVariantsFromConfiguration(configurations.getByName("shadowRuntimeElements")) {
                    skip()
                }
            }
        }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
        }
        withType<JavaExec>().configureEach {
            notCompatibleWithConfigurationCache("Created by IntelliJ")
        }
    }
}
