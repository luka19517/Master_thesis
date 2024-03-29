# Uputstvo za pripremu postgres i hbase okruzenja

Kreiranje kontejnera:

	docker-compose -f docker-compose.yml up --build -d

Za pokretanje psql wrapper-a sa lokalne masine, neophodno je instalirati git bash. Pokrece se sa:

	sh ./psqlWrapper.sh

Komanda koja se unese ce biti izvrsena na docker kontejneru