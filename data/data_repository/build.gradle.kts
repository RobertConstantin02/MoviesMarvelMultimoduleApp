
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.data_repository"
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
    implementation(project(":data:remote"))
    implementation(project(":data:database"))
    implementation(project(":data:paging"))
    implementation(project(":data:data_mapper"))

    api(project(":domain:domain_repository"))
    api(project(":domain:domain_model"))
    api(project(":domain:usecase"))

    libs.bundles.apply {
        implementation(hilt)
        implementation(coroutines)
    }
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.paging)
}