
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.resources"
    compileSdk = 33
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    libs.bundles.apply {
        implementation(arrow)
    }

    implementation(libs.bundles.network)
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}