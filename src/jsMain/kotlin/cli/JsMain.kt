package cli

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@JsExport @JsName("gitStandup")
fun main(args: Array<String>) {
    GlobalScope.promise {
        runGitStandup(args)
    }
}