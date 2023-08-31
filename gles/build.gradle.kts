plugins {
    `java-library`
    `maven-publish`
}

tasks {
    jar {
        manifest {
            attributes["Automatic-Module-Name"] = "gamelauncher.gles"
        }
    }
}

dependencies {
    api(rootProject.project("api"))
    api(project("gl"))
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}
