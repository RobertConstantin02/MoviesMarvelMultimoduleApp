plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.usecase"
    compileSdk = 34
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    implementation(project(":domain:domain_repository"))
    implementation(project(":domain:domain_model"))

    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.junit.jupiter.engine)

    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
        implementation(arrow)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
}