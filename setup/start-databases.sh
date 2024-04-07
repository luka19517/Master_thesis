#!/bin/bash
echo 'PREPARING POSTGRES ENVIRONMENG...';
docker-compose -f docker-compose.yml up --build -d;
echo 'PREPARING HBASE ENVIRONMENT...';



