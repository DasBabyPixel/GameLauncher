import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.launch4j)
}

val lwjglVersion: String = libs.versions.lwjgl.get()
val lwjglNatives = Pair(System.getProperty("os.name")!!, System.getProperty("os.arch")!!).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else "natives-linux"

        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"

        arrayOf("Windows").any { name.startsWith(it) } ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"

        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

launch4j {
    jarFileCollection = tasks.named("shadowJar").map { it.outputs.files }
    outfile = "${project.name}.exe"
    headerType = "gui"
    mainClassName = "gamelauncher.lwjgl.Start"
    icon = "${projectDir}/src/main/resources/assets/gamelauncher/default_icon.ico"
    jreMinVersion = "17"
    jvmOptions = listOf("-Dgamelauncher.name=GameLauncher", "-Dgamelauncher.game_directory=application")
}

fun launch4jOutput(task: Launch4jLibraryTask): File {
    return task.outputDirectory.file(task.outfile.get()).get().asFile
}

tasks {
    register<Launch4jLibraryTask>("createDebugExe") {
        group = "launch4j"
        outfile = "${project.name}-debug.exe"
        headerType = "console"
        jvmOptions.add("-Dgamelauncher.debug=true")
    }
    register<Exec>("runWindowsDebug") {
        dependsOn(getByName("createDebugExe"))
        group = "run"
        workingDir = parent!!.file("run")
        commandLine(launch4jOutput(getByName<Launch4jLibraryTask>("createDebugExe")))
    }
    register<Exec>("runWindows") {
        dependsOn(getByName("createExe"))
        group = "run"
        workingDir = parent!!.file("run")
        commandLine(launch4jOutput(getByName<Launch4jLibraryTask>("createExe")))
    }
    shadowJar {
        archiveBaseName = "lwjgl-test"
        archiveClassifier = ""
        archiveVersion = ""
        manifest {
            attributes["Main-Class"] = "gamelauncher.lwjgl.Start"
        }
//        minimize {
//            exclude(dependency("org.bouncycastle:.*:.*"))
//        }
    }
    jar {
        manifest {
            attributes["Automatic-Module-Name"] = "gamelauncher.lwjgl"
        }
    }
    assemble {
        dependsOn("createExe", "createDebugExe")
    }
}

dependencies {
    api(parent!!.project("api"))
    api(parent!!.project("gles"))
    api(parent!!.project("netty"))
    api(libs.jansi)
    api(libs.commons.imaging)

    api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    api("org.lwjgl:lwjgl")
    api("org.lwjgl:lwjgl-glfw")
    api("org.lwjgl:lwjgl-openal")
    api("org.lwjgl:lwjgl-opengl")
    api("org.lwjgl:lwjgl-opengles")
    api("org.lwjgl:lwjgl-egl")
    api("org.lwjgl:lwjgl-stb")
    api("org.lwjgl:lwjgl::$lwjglNatives")
    api("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    api("org.lwjgl:lwjgl-openal::$lwjglNatives")
    api("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    api("org.lwjgl:lwjgl-opengles::$lwjglNatives")
    api("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}