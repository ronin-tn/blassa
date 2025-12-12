#!/bin/sh
# Render injects a SPRING_DATASOURCE_URL that is incompatible with the JDBC driver
# We unset it here so that Spring Boot falls back to the configuration in application.yaml,
# which correctly constructs the JDBC URL from DB_HOST, DB_PORT, etc.

echo "Unsetting Render-injected SPRING_DATASOURCE_URL and DATABASE_URL..."
unset SPRING_DATASOURCE_URL
unset DATABASE_URL

echo "Starting application with forced JDBC URL..."
exec java -jar app.jar \
  --spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?stringtype=unspecified
