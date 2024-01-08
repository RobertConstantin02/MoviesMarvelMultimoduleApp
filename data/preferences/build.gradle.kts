
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.preferences"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
//    implementation(project(":toplevel:resources"))
    libs.bundles.apply {
        implementation(hilt)
//        implementation(coroutines)
        implementation(arrow)
    }
    implementation(libs.gson)
    kapt(libs.hilt.compiler)
}