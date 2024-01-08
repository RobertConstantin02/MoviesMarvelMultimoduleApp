plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.api"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {

//    api(project(":featureFeed:feed_domain"))
//    implementation(project(":toplevel:resources"))
//    implementation(project(":toplevel:database"))
    api(project(":api_interaction_helper:retrofit"))
    api(project(":api_interaction_helper:core"))

    implementation(libs.bundles.hilt)
    implementation(libs.retrofit)
    implementation(libs.gson)
    kapt(libs.hilt.compiler)

}