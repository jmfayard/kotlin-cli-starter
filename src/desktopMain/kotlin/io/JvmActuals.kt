package io

import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import okio.FileSystem
import java.io.File

@OptIn(ExperimentalFileSystem::class)
actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual suspend fun executeCommandAndCaptureOutput(
    command: List<String>,
    options: ExecuteCommandOptions
): String {
    val builder = ProcessBuilder()
    builder.command(command.filter { it.isNotBlank() })
    builder.directory(File(options.directory))
    val process = builder.start()
    val stdout = process.inputStream.bufferedReader().use { it.readText() }
    val stderr = process.errorStream.bufferedReader().use { it.readText() }
    val exitCode = process.waitFor()
    if (options.abortOnError) assert(exitCode == 0)
    val output = if (stderr.isBlank()) stdout else "$stdout $stderr"
    return if (options.trim) output.trim() else output
}


actual suspend fun findExecutable(executable: String): String =
    executeCommandAndCaptureOutput(listOf("which", executable), ExecuteCommandOptions(".", true, false, true))

actual fun runTest(block: suspend () -> Unit): Unit =
    runBlocking { block() }
