#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" == "${DEPLOY_BRANCH}" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    echo "Starting deployment."
    GIT_HASH=`git rev-parse --short HEAD`
    ./gradlew clean assembleRelease
    curl -F "file=@filltheform/build/outputs/apk/filltheform-release.apk" -F "token=${DEPLOY_GATE_API_KEY}" -F "message=This is '${GIT_HASH}' from '${DEPLOY_BRANCH}' branch of ${FILL_THE_FORM_URL}" -F "distribution_key=${FILL_THE_FORM_DISTRIBUTION_KEY}" -F "release_note=You are looking at '${GIT_HASH}' from '${DEPLOY_BRANCH}' branch of ${FILL_THE_FORM_URL} To quickly try out or test FillTheForm you can get FillTheFormSample app here ${FILL_THE_FORM_SAMPLE_URL}" https://deploygate.com/api/users/ivan9jukic/apps
else
    echo "Deployment is enabled only for ${DEPLOY_BRANCH} branch."
fi