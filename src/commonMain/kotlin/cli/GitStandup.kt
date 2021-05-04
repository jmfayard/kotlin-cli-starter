package cli

import cli.CliConfig.CURRENT_GIT_USER
import cli.CliConfig.FIND
import cli.CliConfig.GIT
import io.ExecuteCommandOptions
import io.executeCommandAndCaptureOutput
import io.fileIsReadable
import io.findExecutable
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object CliConfig {
    val COMMAND_NAME = "git-standup"
    val GIT_STANDUP_WHITELIST = ".git-standup-whitelist"
    var GIT = "git"
    var FIND = "find"
    var CURRENT_GIT_USER = "me"
}

fun runGitStandup(args: Array<String>) {
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
