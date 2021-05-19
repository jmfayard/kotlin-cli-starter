<!-- 
CUSTOMIZE ME : delete alll the README except the paragraph "Installation"
In the paragraph installation, make sure to customize the URL of installer.sh
-->

A starter project to build command-line tools in Kotlin Multiplatform

Contains a re-implementation of a real world CLI tool: [git-standup](https://github.com/kamranahmedse/git-standup)

<img width="1214" alt="jmfayard_kotlin-cli-starter__Life_is_too_short_for_Bash_programming_and_Telegram_and_GitHub_Desktop" src="https://user-images.githubusercontent.com/459464/117193054-b40e9200-ade2-11eb-86a8-ad65a45a82da.png">

## Installation

You can install using one of the options listed below

| Source | Command |
| --- | --- |
| Node | npm install -g kotlin-cli-starter 
| Installer | `curl -L https://raw.githubusercontent.com/jmfayard/kotlin-cli-starter/main/installer.sh \| sudo sh` |
| Tests | `./gradlew allTests` |
| Kotlin All Platforms | Run `./gradlew allRun` |
| Kotlin JVM | Run `./gradlew run` |
| Kotlin Native | Run `./gradlew install` then `$ git standup` |
| Kotlin Node.JS | Run `./gradlew jsNodeRun` |

## Why?

Being able to write your own command-line tools is a great skill to have. Automate all the things!

You can write the CLI tools in Kotlin and reap the benefits of using
- a modern programming language
- modern IDE support
- modern practices such as unit testing and continuous integration
- leverage Kotlin multiplatform libraries
- run your code on the JVM and benefit from a wealth of Java libraries
- or build a native executable, which starts very fast and can be deployed on a computer without the JVM

My strong opinion - weakly held - is that there are only two good kind of Bash scripts:

- the ones that are five lines long or less
- the ones that are written and maintained by others

## Work in progress

We want to support Windows, publish on Homebrew and simplify support of shell completion. 

Look at the issues https://github.com/jmfayard/kotlin-cli-starter/issues


## What the template contains

The template

- can be run
  - with Kotlin/Native via `$ ./gradlew install` and then `git-standup`
  - on the JVM with `$ ./gradlew run`
  - on Node.JS with `$ ./gradlew jsNodeRun`. The package is published on https://www.npmjs.com/package/kotlin-cli-starter
- has tests that can also be run both
  - natively `$ ./gradlew nativeTest`
  - on the JVM `$ ./gradlew desktopTest`
  - on Node.js `$ ./gradlew jsTest`
- has continuous integration powered by GitHub actions. The code and the tests are run both on native and on the JVM, both on Ubuntu and macOS. See [.github/workflows/runOnGitHub.yml](https://github.com/jmfayard/kotlin-cli-starter/blob/main/.github/workflows/runOnGitHub.yml)  
- includes those libraries
  - [ktor-client](https://ktor.io/docs/getting-started-ktor-client.html) to make HTTP calls - _Note: only in the branch ktor-client_ See https://github.com/jmfayard/kotlin-cli-starter/issues/15
  - [Okio multiplatform](https://square.github.io/okio/multiplatform/) allows reading and writing files  
  - [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) for multi-platform testing
  - [CliKt](https://github.com/ajalt/clikt) which parses the command-line arguments in a typesafe way and automatically generates the help and Bash/Zsh/Fish auto-completion
  - [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) which provides Kotlin multiplatform / multi-format serialization
  - [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
  - [gradle refreshVersions](https://jmfayard.github.io/refreshVersions/) to simplify dependency management
- contains a work-around of the [missing `runBlocking { ... }` in commonTests](https://github.com/jmfayard/kotlin-cli-starter/issues/9)
- contains an API to work with Files and execute shell subcommands.

<img width="1631" alt="kotlin-cli-starter_–_nativeMain_…_Files_kt__kotlin-cli-starter_nativeMain__and_GitHub_Desktop_and_kamranahmedse_git-standup__Recall_what_you_did_on_the_last_working_day__Psst__or_be_nosy_and_find_what_someone_else_in_your_team_did__-_" src="https://user-images.githubusercontent.com/459464/117189924-0baafe80-addf-11eb-84d9-c1e52f3f704d.png">
  
## The template reimplements `git-standup`

The template reimplements [`git-standup`](https://github.com/kamranahmedse/git-standup) so that you can learn and find inspiration from a real world example.

Simply run it in and it will give you the output from the last working day

Open a directory having multiple repositories and run

```shell
$ ./gradlew install
$ git standup
```

![git standup](http://i.imgur.com/4xmkA49.gif)

This will show you all your commits since the last working day in all the repositories inside.

There is auto-completion to see the options

<img width="1033" alt="jmfayard_jmfayard____Downloads_and_kotlin-cli-starter_–_README_md__kotlin-cli-starter__and_README_·_Issue__7_·_jmfayard_kotlin-cli-starter" src="https://user-images.githubusercontent.com/459464/117405520-11eac900-af0c-11eb-85e9-8c6292d3b246.png">


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
  [... and much more options....]

Examples: git-standup -a "John Doe" -w "MON-FRI" -m 3
```

## CUSTOMIZE_ME

So are you ready to write your own command-line tool?

Have you an idea of what you want to write yet?

You can find some inspiration in [15 Command Line Tools which Spark Joy in Your Terminal ](https://dev.to/jmfayard/15-command-line-tools-which-spark-joy-in-your-terminal-45ln)

Then click on [Use this GitHub template](https://github.com/jmfayard/kotlin-cli-starter/generate)

<img width="1214"  src="https://user-images.githubusercontent.com/459464/117193054-b40e9200-ade2-11eb-86a8-ad65a45a82da.png">

There are comments starting with [**CUSTOMIZE_ME**](https://github.com/jmfayard/kotlin-cli-starter/search?q=CUSTOMIZE_ME) in all places you need to customize

Find them with `Edit > File > Find in Files`

<img width="998" src="https://user-images.githubusercontent.com/459464/117405450-f253a080-af0b-11eb-882c-7bae969bc7e2.png">

## Built with kotlin-cli-starter

- [git-standup](https://github.com/jmfayard/kotlin-cli-starter)
- [httpie.kt](https://github.com/raychenon/httpie.kt)
- ...

_Have you used the template to build something? Please advertise it here_ 🙏🏻 