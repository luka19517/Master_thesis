# Uputstvo za pripremu postgres okruženja

Kreiranje slike:

	docker image build -t postgresdb:latest .

Pokretanje kontejnera:

	docker container run --name postgresdb -d postgresdb

Pokretanje psql programa unutar kontejnera:

	docker exec -it postgresdb psql -U postgres -W postgresdb

Podešavanje psql putanje:

	SET search_path TO postgresdb;

Uključivanje timing opcije za merenje vremena izvršavanja:
	
	\timing

Isključivanje pager opcije za prikaz rezultata:
	
	\pset pager off
