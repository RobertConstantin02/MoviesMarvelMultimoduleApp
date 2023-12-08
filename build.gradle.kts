//repositories: where our dependencies are fetch from
//dependencies: android gradle version and kotlin version
buildscript {
    val kotlinVersion by extra("1.8.22")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.hilt.gradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }

}

plugins {
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.22" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
}

subprojects {
    afterEvaluate {
        if (hasProperty("android")) {
            extensions.configure<com.android.build.gradle.BaseExtension> {
                compileSdkVersion(libs.versions.compileSdk.get().toInt())

                createDefaultConfig(this@afterEvaluate)
                createBuildTypes(this@afterEvaluate)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                packagingOptions {
                    resources {
                        excludes += "/META-INF/gradle/incremental.annotation.processors"
                    }
                }
            }
        }
    }
}

fun com.android.build.gradle.BaseExtension.createBuildTypes(project: Project) {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                project.file("proguard-android-optimize.txt"),
                project.file("proguard-rules.pro")
            )
        }
    }
}

fun com.android.build.gradle.BaseExtension.createDefaultConfig(project: Project) {
    defaultConfig {
        minSdk = project.libs.versions.minSdk.get().toInt()
        targetSdk = project.libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

//clean will delete all our already built files and then have full rebuild project
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
