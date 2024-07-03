#!/bin/bash
echo 'PREPARING ENVIRONMENT...';
rm -f ./olap_benchmark_postgres.jar
rm -f ./olap_benchmark_hbase.jar

export JAVA_HOME="$JAVA_8";
mvn -f ../../benchmarking/OLAP/olap_benchmark_hbase clean compile assembly:single;
mvn -f ./hbase_model_utilities/hbase_setup_olap_model clean compile assembly:single;

export JAVA_HOME="$JAVA_17";
mvn -f ../../benchmarking/OLAP/olap_benchmark_postgres clean compile assembly:single;

cp ../../benchmarking/OLAP/olap_benchmark_postgres/target/olap_benchmark_postgres-1.0-SNAPSHOT-jar-with-dependencies.jar ./olap_benchmark_postgres.jar;
cp ./hbase_model_utilities/hbase_setup_olap_model/target/hbase_setup_olap_model-1.0-SNAPSHOT-jar-with-dependencies.jar ./hbase_setup_olap_model.jar
cp ../../benchmarking/OLAP/olap_benchmark_hbase/target/olap_benchmark_hbase-1.0-SNAPSHOT-jar-with-dependencies.jar ./olap_benchmark_hbase.jar;

docker-compose -f docker-compose.yml up --build -d;
#docker exec -it setup-hbase-master-1 sh -c "java -jar hbase_setup_model.jar";



