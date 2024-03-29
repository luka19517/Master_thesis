SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;


CREATE SCHEMA postgresdb;


ALTER SCHEMA postgresdb OWNER TO pg_database_owner;


COMMENT ON SCHEMA postgresdb IS 'standard postgresdb schema';

SET default_tablespace = '';

SET default_table_access_method = heap;


CREATE TABLE postgresdb.FXUSER (
	USER_ID SERIAL PRIMARY KEY,
	USERNAME VARCHAR (50) UNIQUE NOT NULL,
	PASSWORD VARCHAR (50) NOT NULL,
	EMAIL VARCHAR (255) UNIQUE NOT NULL,
	CREATED_ON TIMESTAMP NOT NULL,
	LAST_LOGIN TIMESTAMP
);
