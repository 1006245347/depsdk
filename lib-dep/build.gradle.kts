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
    api(libs.glide.core)
    api(libs.glide.okhttp3)
    api(libs.rvBase)
    api(libs.immersionBar)
    api(libs.permissionX)
    api(libs.bugly)
    api(libs.mmkv)
    api(libs.httpDownload)
    api(libs.ok3.core)
    api(libs.ok3.log)
    api(libs.ok3.sse)
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.lifecycle.model)
    api(libs.lifecycle.live)
    api(libs.lifecycle.run)
    api(libs.toast)
    kapt(libs.compiler)
    api(libs.eventbus)
    annotationProcessor(libs.eventbus.annotation.processor)

}