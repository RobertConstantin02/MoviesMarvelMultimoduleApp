
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.database"
    compileSdk = 33

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    libs.bundles.apply {
        implementation(hilt)
        implementation(cache)
    }
    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
    implementation(libs.androidx.paging)

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}