import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.firebase))
            implementation(libs.gitlive.firebase.crashlytics)
        }
        commonMain.dependencies {
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html#jetpack-compose-artifacts-used
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.material3.adaptive)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.gitlive.firebase.auth)

            implementation(libs.koalaplot)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.github.jakubherr.gitfit"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.jakubherr.gitfit"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4 // this version code should be incremented for every single AAB that is uploaded to Google Play console
        versionName = "0.9.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("staging") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        // this needed to be increased from 11 to 17 because of GitLive Firebase ¯\_(ツ)_/¯
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

configurations.all {
    // this forces GitLive Firebase to use a customized version of firebase java SDK published in local Maven repository
    // the reason is that version 0.45 is missing extremely basic features like account creation
    resolutionStrategy.eachDependency {
        if (requested.group == "dev.gitlive" && requested.name == "firebase-java-sdk") {
            useVersion("0.4.8-fork")
        }
    }
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
    additionalEditorconfig.set(
        mapOf(
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            "ktlint_no-wildcard-imports" to "disabled",
        ),
    )
}

dependencies {
    implementation(libs.androidx.material3.android) // note: why does this work on desktop?
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "io.github.jakubherr.gitfit.MainKt"

        nativeDistributions {
            // TODO try to debug and improve
            // include all modules is a hack to fix missing JVM libraries when packaging desktop
            // see https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-native-distribution.html#including-jdk-modules
            includeAllModules = true
            targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "io.github.jakubherr.gitfit"
            packageVersion = "1.0.0"
        }
    }
}
