import cli.runGitStandup
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    runBlocking {
        runGitStandup(args)
    }
}
