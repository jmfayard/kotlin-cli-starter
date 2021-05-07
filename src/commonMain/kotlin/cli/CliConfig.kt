package cli

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object CliConfig {
    val COMMAND_NAME = "git-standup"

    val GIT_STANDUP_WHITELIST = ".git-standup-whitelist"
    var CURRENT_GIT_USER = "me"
    val API_URL = "https://got-quotes.herokuapp.com/quotes"

    /** The Java API ProcessBuilder, used in executeCommandAndCaptureOutput()
     * needs an absolute path to the executable,
     * which we can find with the function findExecutable()
     * **/
    var FIND = "find"
    var GIT = "git"
}