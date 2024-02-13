
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.paging"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
kapt {
    correctErrorTypes = true
}
dependencies {

    api(project(":data:api"))
    api(project(":data:database"))
    api(project(":data:remote"))
    api(project(":data:data_mapper"))
    implementation(project(":api_interaction_helper:core"))

    testImplementation(project(":test"))

    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
        implementation(arrow)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
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