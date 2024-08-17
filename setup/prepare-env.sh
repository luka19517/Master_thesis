#!/bin/bash
echo 'PREPARING ENVIRONMENT...';

rm -f ./hbase_setup_model.jar
rm -f ./benchmark_hbase.jar
rm -f ./benchmark_postgres.jar

export JAVA_HOME="$JAVA_8";
mvn -f ./hbase_model_utilities/hbase_setup_model clean compile assembly:single;
mvn -f ../benchmarking/benchmark_hbase clean compile assembly:single;

export JAVA_HOME="$JAVA_17";
mvn -f ../benchmarking/benchmark_postgres clean compile assembly:single;

cp ./hbase_model_utilities/hbase_setup_model/target/hbase_setup_model-1.0-SNAPSHOT-jar-with-dependencies.jar ./hbase_setup_model-1.0-SNAPSHOT-jar-with-dependencies.jar;
cp ../benchmarking/benchmark_hbase/target/benchmark_hbase-1.0-SNAPSHOT-jar-with-dependencies.jar ./benchmark_hbase-1.0-SNAPSHOT-jar-with-dependencies.jar;
cp ../benchmarking/benchmark_postgres/target/benchmark_postgres-1.0-SNAPSHOT-jar-with-dependencies.jar ./benchmark_postgres-1.0-SNAPSHOT-jar-with-dependencies.jar;

mv ./hbase_setup_model-1.0-SNAPSHOT-jar-with-dependencies.jar hbase_setup_model.jar;
mv ./benchmark_hbase-1.0-SNAPSHOT-jar-with-dependencies.jar benchmark_hbase.jar;
mv ./benchmark_postgres-1.0-SNAPSHOT-jar-with-dependencies.jar benchmark_postgres.jar;


docker-compose -f docker-compose.yml up --build -d;
winpty docker exec -it hbase sh -c "java -jar hbase_setup_model.jar";



