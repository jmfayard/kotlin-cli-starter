package cli

import io.ExecuteCommandOptions
import io.executeCommandAndCaptureOutput
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun File.modifyExif(date: LocalDateTime) {
    executeCommandAndCaptureOutput(
        listOf("exiv2", "-M", "set  Exif.Image.DateTime Ascii ${date.format()}", absolutePath),
        ExecuteCommandOptions(".", abortOnError = true, redirectStderr = true, trim = true)
    )
}

fun main(args: Array<String>) {
    val directory = "/Users/jmfayard/Downloads/CMH-photos rue Désiré"
    //val directory = "/Users/jmfayard/Downloads/test"
    val files = File(directory).walkTopDown().filter { it.extension in listOf("jpg", "jpeg", "JPG", "JPEG") }
    runBlocking {
        for (file in files) {
            println("${dateOf(file.name)?.format()} for <${file.name}>")
            val date = dateOf(file.name)
            if (date != null) {
                file.modifyExif(date)
            }
        }
    }
}

/**
 *
17890714 = date connue, le 14 juillet 1789
17890700 = c’est en juillet 1789, mais on ne connaît pas le jour
17890000 = c’est en 1789, mais on n’en sait pas plus
17890007 = c’est en 1789 à 7 ans près
 */
fun dateOf(name: String): LocalDateTime? {
    if (name.length < 8) return null
    var year = name.substring(0, 4).toIntOrNull() ?: return null
    var month = name.substring(5, 6).toIntOrNull() ?: return null
    var day = name.substring(7, 8).toIntOrNull() ?: return null
    when {
        month == 0 && day == 0 -> {
            month = 1; day = 1
        }
        day == 0 -> day = 1
        month == 0 -> month = 1
    }
    return LocalDateTime.of(year, month, day, 12, 0, 0)
}

val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
fun LocalDateTime.format() = isoFormatter.format(this).replace('T', ' ')
