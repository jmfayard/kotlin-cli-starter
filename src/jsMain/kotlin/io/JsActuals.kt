@file:OptIn(ExperimentalFileSystem::class)

package io

import child_process.ExecOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.NodeJsFileSystem
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


actual val fileSystem: FileSystem = NodeJsFileSystem

actual suspend fun findExecutable(executable: String): String =
    executable


actual suspend fun executeCommandAndCaptureOutput(
    command: List<String>, // "find . -name .git"
    options: ExecuteCommandOptions
): String {
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ")) "'$arg'" else arg
    }
    val redirect = if (options.redirectStderr) "2>&1 " else ""
    val execOptions = object : ExecOptions {
        init {
            cwd = options.directory
        }
    }
    return suspendCoroutine<String> { continuation ->
        child_process.exec("$commandToExecute $redirect", execOptions) { error, stdout, stderr ->
            if (error != null) {
                println(stderr)
                continuation.resumeWithException(error)
            } else {
                continuation.resume(if (options.trim) stdout.trim() else stdout)
            }
        }
    }
}


actual fun runTest(block: suspend () -> Unit): dynamic =
    GlobalScope.promise { block() }