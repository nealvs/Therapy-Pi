#!/bin/bash

mvn install:install-file -Dfile=lib/commons-configuration2-2.0-beta2.jar \
    -DgroupId=org.apache.commons -DartifactId=commons-configuration2 \
    -Dversion=2.0-beta2 -Dpackaging=jar


