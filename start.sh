#!/bin/bash
# file for local development to start the application.
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR/oidc-server
mvn clean package jetty:run -Dspring.profiles.active="local"