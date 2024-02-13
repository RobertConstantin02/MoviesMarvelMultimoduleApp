
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.database"
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
//    implementation(project(":toplevel:network"))

    implementation(project(":api_interaction_helper:core"))

    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
        implementation(cache)
        implementation(arrow)
    }
    implementation(libs.gson)
    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
    implementation(libs.androidx.paging)
}