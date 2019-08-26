#!/bin/sh

cd appserver

mvn package assembly:single
$JAVA_HOME/bin/java -jar ./target/oracle-code-day-demo-appserver-1.0-SNAPSHOT-jar-with-dependencies.jar $@
