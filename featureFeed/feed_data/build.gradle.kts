
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.feed_data"
    compileSdk = 33

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {

    implementation(project(":featureFeed:feed_domain"))
    libs.bundles.apply {
        implementation(hilt)
        implementation(network)
        implementation(coroutines)
        implementation(cache)
    }
    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
    implementation(libs.hilt.navigation.compose)
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}