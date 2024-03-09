/*
 * Copyright (C) 2024 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

gradle.beforeProject {
    repositories {
        maven {
            name = "DarkCube"
            url = uri("https://nexus.darkcube.eu/repository/dasbabypixel/")
        }
        mavenCentral {
            content {
                excludeGroup("net.sourceforge.streamsupport")
                excludeGroup("com.lmax")
            }
        }
        google()
    }
}

rootProject.name = "gamelauncher"
include("api")
project(":api").projectDir = file("base")
include("lwjgl")
include("example")
include("android")
include("gles")
include("gles:gl")
include("netty")
include("netty:test")
include("android:app")
