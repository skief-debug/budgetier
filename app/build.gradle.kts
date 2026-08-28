plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.budgettracker.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.budgettracker.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 11
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("budgetier-release.jks")
            storePassword = "budgetier123"
            keyAlias = "budgetier"
            keyPassword = "budgetier123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    flavorDimensions += "env"
    productFlavors {
        create("sandbox") {
            dimension = "env"
            buildConfigField("boolean", "ENABLE_AUTO_UPDATER", "true")
        }
        create("production") {
            dimension = "env"
            buildConfigField("boolean", "ENABLE_AUTO_UPDATER", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
}

afterEvaluate {
    tasks.findByName("assembleDebug")?.doLast {
        val apkFile = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile
        val destFile = rootDir.resolve("BudgeTier.apk")
        if (apkFile.exists()) {
            apkFile.copyTo(destFile, overwrite = true)
        }
    }
}




