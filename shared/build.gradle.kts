
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("SNDatabase") {
            packageName.set("com.example.marsphotos.data")
        }
    }
}

kotlin {
    // 1. Target de Android para KMP
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // 2. Target de Desktop (JVM)
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor y Serialización de JSON
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Runtime base de SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation("app.cash.sqldelight:coroutines-extensions:2.1.0")
        }

        androidMain.dependencies {
            // Driver nativo de Android
            implementation(libs.sqldelight.android.driver)

            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation("io.ktor:ktor-client-android:${libs.versions.ktor.get()}")
        }

        jvmMain.dependencies {
            // Driver nativo de Desktop (JVM)
            implementation(libs.sqldelight.jvm.driver)
            implementation("io.ktor:ktor-client-okhttp:${libs.versions.ktor.get()}")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// 3. Bloque nativo de Android va AFUERA de la etiqueta kotlin {}
android {
    namespace = "com.example.marsphotos.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
}
