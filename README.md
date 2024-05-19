# Master_thesis
Use case analysis for relational and column oriented non relational databases

# Running benchmarks

For running benchmarks it is necessary to have installed maven and java and Docker Desktop client
After running ./prepareEnv.sh (read /setup/README.md) wait for all of the containers to be started.

## Running hbase benchmark

After all of the containers are started go to hbase-master container shell and run 

    java -jar hbase_setup_model.jar

After model is set run:

    java -jar benchmar_hbase.jar

## Running postgres benchmark

After all of the containers are started go to postgres container shell and run 

    java -jar benchmark_postgres.jar


