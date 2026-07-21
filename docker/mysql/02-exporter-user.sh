#!/bin/bash
# Crée le compte MySQL dédié à la supervision, au strict nécessaire.
#
# mysqld-exporter n'a besoin que de lire des compteurs globaux : il n'accède
# à aucune donnée applicative. Le faire tourner sous root lui donnerait DROP
# sur l'ensemble des bases pour publier un QPS — sans commune mesure.
#
# Droits accordés :
#   PROCESS             → SHOW GLOBAL STATUS / SHOW PROCESSLIST
#   REPLICATION CLIENT  → SHOW BINARY LOG STATUS
#   SELECT on performance_schema.* → collecteurs détaillés
# Aucun droit sur la base applicative.
#
# Monté par docker-compose.monitoring.yml, ce script ne s'exécute qu'au premier
# démarrage du conteneur, quand le volume db-data est vide. Sur une base déjà
# initialisée, exécuter le même SQL manuellement une fois.

set -e

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    CREATE USER IF NOT EXISTS 'exporter'@'%'
        IDENTIFIED BY '${MYSQL_EXPORTER_PASSWORD}'
        WITH MAX_USER_CONNECTIONS 3;

    GRANT PROCESS, REPLICATION CLIENT ON *.* TO 'exporter'@'%';
    GRANT SELECT ON \`performance_schema\`.* TO 'exporter'@'%';

    FLUSH PRIVILEGES;
EOSQL
