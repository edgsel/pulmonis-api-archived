#!/usr/bin/env bash

echo_info() {
  printf "[ \033[1;34m..\033[0m ] $1\n"
}

echo_error() {
  printf "[ \033[1;31m..\033[0m ] $1\n"
}

export $(cat ./variables.env | xargs)

if [ $# -eq 0 ]; then
  echo_error "No arguments supplied!\n"

  echo_info "Supported arguments:"

  echo_info "-----"
  echo_info "start - To start Pulmonis API"

  echo_info "-----"
  echo_info "migrate - Run migrations"

  exit 1
fi

if [ $1 = "start" ]; then
  echo_info "Starting Pulmonis API"
  export ENV_DEBUG_MODE=true
  mvn clean install
  mvn spring-boot:run
elif [ $1 = "migrate" ]; then
  ./migrate.sh
else
  echo_error "Unknown command"
fi
