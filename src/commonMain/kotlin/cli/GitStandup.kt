package cli

import cli.CliConfig.CURRENT_GIT_USER
import cli.CliConfig.FIND
import cli.CliConfig.GIT
import io.*
import okio.Buffer
import okio.BufferedSink
import okio.Path.Companion.toPath
import okio.buffer

/***
 * CUSTOMIZE_ME: this file is all specific to git-standup and can be deleted once understood
 */
suspend fun runGitStandup(args: Array<String>) {
    var options = ExecuteCommandOptions(directory = ".", abortOnError = true, redirectStderr = true, trim = true)

    //TODO: move section into nodejs actual code ?
    val jsPackage = "/build/js/packages/kotlin-cli-starter"
    val pwd = pwd(options)
    if (pwd.contains(jsPackage)) {
        options = options.copy(directory = pwd.removeSuffix(jsPackage))
    }
    GIT = findExecutable(GIT)
    FIND = findExecutable(FIND)
    CURRENT_GIT_USER = try {
        executeCommandAndCaptureOutput(listOf(GIT, "config", "user.name"), options)
    } catch (e: Exception) {
        println("Command 'git config user.name' failed with error $e")
        "me"
    }
    val command = CliCommand()
    val currentDirectory = pwd(options)

    command.main(args)

    if (command.help) {
        println(command.getFormattedHelp())
        return
    }
    command.reportFile = when(platform) {
        Platform.WINDOWS -> "$pwd\\git-standup-report.txt"
        else -> "$pwd/git-standup-report.txt"
    }
    if (command.report) {
        println("Generating ${command.reportFile}")
        fileSystem.delete(command.reportFile.toPath())
    }

    val gitRepositories = when(platform) {
        Platform.WINDOWS -> {
            executeCommandAndCaptureOutput(
                listOf("where", "-r", ".", "HEAD"),
                options.copy(abortOnError = false, directory = currentDirectory)
            )
                .lines()
                .filter { it.endsWith(".git\\HEAD") }
                .joinToString("\n") {
                it.substringBeforeLast("\\HEAD")
            }
        }
        else -> executeCommandAndCaptureOutput(
            command.findCommand(),
            options.copy(abortOnError = false, directory = currentDirectory)
        )
    }

    gitRepositories.lines().filter { it.contains(".git") }.forEach { path ->
        val repositoryPath = when {
            path.startsWith("./") -> "$currentDirectory/" + path.removePrefix("./")
            else -> path
        }.removeSuffix(".git").removeSuffix("/")
        findCommitsInRepo(repositoryPath, command)
    }
}


suspend fun findCommitsInRepo(repositoryPath: String, command: CliCommand) {
    val write: BufferedSink = fileSystem.appendingSink(command.reportFile.toPath()).buffer()
    fun log(message: String) {
        when {
            command.report -> {
                write.writeUtf8("$message\n")
            }
            else -> println(message)
        }
    }

    val options =
        ExecuteCommandOptions(directory = repositoryPath, abortOnError = true, redirectStderr = true, trim = true)

    if (fileIsReadable("$repositoryPath/.git").not()) {
        if (command.verbose) log("Skipping non-repository with path='$repositoryPath'")
    }
    if (command.verbose) {
        log("findCommitsInRepo($repositoryPath)")
    }
    // fetch the latest commits if necessary
    if (command.fetch) {
        val fetchCommand = listOf(GIT, "fetch", "--all")
        if (command.verbose) log(fetchCommand.joinToString(separator = " "))
        try {
            executeCommandAndCaptureOutput(fetchCommand, options)
        } catch (e: Exception) {
            log("Warning: could not fetch commits from repository $repositoryPath ; error $e")
        }
    }

    // history
    val result = executeCommandAndCaptureOutput(command.gitLogCommand(), options)
    if (result.isNotBlank()) {
        log("# $repositoryPath")
        log(result)
    } else if (command.silence.not()) {
        log("# $repositoryPath")
        log("No commits from ${command.authorName()} during this period")
    }
    if (command.verbose) {
        log("$ " + command.gitLogCommand())
    }
    write.close()
}