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


actual suspend fun pwd(options: ExecuteCommandOptions): String {
    return when(platform) {
        Platform.WINDOWS -> executeCommandAndCaptureOutput(listOf("echo", "%cd%"), options).trim('"', ' ')
        else -> executeCommandAndCaptureOutput(listOf("pwd"), options).trim()
    }
}

actual fun runTest(block: suspend () -> Unit) =
    runBlocking { block() }