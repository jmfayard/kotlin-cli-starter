plugins {
    java
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


kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    jvm("desktop") {

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
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Ktor.client.okHttp)
                implementation(Square.okHttp3.okHttp)
            }
        }
        val desktopTest by getting {
            dependencies {

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
}

tasks.getByName<Test>("test") {
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


tasks.register<Exec>("shell-completion") {
// TODO: copy to /usr/local/share/bash-completion/completions
    val completeCommand = "ls -l"
    commandLine = completeCommand.split(" ")
}

tasks.register("runOnGitHub") {
    dependsOn( "nativeTest", "linkDebugExecutableNative", "shell-completion")
}