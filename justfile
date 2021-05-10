## See https://github.com/casey/just
## Install with $ brew install just
run:
    ./gradlew allTests run install
    cd .. && git-standup
github:
    open https://github.com/jmfayard/kotlin-cli-starter/
issues:
    open https://github.com/jmfayard/kotlin-cli-starter/issues
prs:
    open https://github.com/jmfayard/kotlin-cli-starter/pulls
urls: github issues prs
    echo "URLs opened"
package:
    ./gradlew linkReleaseExecutableNative
    cp build/bin/native/releaseExecutable/kotlin-cli-starter.kexe dist/git-standup
    dist/git-standup --generate-completion bash > dist/git-standup.kt.bash
    dist/git-standup --generate-completion zsh > dist/git-standup.kt.zsh
    dist/git-standup --generate-completion fish > dist/git-standup.kt.fish
brew:
    brew reinstall --debug --verbose --build-from-source git-standup.kt
    brew test git-standup.kt
    brew audit --strict git-standup.kt
