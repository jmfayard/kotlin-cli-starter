package cli

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

fun main(args: Array<String>) {
    GlobalScope.promise {
        runGitStandup(args)
    }
}