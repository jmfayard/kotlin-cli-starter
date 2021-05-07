package io

import io.ktor.client.*

/***
 * If you need to access platform-specific APIs from the shared code,
 * use the Kotlin mechanism of expected and actual declarations.
 *
 * https://kotlinlang.org/docs/mpp-connect-to-apis.html
 */


expect fun buildHttpClient(): HttpClient

expect fun readAllText(filePath: String): String

expect fun writeAllText(filePath: String, text: String)

expect fun writeAllLines(
    filePath: String,
    lines: List<String>
)

expect fun fileIsReadable(filePath: String): Boolean

expect fun executeCommandAndCaptureOutput(
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
expect fun findExecutable(executable: String): String

// runBlocking doens't exist on JavaScript therefore in common multiplatform code
// https://github.com/jmfayard/kotlin-cli-starter/issues/9
expect fun runTest(block: suspend () -> Unit): Unit
