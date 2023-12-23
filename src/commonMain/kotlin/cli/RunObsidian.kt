package cli

import cli.ObsidianConfig.FIND
import cli.ObsidianConfig.GIT
import io.ExecuteCommandOptions
import io.findExecutable
import io.pwd


suspend fun runObsidian(args: Array<String>) {
    var options = ExecuteCommandOptions(directory = ".", abortOnError = true, redirectStderr = true, trim = true)

    //TODO: move section into nodejs actual code ?
    val jsPackage = "/build/js/packages/kotlin-cli-starter"
    val pwd = pwd(options)
    if (pwd.contains(jsPackage)) {
        options = options.copy(directory = pwd.removeSuffix(jsPackage))
    }
    GIT = findExecutable(GIT)
    FIND = findExecutable(FIND)
    val command = CliCommand()

    command.main(args)

    if (command.help) {
        println(command.getFormattedHelp())
        return
    }

}

