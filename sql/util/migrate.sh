#!/usr/bin/env bash

set -eu

URL="${MIGRATE_URL:-default}"
USER="${MIGRATE_USER:-user}"
PASSWORD="${MIGRATE_PASSWORD:-password}"

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ACTION="migrate"
OUT_OF_ORDER="false"
META_TABLE="schema_version"

show_help() {
	echo ""
	echo "Usage:"
	echo "  $0 [options] SCHEMA"
	echo ""
	echo "Options:"
	echo "  --url                the database URL"
	echo "  --user               the user identifier"
	echo "  --pass, --password   the password"
	echo "  --migrations-dir     path to migration files"
	echo "  --meta-table         name of Flyway's metadata table"
	echo "  --out-of-order       allows migrations to be run 'out of order' (matches Flyway -outOfOrder=true behaviour)"
	echo ""
}

for i in "$@"
do
case $i in
	--url=*)
		URL="${i#*=}"
		shift
	;;
	--user=*)
		USER="${i#*=}"
		shift
	;;
	--pass=*|--password=*)
		PASSWORD="${i#*=}"
		shift
	;;
	--action=*)
		ACTION="${i#*=}"
		shift
	;;
	--out-of-order)
		OUT_OF_ORDER="true"
		shift
	;;
	--migrations-dir=*)
		MIGRATIONS_DIR="${i#*=}"
		shift
	;;
	--meta-table=*)
		META_TABLE="${i#*=}"
		shift
	;;
	*)
		SCHEMA="${i}"
	;;
esac
done

if [[ "$SCHEMA" == "" ]]; then
	show_help
	exit 1
fi

if [[ ! -d "${MIGRATIONS_DIR:="sql/$SCHEMA"}" ]]; then
    echo "Schema directory does not exist: $MIGRATIONS_DIR"
	exit 1
fi

$DIR/flyway/flyway \
	-url="$URL" \
	-user="$USER" \
	-password="$PASSWORD" \
	-schemas="$SCHEMA" \
	-outOfOrder="$OUT_OF_ORDER" \
	-locations="filesystem:${MIGRATIONS_DIR}" \
	-table="$META_TABLE" \
	$ACTION
