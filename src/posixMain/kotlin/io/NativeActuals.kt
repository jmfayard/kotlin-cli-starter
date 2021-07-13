@file:OptIn(ExperimentalFileSystem::class)

package io

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import okio.FileSystem
import platform.posix.chdir
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

actual suspend fun findExecutable(executable: String): String =
    executable


actual suspend fun pwd(options: ExecuteCommandOptions): String {
    return executeCommandAndCaptureOutput(listOf("pwd"), options).trim()
}

actual fun runTest(block: suspend () -> Unit) =
    runBlocking { block() }