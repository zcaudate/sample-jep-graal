#!/bin/bash

echo "BUILDING JAR"
lein uberjar

echo "INSTRUMENTING JAR"
java -agentlib:native-image-agent=config-output-dir=log -jar target/jep.graal-0.1.0-SNAPSHOT-standalone.jar

echo "INSTRUMENTING COMPLETED"

echo "COPYING CONFIGS from ./log to ./native"
cp log/jni-config.json native
cp log/reflect-config.json native

echo "PREIMAGE SUCCESS"