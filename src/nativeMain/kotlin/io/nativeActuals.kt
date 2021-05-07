package io

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.cinterop.*
import platform.posix.*

/**
 * See https://nequalsonelifestyle.com/2020/11/16/kotlin-native-file-io/
 * **/
actual fun readAllText(filePath: String): String {
    val returnBuffer = StringBuilder()
    val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file $filePath")

    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }

    return returnBuffer.toString()
}


actual fun writeAllText(filePath: String, text: String) {
    val file = fopen(filePath, "w") ?: throw IllegalArgumentException("Cannot open output file $filePath")
    try {
        memScoped {
            if (fputs(text, file) == EOF) throw Error("File write error")
        }
    } finally {
        fclose(file)
    }
}


actual fun writeAllLines(filePath: String, lines: List<String>) {
    val file = fopen(filePath, "w") ?: throw IllegalArgumentException("Cannot open output file $filePath")
    try {
        memScoped {
            lines.forEach {
                if (fputs(it + "\n", file) == EOF) {
                    throw Error("File write error")
                }
            }
        }
    } finally {
        fclose(file)
    }
}

actual fun fileIsReadable(filePath: String): Boolean =
    access(filePath, R_OK ) == 0

actual fun findExecutable(executable: String): String =
    executable


actual fun buildHttpClient(): HttpClient =
    HttpClient(Curl) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    } // TODO : find out why ktor-client leaks memory
        .also { Platform.isMemoryLeakCheckerActive = false }


/**
 * https://stackoverflow.com/questions/57123836/kotlin-native-execute-command-and-get-the-output
 */
actual fun executeCommandAndCaptureOutput(
    command: List<String>, // "find . -name .git"
    options: ExecuteCommandOptions
): String {
    chdir(options.directory)
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ")) "'$arg'" else arg
    }
    val redirect = if (options.redirectStderr) " 2>&1 " else ""
    val fp = popen("$commandToExecute $redirect", "r") ?: error("Failed to run command: $command")

    val stdout = buildString {
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            append(input.toKString())
        }
    }

    val status = pclose(fp)
    if (status != 0 && options.abortOnError) {
        println(stdout)
        throw Exception("Command `$command` failed with status $status${if (options.redirectStderr) ": $stdout" else ""}")
    }

    return if (options.trim) stdout.trim() else stdout
}

