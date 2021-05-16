## See https://github.com/casey/just
## Install with $ brew install just
run:
    ./gradlew allTests allRun install
    cd .. && git-standup
ci:
    ./gradlew runOnGitHub
github:
    open https://github.com/jmfayard/kotlin-cli-starter/
issues:
    open https://github.com/jmfayard/kotlin-cli-starter/issues
prs:
    open https://github.com/jmfayard/kotlin-cli-starter/pulls
urls: github issues prs
    echo "URLs opened"
install:
    ./gradlew install
completions:
    ./gradlew completions
brew:
    brew reinstall --debug --verbose --build-from-source git-standup-kotlin
    brew test git-standup-kotlin
    brew audit --strict git-standup-kotlin
