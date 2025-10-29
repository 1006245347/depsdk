plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinKapt)
    `maven-publish`
}

android {
    namespace = "com.lyentech.lib"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)
    api(libs.dex)

    //把这全搞成libs arr jar,如何
    api(libs.ok3.core)
    api(libs.ok3.log)
    api(libs.ok3.sse)
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.lifecycle.model)
    api(libs.lifecycle.live)
    api(libs.lifecycle.run)
    api(libs.rvBase)

    api(libs.glide.core)
    api(libs.glide.okhttp3)
    kapt(libs.glide.compiler) //kapt本地报错
    api(libs.immersionBar)
    api(libs.permissionX)
    api(libs.mmkv)
    api(libs.httpDownload)
    api(libs.toast)
    api(libs.eventbus)

    api(libs.um.core)
    api(libs.um.asms)
    api(libs.um.apm)
}


publishing {//https://jitpack.io
    publications {
        register<MavenPublication>("release") {
            groupId = "com.lyentech"
            artifactId = "dep"
            version = "0.2.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}