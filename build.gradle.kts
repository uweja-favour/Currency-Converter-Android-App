

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.2") // Use the compatible version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")

    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
