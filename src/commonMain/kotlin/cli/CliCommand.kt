package cli

import cli.ObsidianConfig.COMMAND_NAME
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class CliCommand : CliktCommand(
    help = """
       Obsidian helper
    """.trimIndent(), epilog = """
        Examples:
            $COMMAND_NAME -a "John Doe" -w "MON-FRI" -m 3
    """.trimIndent(), name = COMMAND_NAME
) {
    init {
        completionOption()
    }

    val help by option("-h", "--help", help = "help").flag(defaultForHelp = "disabled")
    val verbose by option("-v", "--verbose", help = "verbose").flag(defaultForHelp = "disabled")

    override fun run() {
        if (verbose) println(this)
        println("Obisidian is magic")
    }
}

