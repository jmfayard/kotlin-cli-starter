package cli

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object CliConfig {
    // CUSTOMIZE_ME: use your own command-name here
    val COMMAND_NAME = "git-standup"

    // CUSTOMIZE_ME: this is specific to git-standup and can be deleted
    val GIT_STANDUP_WHITELIST = ".git-standup-whitelist"
    var CURRENT_GIT_USER = "me"
    val API_URL = "https://got-quotes.herokuapp.com/quotes"

    /** The Java API ProcessBuilder, used in executeCommandAndCaptureOutput()
     * needs an absolute path to the executable,
     * which we can find with the function findExecutable()
     * **/
    // CUSTOMIZE_ME: every subcommand that you are using should be declared here
    var FIND = "find"
    var GIT = "git"
}