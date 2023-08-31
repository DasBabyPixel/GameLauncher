plugins {
    `java-library`
//  `maven-publish`
}

tasks {
    jar {
        manifest {
            attributes["Automatic-Module-Name"] = "gamelauncher.gles.gl"
        }
    }
}

dependencies {
    api(rootProject.project("api"))
}

version = "1.0"
