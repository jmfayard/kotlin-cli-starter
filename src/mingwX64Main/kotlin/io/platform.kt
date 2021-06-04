package io

import kotlinx.coroutines.runBlocking

actual val compilationTarget = CompilationTarget.WINDOWS
actual val platform: Platform by lazy {
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
//    Platform.WINDOWS

}