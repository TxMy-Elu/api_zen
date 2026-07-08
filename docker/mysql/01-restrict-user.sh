#!/bin/bash
# Restreint les droits DDL de l'utilisateur applicatif en production.
# ddl-auto=validate (prod) n'a besoin que de SELECT/INSERT/UPDATE/DELETE.
# Ce script est monté via docker-compose.prod.yml et ne s'exécute qu'au
# premier démarrage (quand le volume db-data est vide).

set -e

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    REVOKE CREATE, ALTER, DROP, INDEX, REFERENCES
        ON \`${MYSQL_DATABASE}\`.*
        FROM '${MYSQL_USER}'@'%';
    FLUSH PRIVILEGES;
EOSQL
