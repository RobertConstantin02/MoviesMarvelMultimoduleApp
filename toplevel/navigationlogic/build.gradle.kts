
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.navigationlogic"
    compileSdk = 34
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {

    libs.bundles.apply {
        implementation(compose)
    }

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}