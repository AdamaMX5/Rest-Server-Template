#!/bin/bash

BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"

echo "running from $BASE_DIR"

(
echo "Generating Java stubs"
cd $BASE_DIR/api
rm -rf build/generated-sources/openapi
./gradlew openApiGenerate
)

(
echo "Generating TypeScript stubs"
cd $BASE_DIR/ui/flussmark
npm run api-gen
)
