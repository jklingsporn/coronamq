FROM postgres
COPY core/src/main/resources/00_setup.sql /docker-entrypoint-initdb.d/
COPY core/src/main/resources/01_setup_user.sql /docker-entrypoint-initdb.d/
