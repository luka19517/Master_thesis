#!/bin/bash
echo 'PREPARING POSTGRES ENVIRONMENG...';
docker-compose -f docker-compose.yml up --build -d;
echo 'PREPARING HBASE ENVIRONMENT...';
#$HBASE_HOME/bin/start-hbase.sh;
#java -jar ./hbase_setup_model.jar;



