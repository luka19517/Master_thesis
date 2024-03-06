FROM postgres:latest
COPY ./postgres_model.sql /docker-entrypoint-initdb.d/create_script.sql
ENV POSTGRES_PASSWORD=postgres
ENV	POSTGRES_USER=postgres
ENV POSTGRES_DB=postgresdb