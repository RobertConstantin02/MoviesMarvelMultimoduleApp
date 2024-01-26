plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.retrofit"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    testOptions {
        unitTests {
            isIncludeAndroidResources = true // Robolectric config: https://robolectric.org/getting-started
        }
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {
    implementation(project(":api_interaction_helper:core"))
    libs.bundles.apply {
        implementation(hilt)
        implementation(network)
        implementation(coroutines)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    //Unit test
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.junit.jupiter.engine)

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}