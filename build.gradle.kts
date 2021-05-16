import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

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
    jcenter()
}

dependencies {
    testImplementation(Testing.junit.params)
    testRuntimeOnly(Testing.junit.engine)
}

application {
    mainClass.set("cli.JvmMainKt")
}

kotlin {

    macosX64 { binaries { executable { entryPoint = "main" } } }
    mingwX64 { binaries { executable { entryPoint = "main" } } }
    linuxX64 { binaries { executable { entryPoint = "main" } } }

    val jvmTarget = jvm()

    val node = js(LEGACY) {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
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
        getByName("jvmMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(Ktor.client.okHttp)
                /// implementation(Square.okHttp3.okHttp)
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(Testing.junit.api)
                implementation(Testing.junit.engine)
                implementation(Kotlin.test.junit5)
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                /// implementation(Ktor.client.curl)
            }
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        val posixMain by creating {
            dependsOn(nativeMain)
        }
        val posixTest by creating {
            dependsOn(nativeTest)
        }
        arrayOf("macosX64", "linuxX64").forEach { targetName ->
            getByName("${targetName}Main").dependsOn(posixMain)
            getByName("${targetName}Test").dependsOn(posixTest)
        }
        arrayOf("macosX64", "linuxX64", "mingwX64").forEach { targetName ->
            getByName("${targetName}Main").dependsOn(nativeMain)
            getByName("${targetName}Test").dependsOn(nativeTest)
        }
        getByName("jsMain") {
            dependencies {
                implementation("com.squareup.okio:okio-nodefilesystem-js:_")
                implementation(KotlinX.nodeJs)
            }
        }
        getByName("jsTest") {
            dependsOn(nativeTest)
            dependencies {
                implementation(Kotlin.test.jsRunner)
                implementation(kotlin("test-js"))
            }
        }

        sourceSets {
            all {
                languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
                languageSettings.useExperimentalAnnotation("okio.ExperimentalFileSystem")
            }
        }
    }

    tasks.withType<JavaExec> {
        // code to make run task in kotlin multiplatform work
        val compilation = jvmTarget.compilations.getByName<KotlinJvmCompilation>("main")

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

        from(jvmTarget.compilations.getByName("main").output)
        configurations = mutableListOf(
            jvmTarget.compilations.getByName("main").compileDependencyFiles as Configuration,
            jvmTarget.compilations.getByName("main").runtimeDependencyFiles as Configuration
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

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> "MacosX64"
        hostOs == "Linux" -> "LinuxX64"
        isMingwX64 -> "MingwX64"
        else -> throw GradleException("Host $hostOs is not supported in Kotlin/Native.")
    }
    dependsOn("runDebugExecutable$nativeTarget")
    from("build/bin/native/debugExecutable") {
        rename { PROGRAM }
    }
    into(destDir)
    doLast {
        println("$ $PROGRAM installed into $destDir")
    }
}

tasks.register("allRun") {
    group = "run"
    description = "Run git-standup on the JVM, on Node and natively"
    dependsOn("run", "jsNodeRun", "runDebugExecutableNative")
}

tasks.register("runOnGitHub") {
    group = "run"
    description = "CI with Github Actions : .github/workflows/runOnGitHub.yml"
    dependsOn("allTests", "allRun")
}

interface Injected {
    @get:Inject
    val exec: ExecOperations
    @get:Inject
    val fs: FileSystemOperations
}

tasks.register("completions") {
    group = "run"
    description = "Generate Bash/Zsh/Fish completion files"
    dependsOn(":install")
    val injected = project.objects.newInstance<Injected>()
    val shells = listOf(
        Triple("bash", file("completions/git-standup.bash"), "/usr/local/etc/bash_completion.d"),
        Triple("zsh", file("completions/_git_standup.zsh"), "/usr/local/share/zsh/site-functions"),
        Triple("fish", file("completions/git-standup.fish"), "/usr/local/share/fish/vendor_completions.d"),
    )
    for ((SHELL, FILE, INSTALL) in shells) {
        actions.add {
            println("Updating   $SHELL completion file at $FILE")
            injected.exec.exec {
                commandLine("git-standup", "--generate-completion", SHELL)
                standardOutput = FILE.outputStream()
            }
            println("Installing $SHELL completion into $INSTALL")
            injected.fs.copy {
                from(FILE)
                into(INSTALL)
            }
        }
    }
    doLast {
        println("On macOS, follow those instructions to configure shell completions")
        println("👀 https://docs.brew.sh/Shell-Completion 👀")
    }
}