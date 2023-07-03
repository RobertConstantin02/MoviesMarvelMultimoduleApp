//repositories: where our dependencies are fetch from
//dependencies: android gradle version and kotlin version
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
    }
}
//clean will delete all our already built files and then have full rebuild project
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

//plugins{
//    id("com.google.dagger.hilt.android") version "2.44" apply false
//}