#!/usr/bin/env bash
# Plain-Java checks for the plugin API. Run from anywhere.
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q clean compile
mkdir -p target/test-classes
javac -cp target/classes -d target/test-classes tests/src/*.java
java -cp target/test-classes:target/classes PluginApiTest
