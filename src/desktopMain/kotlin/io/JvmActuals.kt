package io

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okio.ExperimentalFileSystem
import okio.FileSystem
import java.io.File

@OptIn(ExperimentalFileSystem::class)
actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual fun executeCommandAndCaptureOutput(
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


actual fun findExecutable(executable: String): String =
    executeCommandAndCaptureOutput(listOf("which", executable), ExecuteCommandOptions(".", true, false, true))

actual fun buildHttpClient(): HttpClient {
    val okHttpClient = OkHttpClient.Builder().build()
    return HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = defaultSerializer()
        }
        engine {
            preconfigured = okHttpClient
        }
    }
}

actual fun runTest(block: suspend () -> Unit): Unit =
    runBlocking { block() }
