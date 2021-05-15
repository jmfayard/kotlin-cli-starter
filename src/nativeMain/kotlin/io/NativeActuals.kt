@file:OptIn(ExperimentalFileSystem::class)

package io

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import okio.FileSystem
import platform.posix.*

actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual suspend fun findExecutable(executable: String): String =
    executable

/**
 * https://stackoverflow.com/questions/57123836/kotlin-native-execute-command-and-get-the-output
 */
actual suspend fun executeCommandAndCaptureOutput(
    command: List<String>, // "find . -name .git"
    options: ExecuteCommandOptions
): String {
    chdir(options.directory)
    val commandToExecute = command.joinToString(separator = " ") { arg ->
        if (arg.contains(" ")) "'$arg'" else arg
    }
    val redirect = if (options.redirectStderr) " 2>&1 " else ""
    val fp: CPointer<FILE>? = popen("$commandToExecute $redirect", "r")
    val buffer = ByteArray(4096)
    val returnString = StringBuilder()

    /* Open the command for reading. */
    if (fp == NULL) {
        printf("Failed to run command\n")
        exit(1)
    }

    /* Read the output a line at a time - output it. */
    var scan = fgets(buffer.refTo(0), buffer.size, fp)
    if (scan != null) {
        while (scan != NULL) {
            returnString.append(scan!!.toKString())
            scan = fgets(buffer.refTo(0), buffer.size, fp)
        }
    }
    /* close */
    pclose(fp)
    return if (options.trim) returnString.toString().trim() else returnString.toString()
}

actual fun runTest(block: suspend () -> Unit) =
    runBlocking { block() }