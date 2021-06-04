@file:OptIn(ExperimentalFileSystem::class)

package io

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import platform.posix._pclose
import platform.posix._popen
import platform.posix.chdir
import platform.posix.fgets

actual suspend fun findExecutable(executable: String): String =
    executable

/**
 * https://stackoverflow.com/questions/57123836/kotlin-native-execute-command-and-get-the-output
 */
actual suspend fun executeCommandAndCaptureOutput(
    command: List<String>,
    options: ExecuteCommandOptions
): String {
    chdir(options.directory)
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ") || arg.contains("%")) "\"$arg\"" else arg
    }
    println("executing: $commandToExecute")
    val redirect = if (options.redirectStderr) " 2>&1 " else ""
    val fp = _popen("$commandToExecute $redirect", "r") ?: error("Failed to run command: $command")

    val stdout = buildString {
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            append(input.toKString())
        }
    }

    val status = _pclose(fp)
    if (status != 0 && options.abortOnError) {
        println(stdout)
        println("failed to run: $commandToExecute")
        throw Exception("Command `$command` failed with status $status${if (options.redirectStderr) ": $stdout" else ""}")
    }

    return if (options.trim) stdout.trim() else stdout
}

actual suspend fun pwd(options: ExecuteCommandOptions): String {
    return when(platform) {
        Platform.WINDOWS -> executeCommandAndCaptureOutput(listOf("echo", "%cd%"), options).trim('"', ' ')
        else -> executeCommandAndCaptureOutput(listOf("pwd"), options).trim()
    }
}

actual fun runTest(block: suspend () -> Unit) =
    runBlocking { block() }