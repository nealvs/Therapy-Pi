#!/bin/bash

mvn compile assembly:single
cp target/therapy-pi-1.0-SNAPSHOT-jar-with-dependencies.jar therapypi.jar

