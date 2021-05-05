package cli

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object CliConfig {
    val COMMAND_NAME = "git-standup"
    val GIT_STANDUP_WHITELIST = ".git-standup-whitelist"
    var GIT = "git"
    var FIND = "find"
    var CURRENT_GIT_USER = "me"
    val API_URL = "https://got-quotes.herokuapp.com/quotes"
}