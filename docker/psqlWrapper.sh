#!/bin/bash

echo '----Docker psql wrapper----'
echo 'Enter psql command'
read command
docker exec postgres psql -U postgres -d postgresdb -c "$command"