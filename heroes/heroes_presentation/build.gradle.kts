import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.heroes_presentation"
    compileSdk = 33
}

dependencies {
    implementation(project(":core_ui"))
    implementation(project(":heroes:heroes_domain"))


    libs.bundles.apply {
        implementation(compose)
        implementation(coroutines)
        implementation(lifecycle)
    }
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test.unit)
}