import java.util.Properties
import java.io.FileInputStream

plugins{
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Load local.properties for environment-specific configuration
val localProperties=Properties()
val localPropertiesFile=rootProject.file("local.properties")
if (localPropertiesFile.exists()){
    localProperties.load(FileInputStream(localPropertiesFile))
}

// Helper function to get property with fallback
fun getLocalProperty(key: String, defaultValue: String): String{
    return localProperties.getProperty(key, defaultValue)
}

android{
    namespace="com.tp.blassa"
    compileSdk=36

    defaultConfig{
        applicationId="com.tp.blassa"
        minSdk=24
        targetSdk=36
        versionCode=1
        versionName="1.0"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        
        // Environment configuration from local.properties
        // For development: Set BASE_URL in local.properties
        // For emulator: http://10.0.2.2:8088/api/v1/
        // For physical device: http://YOUR_IP:8088/api/v1/
        val devBaseUrl=getLocalProperty("BASE_URL", "https://tangela-nonseismic-mekhi.ngrok-free.dev/api/v1/")
        val googleClientId=getLocalProperty("GOOGLE_CLIENT_ID", "484738135148-ojm214hd016n0u91nuo6n72v5i5mf4en.apps.googleusercontent.com")
        val prodBaseUrl=getLocalProperty("PROD_BASE_URL", "https://blassa-production.up.railway.app/api/v1/")
        
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"$googleClientId\"")
        buildConfigField("String", "BASE_URL", "\"$devBaseUrl\"")
        buildConfigField("String", "PROD_BASE_URL", "\"$prodBaseUrl\"")
    }

    buildTypes{
        release{
            isMinifyEnabled=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use production URL for release builds
            val prodBaseUrl=getLocalProperty("PROD_BASE_URL", "https://blassa-production.up.railway.app/api/v1/")
            buildConfigField("String", "BASE_URL", "\"$prodBaseUrl\"")
        }
    }
    compileOptions{
        sourceCompatibility=JavaVersion.VERSION_11
        targetCompatibility=JavaVersion.VERSION_11
    }
    kotlinOptions{
        jvmTarget="11"
    }
    buildFeatures{
        compose=true
        buildConfig=true
    }
}

dependencies{

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    // Google Sign-In (Credential Manager)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    // Encrypted Storage
    implementation(libs.security.crypto)
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}