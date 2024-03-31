#!/bin/bash
echo 'HBASE STOPPING...'
java -jar ./hbase_clean_model.jar;
$HBASE_HOME/bin/stop-hbase.sh
