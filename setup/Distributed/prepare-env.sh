#!/bin/bash
docker-compose -f docker-compose.yml up --build -d;
#docker exec -it setup-hbase-master-1 sh -c "java -jar hbase_setup_model.jar";



