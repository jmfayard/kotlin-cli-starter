You should use Kotlin - or your favorite language - to script and automate all the things.

Not Bash.

Modern programming language are an order of magnitude better than Bash.

There are only two good kind of Bash scripts:
- the ones that are five lines long or less
- the ones that are written and maintained by others

This repo is two things:

- a reimplementation in Kotlin Multi-platform of a real world CLI tool: [git-standup](https://github.com/kamranahmedse/git-standup)
- a GitHub template to give you the inspiration to write your own command line tools in Kotlin and to get you started faster

<img width="1214" alt="jmfayard_kotlin-cli-starter__Life_is_too_short_for_Bash_programming_and_Telegram_and_GitHub_Desktop" src="https://user-images.githubusercontent.com/459464/117193054-b40e9200-ade2-11eb-86a8-ad65a45a82da.png">

## Work in progress

Look at the issues https://github.com/jmfayard/kotlin-cli-starter/issues

## Install

You can install using one of the options listed below

| Source | Command |
| --- | --- |
| curl | `curl -L https://raw.githubusercontent.com/jmfayard/kotlin-cli-starter/main/installer.sh \| sudo sh` |
| Kotlin Native | Clone and run `./gradlew install` |
| Kotlin JVM | Clone and run `./gradlew install` |

## What the template contains

The template

- can be run both
  - with Kotlin/Native via `$ ./gradlew install` and then `git-standup`
  - on the JVM with `$ ./gradlew run`
- has tests that can also be run both
  - natively `$ ./gradlew nativeTest`
  - on the JVM `$ ./gradlew desktopTest`
- has continuous integration powered by GitHub actions. The code and the tests are run both on native and on the JVM, both on Ubuntu and macOS. See [.github/workflows/runOnGitHub.yml](https://github.com/jmfayard/kotlin-cli-starter/blob/main/.github/workflows/runOnGitHub.yml)  
- includes those libraries
  - [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) for multi-platform testing
  - [CliKt](https://github.com/ajalt/clikt) which parses the command-line arguments in a typesafe way and automatically generates the help and Bash/Zsh/Fish auto-completion
  - [ktor-client](https://ktor.io/docs/getting-started-ktor-client.html) to make HTTP calls
  - [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) which provides Kotlin multiplatform / multi-format serialization
  - [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
  - [gradle refreshVersions](https://jmfayard.github.io/refreshVersions/) to simplify dependency management
- contains an API to work with Files and execute shell subcommands.

<img width="1631" alt="kotlin-cli-starter_–_nativeMain_…_Files_kt__kotlin-cli-starter_nativeMain__and_GitHub_Desktop_and_kamranahmedse_git-standup__Recall_what_you_did_on_the_last_working_day__Psst__or_be_nosy_and_find_what_someone_else_in_your_team_did__-_" src="https://user-images.githubusercontent.com/459464/117189924-0baafe80-addf-11eb-84d9-c1e52f3f704d.png">
  
## The template reimplements `git-standup`

The template reimplement [`git-standup`](https://github.com/kamranahmedse/git-standup) so that you can learn and find inspiration from a real world example.

Simply run it in and it will give you the output from the last working day

Open a directory having multiple repositories and run

```shell
$ ./gradlew install
$ git standup
```

![git standup](http://i.imgur.com/4xmkA49.gif)

This will show you all your commits since the last working day in all the repositories inside.

Options:

```shell
git standup --help
Usage: git-standup [OPTIONS]

  Recall what you did on the last working day ..or be nosy and find what
  someone else did.

Options:
  --generate-completion [bash|zsh|fish]
  -a, --author TEXT                Specify author to restrict search to
  -b, --branch TEXT                Specify branch to restrict search to
                                   (unset: all branches, "$remote/$branch" to
                                   include fetches)
  -w, --week-day TEXT              Specify weekday range to limit search to
  -m, --depth INT                  Specify the depth of recursive directory
                                   search
  -F, --force-recursion TEXT       Force recursion up to speficied depth
  -L, --symbolic-links             Toggle inclusion of symbolic links in
                                   recursive directory search
  -d, --days INT                   Specify the number of days back to include
  -u, --until INT                  Specify the number of days back until this
                                   day
  -D, --date-format TEXT           Specify the number of days back until this
                                   day
  -h, --help                       Display this help screen
  -g, --gpg-signed / disabled      Show if commit is GPG signed (G) or not (N)
  -f, --fetch / --no-fetch         Fetch the latest commits beforehand
  -s, --silence                    Silences the no activity message (useful
                                   when running in a directory having many
                                   repositories)
  -r, --report                     Generate a file with the report
  -c, --diff-stat TEXT             Show diffstat for every matched commit
  -A, --after TEXT                 List commits after this date
  -B, --before TEXT                List commits before this date
  -R, --author-date                Display the author date instead of the
                                   committer date
  --verbose                        verbose

Repositories will be searched in the current directory unless a file
`.git-standup-whitelist` is found that contains repository paths.

Examples: git-standup -a "John Doe" -w "MON-FRI" -m 3
```

