import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

group = "cli"
version = "1.0-SNAPSHOT"

// CUSTOMIZE_ME: the name of your command-line tool goes here
val PROGRAM = "http"

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
        else -> throw GradleException("Host $hostOs is not supported in Kotlin/Native.")
    }

    val desktop = jvm("desktop") {
        // cli.MainKt
    }

    val node = js(LEGACY) {
        nodejs()
        binaries.executable()
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }

        val commonMain by getting  {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:_")
                implementation("com.squareup.okio:okio-multiplatform:_")
                implementation(KotlinX.coroutines.core)

                /// implementation(Ktor.client.core)
                /// implementation(Ktor.client.serialization)
                implementation(KotlinX.serialization.core)
                implementation(KotlinX.serialization.json)
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
                /// implementation(Square.okHttp3.okHttp)
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
                /// implementation(Ktor.client.curl)
            }
        }
        val nativeTest by getting {

        }
        val jsMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio-nodefilesystem-js:_")
            }
        }
        val jsTest by getting {

        }

        sourceSets {
            all {
                languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
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
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        archiveVersion.set("")

        from(desktop.compilations.getByName("main").output)
        configurations = mutableListOf(
            desktop.compilations.getByName("main").compileDependencyFiles as Configuration,
            desktop.compilations.getByName("main").runtimeDependencyFiles as Configuration
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("install") {
    group = "run"
    description = "Build the native executable and install it"
    val destDir = "/usr/local/bin"
    dependsOn("runDebugExecutableNative")
    from("build/bin/native/debugExecutable") {
        rename { PROGRAM }
    }
    into(destDir)
    doLast {
        println("$ $PROGRAM installed into $destDir")
    }
}

tasks.register("runOnGitHub") {
    group = "run"
    description = "CI with Github Actions : .github/workflows/runOnGitHub.yml"
    dependsOn( "allTests", "linkDebugExecutableNative", "run")
}