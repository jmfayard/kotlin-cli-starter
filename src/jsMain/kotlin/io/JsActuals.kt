@file:OptIn(ExperimentalFileSystem::class)

package io

import path.path
import child_process.ExecOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.NodeJsFileSystem
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise.Companion.resolve


actual val fileSystem: FileSystem = NodeJsFileSystem

actual suspend fun findExecutable(executable: String): String =
    executable



actual suspend fun pwd(options: ExecuteCommandOptions): String {
    return when(platform) {
        Platform.WINDOWS -> executeCommandAndCaptureOutput(listOf("echo", "%cd%"), options).trim()
        else -> executeCommandAndCaptureOutput(listOf("pwd"), options).trim()
    }
}

actual fun runTest(block: suspend () -> Unit): dynamic =
    GlobalScope.promise { block() }

actual val compilationTarget = CompilationTarget.NODEJS

actual val platform: Platform by lazy {
    //  https://nodejs.org/api/os.html
    when(os.platform()) {
        "win32" -> Platform.WINDOWS
        "darwin" -> Platform.MACOS
        else -> Platform.LINUX
    }
}