package cli

import cli.CliConfig.CURRENT_GIT_USER
import cli.CliConfig.FIND
import cli.CliConfig.GIT
import io.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable

suspend fun runGitStandup(args: Array<String>) {
    val options = ExecuteCommandOptions(directory = ".", abortOnError = true, redirectStderr = true, trim = true)
    GIT = findExecutable(GIT)
    FIND = findExecutable(FIND)
    CURRENT_GIT_USER = executeCommandAndCaptureOutput(listOf(GIT, "config", "user.name"), options)

    val command = CliCommand()
    val currentDirectory = executeCommandAndCaptureOutput(listOf("pwd"), options).trim()

    command.main(args)

    if (command.help) {
        println(command.getFormattedHelp())
        return
    } else if (command.quote) {
        fetchAndPrintRandomQuote()
        return
    }
    val gitRepositories =
        executeCommandAndCaptureOutput(
            command.findCommand(),
            options.copy(abortOnError = false, directory = currentDirectory)
        )
    gitRepositories.lines().filter { it.contains(".git") }.forEach { path ->
        val repositoryPath = when {
            path.startsWith("./") -> "$currentDirectory/" + path.removePrefix("./")
            else -> path
        }.removeSuffix(".git").removeSuffix("/")
        findCommitsInRepo(repositoryPath, command)
    }
}


fun findCommitsInRepo(repositoryPath: String, command: CliCommand) {
    val options =
        ExecuteCommandOptions(directory = repositoryPath, abortOnError = true, redirectStderr = true, trim = true)

    if (fileIsReadable("$repositoryPath/.git").not()) {
        if (command.verbose) println("Skipping non-repository with path='$repositoryPath'")
    }
    if (command.verbose) {
        println("findCommitsInRepo($repositoryPath)")
    }
    // fetch the latest commits if necessary
    if (command.fetch) {
        val fetchCommand = listOf(GIT, "fetch", "--all")
        if (command.verbose) println(fetchCommand)
        try {
            executeCommandAndCaptureOutput(fetchCommand, options)
        } catch (e: Exception) {
            println("Warning: could not fetch commits from repository $repositoryPath ; error $e")
        }
    }

    // history
    val result = executeCommandAndCaptureOutput(command.gitLogCommand(), options)
    if (result.isNotBlank()) {
        println("# $repositoryPath")
        println(result)
    } else if (command.silence.not()) {
        println("# $repositoryPath")
        println("No commits from ${command.authorName()} during this period")
    }
    if (command.verbose) {
        println("$ " + command.gitLogCommand())
    }
}


/**
 * Networking can be done in Kotlin Native with Ktor-Client
 * https://ktor.io/docs/getting-started-ktor-client.html
 * */
suspend fun fetchAndPrintRandomQuote() {
    buildHttpClient().use { client ->
        val quote = client.get<GameOfThroneQuote>(CliConfig.API_URL) {

        }
        println("> ${quote.quote}")
        println("-- ${quote.character}")
    }
}

/**
 * Serialization can be done in Kotlin Native with KotlinX Serialization
 * https://github.com/Kotlin/kotlinx.serialization
 * */
@Serializable
data class GameOfThroneQuote(
    val quote: String,
    val character: String,
)
