import com.google.devtools.ksp.gradle.KspAATask
import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.example.mymoji.core.data"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") { option("lite") }
                create("kotlin") { option("lite") }
            }
        }
    }
}

tasks.withType<KspAATask>().configureEach {
    val protoTaskName = "generate${name.removePrefix("ksp").removeSuffix("Kotlin")}Proto"
    if (protoTaskName !in tasks.names) return@configureEach
    val protoTask = tasks.named<GenerateProtoTask>(protoTaskName)
    dependsOn(protoTask)
    val outputDir = protoTask.flatMap { it.outputBaseDirProperty }
    kspConfig.javaSourceRoots.from(outputDir.map { it.dir("java") })
    kspConfig.sourceRoots.from(outputDir.map { it.dir("kotlin") })
}

dependencies {
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)

    // Persistence
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore)
    implementation(libs.protobuf.kotlin.lite)

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
