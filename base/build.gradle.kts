plugins {
    `java-library`
    `maven-publish`
}

tasks {
    jar {
        manifest {
            attributes["Automatic-Module-Name"] = "gamelauncher.base"
        }
    }
}

dependencies {
    api(libs.fastutil)
    api(libs.disruptor)
    api(libs.annotations)
    api(libs.streamsupport)
    api(libs.guava)
    api(libs.property)
    api(libs.joml)
    api(libs.gson)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}
