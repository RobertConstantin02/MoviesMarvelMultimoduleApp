
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
    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            testOptions.unitTests.isIncludeAndroidResources = true // Robolectric config: https://robolectric.org/getting-started
        }
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
    testImplementation(project(":test"))

    //Unit test
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.core.ktx)
    //testImplementation(kotlin("test"))
    //testImplementation(libs.bundles.test)
    //testRuntimeOnly(libs.junit.jupiter.engine)

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

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}