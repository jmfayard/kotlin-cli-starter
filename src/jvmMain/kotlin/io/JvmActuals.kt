package io

import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import okio.FileSystem
import java.io.File

@OptIn(ExperimentalFileSystem::class)
actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual suspend fun executeCommandAndCaptureOutput(
    command: List<String>,
    options: ExecuteCommandOptions,
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

actual suspend fun findExecutable(executable: String): String = when (platform) {
    Platform.WINDOWS -> executeCommandAndCaptureOutput(listOf("where", executable),
        ExecuteCommandOptions(".", true, false, true))
    else -> executeCommandAndCaptureOutput(listOf("which", executable),
        ExecuteCommandOptions(".", true, false, true))
//    else -> executable
}

actual fun runTest(block: suspend () -> Unit): Unit =
    runBlocking { block() }

actual suspend fun pwd(options: ExecuteCommandOptions): String {
    return File(".").absolutePath
}

actual val compilationTarget = CompilationTarget.JVM
actual val platform: Platform by lazy {
    val osName = System.getProperty("os.name").lowercase()

    when {
        osName.startsWith("windows") -> {
            val uname = runBlocking {
                try {
                    executeCommandAndCaptureOutput(
                        listOf("where", "uname"),
                        ExecuteCommandOptions(
                            directory = ".",
                            abortOnError = true,
                            redirectStderr = false,
                            trim = true,
                        ),
                    )
                    executeCommandAndCaptureOutput(
                        listOf("uname", "-a"),
                        ExecuteCommandOptions(
                            directory = ".",
                            abortOnError = true,
                            redirectStderr = true,
                            trim = true,
                        ),
                    )
                } catch (e: Exception) {
                    ""
                }
            }
            if(uname.isNotBlank()) {
                println("uname: $uname")
            }
            when {
                uname.startsWith("MSYS") -> Platform.LINUX
                uname.startsWith("MINGW") -> Platform.LINUX
                uname.startsWith("CYGWIN") -> Platform.LINUX
                else -> Platform.WINDOWS
            }.also {
                println("platform is $it")
            }
        }
        osName.startsWith("linux") -> Platform.LINUX
        osName.startsWith("mac") -> Platform.MACOS
        osName.startsWith("darwin") -> Platform.MACOS
        else -> error("unknown osName: $osName")
    }
}