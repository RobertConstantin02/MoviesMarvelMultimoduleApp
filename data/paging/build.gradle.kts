
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.paging"
    compileSdk = 33

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
    implementation(project(":data:remote"))
    implementation(project(":data:data_mapper"))

    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
        implementation(arrow)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
    //Unit test
//    testImplementation(kotlin("test"))
//    testImplementation(libs.bundles.test)
//
//    tasks.withType<Test> {
//        useJUnitPlatform()
//        testLogging {
//            events("passed", "skipped", "failed")
//        }
//    }
}