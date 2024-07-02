#!/bin/bash
echo 'PREPARING ENVIRONMENT...';
rm -f ./olap_benchmark_postgres.jar

export JAVA_HOME="$JAVA_17";
mvn -f ../../benchmarking/OLAP/olap_benchmark_postgres clean compile assembly:single;

cp ../../benchmarking/OLAP/olap_benchmark_postgres/target/olap_benchmark_postgres-1.0-SNAPSHOT-jar-with-dependencies.jar ./olap_benchmark_postgres.jar;

docker-compose -f docker-compose.yml up --build -d;
#docker exec -it setup-hbase-master-1 sh -c "java -jar hbase_setup_model.jar";



