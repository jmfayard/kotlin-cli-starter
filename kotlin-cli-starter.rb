# CUSTOMIZE_ME : formula to be included on Homebrew
# Note that there are restrictions on who can be on homebrew
# For example  your repo needs 75 stars and no less than one month old
# You can create your own tap if that's not an option https://docs.brew.sh/Taps
class KotlinCliStarter < Formula
  desc "Git extension to generate reports for standup - in Kotlin Multiplatform"
  homepage "https://github.com/jmfayard/kotlin-cli-starter"
  url "https://github.com/jmfayard/kotlin-cli-starter/archive/refs/tags/v0.1.tar.gz"
  sha256 "358c6c7cd2b3b752a84be972202645cbf9b12fc34ce18aaedf3563b7b005669c"
  license "MIT"

  depends_on "gradle" => :build
  depends_on "openjdk" => :build

  conflicts_with "git-standup", because: "it's a reimplementation of this tool"

  def install
    system "git", "init", "."
    system "git", "config", "--global", "user.name", "Git User"
    system "./gradlew", "nativeTest", "linkReleaseExecutableNative"
    bash_completion.install "dist/git-standup.kt.bash" => "git-standup"
    fish_completion.install "dist/git-standup.kt.fish"
    zsh_completion.install "dist/git-standup.kt.zsh" => "_git-standup"
    bin.install "build/bin/native/releaseExecutable/kotlin-cli-starter.kexe" => "git-standup"
  end

  test do
    system "git", "config", "--global", "user.name", "Git User"
    system "#{bin}/git-standup", "--help"
  end
end
