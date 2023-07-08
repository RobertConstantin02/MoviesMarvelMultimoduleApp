
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.udemycourseapp"
    compileSdk = 33

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion =  libs.versions.kotlinCompilerExtensionVersion.get()
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
    implementation(project(":toplevel:navigationlogic"))
    implementation(project(":toplevel:designsystem"))
    implementation(project(":featureFeed:feed_data"))
    implementation(project(":featureFeed:feed_domain"))
    implementation(project(":featureFeed:feed_presentation"))
    implementation(project(":featureFavorites:favorites_data"))
    implementation(project(":featureFavorites:favorites_domain"))
    implementation(project(":featureFavorites:favorites_presentation"))

    libs.bundles.apply {
        implementation(compose)
        implementation(hilt)
        implementation(coroutines)
        implementation(lifecycle)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

