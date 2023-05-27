#Uputstvo za pokretanje postgres servera
Create image:

	docker image build -t postgresdb:latest .

Run container:

	docker container run --name postgresdb -d postgresdb

Run psql inside container:

	docker exec -it postgresdb psql -U postgres -W postgresdb

Set search path:

	SET search_path TO postgresdb;

Turn on timing:
	
	\timing
