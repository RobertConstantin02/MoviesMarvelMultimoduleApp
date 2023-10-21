plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.common"
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
    implementation(project(":toplevel:designsystem"))
    implementation(project(":toplevel:resources"))
    implementation(project(":presentation:presentation_model"))
    libs.bundles.apply {
        implementation(compose)
    }

    implementation(libs.androidx.paging.compose)
}