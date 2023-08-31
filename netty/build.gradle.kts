plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveBaseName = "standalone-server"
        archiveClassifier = ""
        minimize {
            exclude(dependency("org.bouncycastle:.*:.*"))
        }
    }
    jar {
        manifest {
            attributes["Automatic-Module-Name"] = "gamelauncher.netty"
            attributes["Main-Class"] = "gamelauncher.netty.standalone.StandaloneServer"
        }
    }
}

dependencies {
    api(rootProject.project("api"))
    api(libs.bundles.bouncycastle)
    api(libs.bundles.netty)
    implementation(libs.jansi)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}
