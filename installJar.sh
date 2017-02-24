#!/bin/bash

mvn install:install-file -Dfile=lib/commons-configuration2-2.0-beta2.jar \
    -DgroupId=org.apache.commons -DartifactId=commons-configuration2 \
    -Dversion=2.0-beta2 -Dpackaging=jar

mvn install:install-file -Dfile=lib/phidget21.jar \
    -DgroupId=com.phidgets -DartifactId=phidget \
    -Dversion=2.1 -Dpackaging=jar
