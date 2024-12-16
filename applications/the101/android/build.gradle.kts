plugins {
    id("com.android.application")
    kotlin("android")
}

apply("${project.rootProject.file("gradle/secrets.gradle")}")

repositories {
    google()
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://maven.pkg.github.com/pavan2you/kmm-clean-architecture")

        credentials {
            username = extra["githubUser"] as? String
            password = extra["githubToken"] as? String
        }
    }
}

android {
    namespace = "io.verse.system.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "io.verse.system.android"
        minSdk = 21
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":libraries:system-core"))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.activity.compose)
}