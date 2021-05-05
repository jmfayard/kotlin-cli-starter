import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "cli"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(Testing.junit.params)
    testRuntimeOnly(Testing.junit.engine)
}

application {
    mainClass.set("cli.MainKt")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    val desktop = jvm("desktop") {
        // cli.MainKt
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val commonMain by getting  {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:_")
                implementation(Ktor.client.core)
                implementation(Ktor.client.serialization)
                implementation(KotlinX.serialization.core)
                implementation(KotlinX.serialization.json)
                implementation(KotlinX.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test.common)
                implementation(Kotlin.test.annotationsCommon)
            }
        }
        val desktopMain: KotlinSourceSet by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Ktor.client.okHttp)
                implementation(Square.okHttp3.okHttp)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(Testing.junit.api)
                implementation(Testing.junit.engine)
                implementation(Kotlin.test.junit5)
            }
        }
        val nativeMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Ktor.client.curl)
            }
        }
        val nativeTest by getting {
        }

    }
    tasks.withType<JavaExec> {
        // code to make run task in kotlin multiplatform work
        val compilation = desktop.compilations.getByName<KotlinJvmCompilation>("main")

        val classes = files(
            compilation.runtimeDependencyFiles,
            compilation.output.allOutputs
        )
        classpath(classes)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("install") {
    val destDir = "/usr/local/bin"
    dependsOn("runDebugExecutableNative")
    from("build/bin/native/debugExecutable") {
        rename { "git-standup" }
    }
    into(destDir)
    doLast {
        println("$ git-standup installed into $destDir")
    }
}

tasks.register("runOnGitHub") {
    dependsOn( "allTests", "linkDebugExecutableNative", "shell-completion")
}