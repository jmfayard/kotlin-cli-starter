@file:OptIn(ExperimentalFileSystem::class)
package io

import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath

expect val platform: Platform
expect val compilationTarget: CompilationTarget
expect val fileSystem: FileSystem

fun readAllText(filePath: String): String =
    fileSystem.read(filePath.toPath()) {
        readUtf8()
    }

fun writeAllText(filePath: String, text: String): Unit =
    fileSystem.write(filePath.toPath()) {
        writeUtf8(text)
    }

fun writeAllLines(
    filePath: String,
    lines: List<String>
) = writeAllText(filePath, lines.joinToString(separator = "\n"))

fun fileIsReadable(filePath: String): Boolean =
    fileSystem.exists(filePath.toPath())

expect suspend fun executeCommandAndCaptureOutput(
    command: List<String>,
    options: ExecuteCommandOptions
): String

data class ExecuteCommandOptions(
    val directory: String,
    val abortOnError: Boolean,
    val redirectStderr: Boolean,
    val trim: Boolean
)

expect suspend fun pwd(options: ExecuteCommandOptions): String

// call $ which $executable on the JVM
expect suspend fun findExecutable(executable: String): String

// runBlocking doens't exist on JavaScript therefore in common multiplatform code
// https://github.com/jmfayard/kotlin-cli-starter/issues/9
expect fun runTest(block: suspend () -> Unit): Unit
