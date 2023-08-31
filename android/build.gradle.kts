plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "gamelauncher.android"
    compileSdk = 34

    defaultConfig {
        multiDexEnabled = true
        minSdk = 16
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources.excludes.add("META-INF/INDEX.LIST")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }
}

dependencies {
    api(rootProject.project("gles"))
    api(rootProject.project("api"))
    api(rootProject.project("netty"))
    api(libs.androidx.appcompat)
    coreLibraryDesugaring(libs.android.desugar)
}
