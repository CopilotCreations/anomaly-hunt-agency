#!/bin/sh

# Gradle wrapper script for Unix systems
# This script will download and run Gradle

# Determine the project directory
DIRNAME=dirname ""
APP_HOME=cd "" && pwd

# Set defaults
APP_NAME="Gradle"
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Check for Java
if [ -n "" ] ; then
    JAVACMD="/bin/java"
else
    JAVACMD="java"
fi

# Get Gradle wrapper jar
WRAPPER_JAR="/gradle/wrapper/gradle-wrapper.jar"

# Run Gradle
exec ""  -jar "" "$@"