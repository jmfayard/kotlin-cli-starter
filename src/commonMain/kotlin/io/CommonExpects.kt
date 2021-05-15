@file:OptIn(ExperimentalFileSystem::class)
package io

import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath

/***
 * If you need to access platform-specific APIs from the shared code,
 * use the Kotlin mechanism of expected and actual declarations.
 *
 * https://kotlinlang.org/docs/mpp-connect-to-apis.html
 */

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

// call $ which $executable on the JVM
expect suspend fun findExecutable(executable: String): String

// runBlocking doens't exist on JavaScript therefore in common multiplatform code
// https://github.com/jmfayard/kotlin-cli-starter/issues/9
expect fun runTest(block: suspend () -> Unit): Unit
