
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.test"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    implementation(project(":data:api"))
    implementation(project(":data:database"))
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.junit.jupiter.engine)

    implementation(libs.gson)
    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
}