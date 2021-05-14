@file:OptIn(ExperimentalFileSystem::class)
package io

import child_process.ExecException
import child_process.ExecOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.NodeJsFileSystem


actual val fileSystem: FileSystem = NodeJsFileSystem

actual fun findExecutable(executable: String): String =
    executable


/**
 * https://stackoverflow.com/questions/57123836/kotlin-native-execute-command-and-get-the-output
 */
actual fun executeCommandAndCaptureOutput(
    command: List<String>, // "find . -name .git"
    options: ExecuteCommandOptions
): String {
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ")) "'$arg'" else arg
    }
    val redirect = if (options.redirectStderr) "2>&1 " else ""
    val options = object : ExecOptions {
        init {
            cwd = options.directory
        }
    }
    child_process.exec("$commandToExecute $redirect", options) { error, stdout, stderr ->
        if (error != null) {
            println(stderr)
            error(error)
        }
    }
    return "OK"
}

actual fun runTest(block: suspend () -> Unit): dynamic =
    GlobalScope.promise { block() }