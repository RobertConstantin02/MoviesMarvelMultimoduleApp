
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.di"
    compileSdk = 33

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {

//    api(project(":featureFeed:feed_domain"))
    implementation(project(":data:database"))
//    implementation(project(":toplevel:database"))
//    implementation(project(":toplevel:network"))
    libs.bundles.apply {
        implementation(hilt)
        implementation(network)
    }
    kapt(libs.hilt.compiler)
}