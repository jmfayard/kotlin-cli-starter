@file:OptIn(ExperimentalFileSystem::class)
package io

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.cinterop.*
import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import okio.FileSystem
import platform.posix.*

actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual fun findExecutable(executable: String): String =
    executable


actual fun buildHttpClient(): HttpClient =
    HttpClient(Curl) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    } // TODO : find out why ktor-client leaks memory
        .also { Platform.isMemoryLeakCheckerActive = false }


/**
 * https://stackoverflow.com/questions/57123836/kotlin-native-execute-command-and-get-the-output
 */
actual fun executeCommandAndCaptureOutput(
    command: List<String>, // "find . -name .git"
    options: ExecuteCommandOptions
): String {
    chdir(options.directory)
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ")) "'$arg'" else arg
    }
    val redirect = if (options.redirectStderr) " 2>&1 " else ""
    val fp = popen("$commandToExecute $redirect", "r") ?: error("Failed to run command: $command")

    val stdout = buildString {
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            append(input.toKString())
        }
    }

    val status = pclose(fp)
    if (status != 0 && options.abortOnError) {
        println(stdout)
        throw Exception("Command `$command` failed with status $status${if (options.redirectStderr) ": $stdout" else ""}")
    }

    return if (options.trim) stdout.trim() else stdout
}

actual fun runTest(block: suspend () -> Unit) =
    runBlocking { block() }