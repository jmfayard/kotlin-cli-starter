package cli

import kotlin.test.Test
import kotlin.test.assertEquals

/***
 * CUSTOMIZE_ME: the tests are specific to git-standup and can be deleted once understood
 * Write your own unit tests!
 */
class CliCommandTest {
    private fun assertLogCommand(args: String, expected: String) {
        fun String.simplify() =
            replace("\\s+".toRegex(), " ").replace("'", "").replace("\"", "")

        val cmd = CliCommand()
        cmd.main(args.split(" ").filter { it.isNotBlank() }.also { println(it) })
        val actual = cmd.gitLogCommand().joinToString(separator = " ").simplify()
        assertEquals(expected.simplify(), actual, "Invalid git log for args='$args'")
    }


    @Test
    fun gitLogWithNoArgument() {
        assertLogCommand(
            "",
            "git --no-pager log --all --no-merges --since=yesterday --author=me --abbrev-commit --oneline --pretty=format:'%Cred%h%Creset - %s %Cgreen(%cd) %C(bold blue)<%an>%Creset' --date='relative' --color=always",
        )
    }

    @Test
    fun gitLogForExample() {
        val expected =
            "git --no-pager log --all --no-merges --since=\"yesterday\" --author=\"John\" --abbrev-commit --oneline --pretty=format:'%Cred%h%Creset - %s %Cgreen(%cd) %C(bold blue)<%an>%Creset' --date='relative' --color=always"
        assertLogCommand("-a John -w MON-FRI -m 3", expected)
        assertLogCommand("--author John --week-day MON-FRI --depth 3", expected)
    }

    @Test
    fun gitLogWithLotsOfParameters() {
        val expected =
            "git --no-pager log --first-parent main --no-merges --since=\"7 days ago\" --until=\"4 days ago\" --after=\"2021-01-01\" --author=\"John\" --abbrev-commit --oneline --pretty=format:'%Cred%h%Creset - %s %Cgreen(%ad) %C(bold blue)<%an>%Creset' --date='short' --color=always"
        assertLogCommand("-a John -b main -d 7 -u 4 -D short -A 2021-01-01 -B 2021-01-02 -R", expected)
        assertLogCommand(
            "--author John --branch main --days 7 --until 4 --date-format short --after 2021-01-01 --before 2021-01-02 --author-date",
            expected
        )
    }

    @Test
    fun gitLogForGpg() {
        val expected =
            "git --no-pager log --all --no-merges --since=\"yesterday\" --author=\"John\" --abbrev-commit --oneline --pretty=format:'%Cred%h%Creset - %s %Cgreen(%cd) %C(bold blue)<%an>%Creset %C(yellow)gpg: %G?%Creset' --date='relative' --color=always"
        assertLogCommand("-g -a John", expected)
        assertLogCommand("--gpg-signed --author John", expected)
    }

    @Test
    fun findWithNoArguments() {
        val cmd = CliCommand()
        cmd.main(emptyList())
        assertEquals("find . -maxdepth 2 -mindepth 0 -name .git".split(" "), cmd.findCommand())
    }

    @Test
    fun findWithArguments() {
        val cmd = CliCommand()
        cmd.main(listOf("-L", "-m", "2"))
        assertEquals("find . -L -maxdepth 3 -mindepth 0 -name .git".split(" "), cmd.findCommand())
    }

}
