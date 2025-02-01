#!/bin/sh

/entrypoint/mariadb.sh

echo "[Backend] starting up"
cd /api
exec java -jar api*SNAPSHOT.jar --spring.config.additional-location=./overrides.properties
