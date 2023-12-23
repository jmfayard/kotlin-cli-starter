package cli

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ObsidianConfig {
    val COMMAND_NAME = "kobs"

    /** The Java API ProcessBuilder, used in executeCommandAndCaptureOutput()
     * needs an absolute path to the executable,
     * which we can find with the function findExecutable()
     * **/
    var FIND = "find"
    var GIT = "git"
}