plugins {
    id("io.verse.kmm.library")
}

apply("${project.rootProject.file("gradle/github_repo_access.gradle")}")

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.tagd.arch)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "io.verse.system.core"
}

pomBuilder {
    description.set("System Core library")
}