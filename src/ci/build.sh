#!/bin/bash

# deploy snapshot from ONLY this branch
SNAPSHOT_BRANCH="new-deal"

# Get the slug from the TRAVIS var, or parse the 'origin' remote
REPO_SLUG=${REPO_SLUG:-${TRAVIS_REPO_SLUG:-$(git remote get-url origin | sed 's_.*\:__; s_.*github.com/__; s_\.git__')}}
PULL_REQUEST=${PULL_REQUEST:-${TRAVIS_PULL_REQUEST:-true}} # default to true
BRANCH=${TRAVIS_BRANCH:-"$(git rev-parse --abbrev-ref HEAD)"}

# run the ITs if we have an orgUrl
if [ ! -z $OKTA_APICLIENT_ORGURL ] ; then
    RUN_ITS=true
fi
RUN_ITS=${RUN_ITS:-false}

# we only deploy from a given branch NOT for pull requests, and ONLY when we can run the ITs
# and do NOT deploy releases, only snapshots right now
if [ "$BRANCH" = "$SNAPSHOT_BRANCH" ] && [ "$PULL_REQUEST" = false ] && [ "$RUN_ITS" = true ] && [ ! "$IS_RELEASE" = true ]; then
        DEPLOY=true
fi
DEPLOY=${DEPLOY:-false}

# print the props so it is easier to debug on Travis or locally.
echo "REPO_SLUG: ${REPO_SLUG}"
echo "PULL_REQUEST: ${PULL_REQUEST}"
echo "BRANCH: ${BRANCH}"
echo "IS_RELEASE: ${IS_RELEASE}"
echo "IS_RELEASE: ${IS_RELEASE}"
echo "RUN_ITS: ${RUN_ITS}"

# all the prep is done, lets run the build!
MVN_CMD="mvn -s src/ci/settings.xml"

# run 'mvn deploy' if we can
if [ "$DEPLOY" = true ] ; then
    echo "Deploying SNAPSHOT build"
    $MVN_CMD deploy
else
    # else try to run the ITs if possible (for someone who has push access to the repo
    if [ "$RUN_ITS" = true ] ; then
        echo "Running mvn install"
        $MVN_CMD install
    else
        # fall back to running an install and skip the ITs
        echo "Skipping ITs, likely this build is a pull request from a fork"
        $MVN_CMD install -DskipITs
    fi
fi
