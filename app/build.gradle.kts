plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.udemycourseapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.udemycourseapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled =  true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion =  "1.2.0"
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

/**
 * kapt: The kapt keyword is used to declare annotation processors in your build script. Annotation
 * processors are tools that generate code at compile-time based on annotations present in your source code.
 * The kapt keyword tells the build system to apply the annotation processor during the compilation phase.
 *
 * In the case of kapt(libs.hilt.android.compiler), it specifies that the libs.hilt.android.compiler library
 * contains an annotation processor that needs to be applied during compilation. This is commonly
 * used for libraries that use annotation processing, such as Dagger or Hilt.
 */
dependencies {

    libs.bundles.apply {
        implementation(compose)
        implementation(coroutines)
        implementation(network)
        implementation(lifecycle)
    }
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.gson)
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test.unit)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}