package io

expect fun readAllText(filePath: String): String

expect fun writeAllText(filePath: String, text: String)

expect fun writeAllLines(
    filePath: String,
    lines: List<String>
)

expect fun fileIsReadable(filePath: String): Boolean

expect fun executeCommandAndCaptureOutput(
    command: List<String>,
    options: ExecuteCommandOptions
): String

data class ExecuteCommandOptions(
    val directory: String,
    val abortOnError: Boolean,
    val redirectStderr: Boolean,
    val trim: Boolean
)

// call $ which $executable on the JVM
expect fun findExecutable(executable: String): String
