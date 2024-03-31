#!/bin/bash
echo 'HBASE STARTING...'
$HBASE_HOME/bin/start-hbase.sh;
java -jar ./hbase_setup_model.jar;
echo 'POSTGRES STARTING...'
docker-compose -f docker-compose.yml up --build -d
