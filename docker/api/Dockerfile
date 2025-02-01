# Step 1: build jar
###################
FROM gradle:7.6-jdk17-alpine as jar-builder

# Spring Boot app
WORKDIR /api

COPY api/ .
# ensure overrides.yml (from dev) is never deployed
RUN find . -type f -name 'overrides.properties' -delete

# Generate API stubs
RUN rm -rf build/generated-sources
RUN gradle openApiGenerate

# build the app
RUN gradle build -x test --no-daemon

# Step 2: create a lean image only from the jar
###############################################
FROM openjdk:17-jdk-alpine

# MariaDB
RUN apk add --no-cache mariadb mariadb-client mariadb-server-utils pwgen && \
    rm -f /var/cache/apk/*

# Copy jar
WORKDIR /api
COPY --from=jar-builder /api/build/libs/api*.jar ./

# Copy entrypoint scripts
COPY docker/*.sh /entrypoint/

EXPOSE 8080
VOLUME ["/var/lib/mysql"]
VOLUME ["/api/overrides.properties"]

ENTRYPOINT [ "/entrypoint/run.sh" ]
