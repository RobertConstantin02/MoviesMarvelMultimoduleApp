plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.feature_feed"
    compileSdk = 33
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
kapt {
    correctErrorTypes = true
}
dependencies {

    implementation(project(":toplevel:navigationlogic"))
    implementation(project(":toplevel:resources"))
    implementation(project(":presentation:presentation_model"))
    implementation(project(":presentation:presentation_mapper"))
    implementation(project(":domain:domain_model"))
    implementation(project(":domain:usecase"))

    libs.bundles.apply {
        implementation(compose)
        implementation(hilt)
        implementation(coroutines)
        implementation(lifecycle)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.common)
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}