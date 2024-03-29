#!/bin/bash

echo 'Docker psql wrapper'

read command
docker exec postgres psql -U postgres -d postgresdb -c "$command"
