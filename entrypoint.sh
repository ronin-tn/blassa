#!/bin/sh

# If DB_HOST is not set, try to extract it from Render's SPRING_DATASOURCE_URL
if [ -z "$DB_HOST" ]; then
  echo "DB_HOST not set. Attempting to extract from SPRING_DATASOURCE_URL..."
  # Expected format: postgresql://user:pass@hostname/dbname or postgresql://user:pass@hostname:port/dbname
  # 1. Strip protocol (postgresql://)
  tmp="${SPRING_DATASOURCE_URL#*://}"
  # 2. Strip credentials (upto @) - extract everything after the last @
  tmp="${tmp##*@}"
  # 3. Extract host (everything before the first slash or colon)
  DB_HOST="${tmp%%[:/]*}"
  
  echo "Extracted DB_HOST: $DB_HOST"
  export DB_HOST
fi

if [ -z "$DB_NAME" ]; then
  # Extract DB name (everything after the last slash)
  DB_NAME="${SPRING_DATASOURCE_URL##*/}"
  echo "Extracted DB_NAME: $DB_NAME"
  export DB_NAME
fi

# Ensure Port is set
if [ -z "$DB_PORT" ]; then
  export DB_PORT=5432
fi

echo "Unsetting Render-injected SPRING_DATASOURCE_URL and DATABASE_URL..."
unset SPRING_DATASOURCE_URL
unset DATABASE_URL

echo "Starting application..."
exec java -jar app.jar
