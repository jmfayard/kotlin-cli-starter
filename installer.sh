#!/usr/bin/env bash
## CUSTOMIZE_ME: use your own GitHub username and repository here
USER=jmfayard
REPO=kotlin-cli-starter
## Clone the repo
git clone https://github.com/$USER/$REPO.git --depth=1 || {
  echo >&2 "Clone failed with $?"
  exit 1
}

cd $REPO || exit

./gradlew nativeTest install || {
  echo >&2 "Install failed with $?"
  exit 1
}

cd ..

rm -rf $REPO
