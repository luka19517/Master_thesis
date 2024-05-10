#!/bin/bash
echo 'PREPARING ENVIRONMENT...';
docker-compose -f docker-compose.yml up --build -d;
#echo 'PREPARING HBASE ENVIRONMENT...';
#$HBASE_HOME/bin/start-hbase.sh;
#java -jar ./hbase_setup_model.jar;



