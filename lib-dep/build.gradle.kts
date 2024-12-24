plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinKapt)
}

android {
    namespace = "com.lyentech.lib"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

//    api(libs.glide.core)
//    api(libs.glide.okhttp3)
//    kapt(libs.glide.compiler)
//    api(libs.rvBase)
//    api(libs.immersionBar)
//    api(libs.permissionX)
//    api(libs.bugly)
//    api(libs.mmkv)
//    api(libs.httpDownload)
//    api(libs.toast)
//    api(libs.eventbus)
//    kapt(libs.eventbus.annotation.processor)

    api(files("/libs/glide-4.14.2.aar"))
//    api(files("/libs/gifdecoder-4.14.2.aar"))
//    api(files("/libs/annotations-4.14.2.jar"))\//    api(files("/libs/disklrucache-4.14.2.jar"))
    api(files("/libs/okhttp3-integration-4.14.2.aar"))
//    kapt(files("/libs/compiler-4.14.2.jar"))
    api(files("/libs/BaseRecyclerViewAdapterHelper4-4.1.4.aar"))
    api(files("/libs/immersionbar-3.2.2.aar"))
    api(files("/libs/permissionx-1.8.0.aar"))
    api(files("/libs/crashreport-4.1.9.aar"))
    api(files("/libs/mmkv-1.2.13.aar"))
    api(files("/libs/android-http-download-manager-2.0.0.aar"))
    api(files("/libs/eventbus-3.2.0.jar"))
//    kapt(files("/libs/eventbus-annotation-processor-3.2.0.jar"))
    api(files("/libs/EasyWindow-10.62.aar"))

}