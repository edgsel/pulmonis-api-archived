#!/usr/bin/env bash

echo_info () {
  printf "[ \033[1;34m..\033[0m ] $1\n"
}

echo_success () {
  printf "\r[ \033[1;32mOK\033[0m ] $1\n"
}

echo_error () {
  printf "\r[ \033[1;31mERR\033[0m ] $1\n"
}

echo_warning () {
  printf "\r[ \033[1;33mWAR\033[0m ] $1\n"
}

echo_fatal () {
  echo_error "$@"
  exit 1
}

set -eu

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

export $(cat ./variables.env | xargs)

DB_HOST=${ENV_DATABASE_HOST}
DB_PORT=${ENV_DATABASE_PORT}
DB_SCHEMA=${ENV_DATABASE_NAME}

DB_USER=${ENV_DATABASE_USERNAME}
DB_PASSWORD=${ENV_DATABASE_PASSWORD}

echo_info "Starting to run migrations"
MIGRATIONS_DIR="sql/migrations"
SCHEMA_VERSION_TABLE="schema_version"

sql/util/migrate.sh \
  --migrations-dir="${MIGRATIONS_DIR}" \
  --url="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_SCHEMA" \
  --user="${DB_USER}" \
  --password="${DB_PASSWORD}" \
  --meta-table="${SCHEMA_VERSION_TABLE}" \
  --action="migrate" \
  "$DB_SCHEMA"

echo_success "Finished running migrations"
