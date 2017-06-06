#!/bin/bash
#
# Copyright 2017 Okta
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


#Using xmllint is faster than invoking maven
export ARTIFACT_VERSION="$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)"
export IS_RELEASE="$([ ${ARTIFACT_VERSION/SNAPSHOT} == $ARTIFACT_VERSION ] && [ $TRAVIS_BRANCH == 'master' ] && echo 'true')"
#Install Maven 3.3.9 since Travis uses 3.2 by default
MVN_VERSION=3.3.9
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.zip
unzip -qq apache-maven-${MVN_VERSION}-bin.zip -d ..
rm apache-maven-${MVN_VERSION}-bin.zip
export M2_HOME=$PWD/../apache-maven-${MVN_VERSION}
export PATH=$M2_HOME/bin:$PATH

info "Build configuration:"
echo "Version:             $ARTIFACT_VERSION"
echo "Is release:          ${IS_RELEASE:-false}"

