#!/bin/sh

echo "=== Railway/Render Entrypoint ==="

# Determine the source URL (Railway uses DATABASE_URL, Render uses SPRING_DATASOURCE_URL)
if [ -n "$DATABASE_URL" ]; then
  SOURCE_URL="$DATABASE_URL"
  echo "Detected Railway environment (DATABASE_URL)"
elif [ -n "$SPRING_DATASOURCE_URL" ]; then
  SOURCE_URL="$SPRING_DATASOURCE_URL"
  echo "Detected Render environment (SPRING_DATASOURCE_URL)"
else
  echo "No database URL found, using individual DB_* variables"
  SOURCE_URL=""
fi

# Parse database URL if available and individual vars not set
if [ -n "$SOURCE_URL" ]; then
  # Expected format: postgresql://user:pass@hostname:port/dbname
  
  # Extract username if not set
  if [ -z "$DB_USERNAME" ]; then
    # Strip protocol, get user:pass@rest, extract user
    tmp="${SOURCE_URL#*://}"
    tmp="${tmp%%@*}"
    DB_USERNAME="${tmp%%:*}"
    echo "Extracted DB_USERNAME: $DB_USERNAME"
    export DB_USERNAME
  fi

  # Extract password if not set
  if [ -z "$DB_PASSWORD" ]; then
    tmp="${SOURCE_URL#*://}"
    tmp="${tmp%%@*}"
    DB_PASSWORD="${tmp#*:}"
    echo "Extracted DB_PASSWORD: [HIDDEN]"
    export DB_PASSWORD
  fi

  # Extract host if not set
  if [ -z "$DB_HOST" ]; then
    tmp="${SOURCE_URL#*://}"
    tmp="${tmp##*@}"
    DB_HOST="${tmp%%[:/]*}"
    echo "Extracted DB_HOST: $DB_HOST"
    export DB_HOST
  fi

  # Extract port if not set
  if [ -z "$DB_PORT" ]; then
    tmp="${SOURCE_URL#*://}"
    tmp="${tmp##*@}"
    if echo "$tmp" | grep -q ":"; then
      tmp="${tmp#*:}"
      DB_PORT="${tmp%%/*}"
    else
      DB_PORT="5432"
    fi
    echo "Extracted DB_PORT: $DB_PORT"
    export DB_PORT
  fi

  # Extract database name if not set
  if [ -z "$DB_NAME" ]; then
    DB_NAME="${SOURCE_URL##*/}"
    # Remove any query parameters
    DB_NAME="${DB_NAME%%\?*}"
    echo "Extracted DB_NAME: $DB_NAME"
    export DB_NAME
  fi
fi

# Ensure Port has a default
if [ -z "$DB_PORT" ]; then
  export DB_PORT=5432
fi

# Clean up platform-injected variables to avoid conflicts
echo "Unsetting platform-injected database URLs..."
unset SPRING_DATASOURCE_URL
unset DATABASE_URL

echo "=== Starting Application ==="
echo "DB_HOST: $DB_HOST"
echo "DB_PORT: $DB_PORT"
echo "DB_NAME: $DB_NAME"
echo "DB_USERNAME: $DB_USERNAME"

exec java -jar app.jar
