#!/bin/bash
# file for local development to start the application.
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR/oidc-server
export MAVEN_OPTS="-Xmx1024m -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
mvn clean package jetty:run