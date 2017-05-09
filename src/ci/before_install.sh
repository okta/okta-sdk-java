#!/bin/bash

#Using xmllint is faster than invoking maven
export ARTIFACT_VERSION="$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)"
export IS_RELEASE="$([ ${ARTIFACT_VERSION/SNAPSHOT} == $ARTIFACT_VERSION ] && [ $TRAVIS_BRANCH == 'master' ] && echo 'true')"
#Install Maven 3.3.9 since Travis uses 3.2 by default
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9
export PATH=$M2_HOME/bin:$PATH

info "Build configuration:"
echo "Version:             $ARTIFACT_VERSION"
echo "Is release:          ${IS_RELEASE:-false}"

