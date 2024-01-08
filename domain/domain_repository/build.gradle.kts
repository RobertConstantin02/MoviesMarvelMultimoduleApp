plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.domain_repository"
    compileSdk = 34
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    implementation(project(":toplevel:resources"))
    implementation(project(":domain:domain_model"))

    api(project(":api_interaction_helper:retrofit"))
    api(project(":api_interaction_helper:core"))

    implementation(libs.bundles.hilt)
    implementation(libs.bundles.arrow)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
}