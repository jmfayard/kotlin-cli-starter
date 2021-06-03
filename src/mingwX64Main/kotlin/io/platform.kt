package io

import kotlinx.coroutines.runBlocking

actual val platform: Platform by lazy {
    val uname = runBlocking {
        try {
            val whereResult = executeCommandAndCaptureOutput(
                listOf("where", "uname"),
                ExecuteCommandOptions(
                    directory = ".",
                    abortOnError = true,
                    redirectStderr = false,
                    trim = true,
                ),
            )
            println(whereResult)
            if(whereResult.contains("Could not find")) {
                println("uname is not available")
                return@runBlocking ""
            }
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
        uname.startsWith("MINGW") -> Platform.LINUX
        uname.startsWith("CYGWIN") -> Platform.LINUX
        else -> Platform.WINDOWS
    }.also {
        println("platform is $it")
    }
//    Platform.WINDOWS

}