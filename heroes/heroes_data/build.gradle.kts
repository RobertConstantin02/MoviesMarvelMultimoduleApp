import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.heroes_data"
    compileSdk = 33
}

dependencies {
    implementation(project(":heroes:heroes_domain"))
    implementation("androidx.test:monitor:1.5.0")
    androidTestImplementation("org.testng:testng:6.9.6")

    libs.bundles.apply {
        implementation(coroutines)
        implementation(network)
        implementation(room)
    }
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.gson)
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test.unit)
}