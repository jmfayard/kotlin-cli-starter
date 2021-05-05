
## Install

You can install `git-standup` using one of the options listed below

| Source | Command |
| --- | --- |
| curl | `curl -L https://raw.githubusercontent.com/jmfayard/kotlin-cli-starter/master/installer.sh \| sudo sh` |
| manual | Clone and run `./gradlew install` |
| brew | TODO: `brew install kotlin-cli-starter` |

## Usage

Simply run it in and it will give you the output from the last working day

Open a directory having multiple repositories and run

```shell
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


