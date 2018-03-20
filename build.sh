#!/bin/bash

mvn compile assembly:single
cp target/therapy-pi-1.0-SNAPSHOT-jar-with-dependencies.jar therapypi.jar

sudo rm -r  ~/.cache/Chromium/Default/Cache/f_*
sudo rm -r  ~/.cache/Chromium/Default/Media\ Cache/f_*
