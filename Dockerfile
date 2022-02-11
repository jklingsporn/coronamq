FROM postgres:14.1
COPY core/src/main/resources/00_setup.sql /docker-entrypoint-initdb.d/00_setup.sql
COPY core/src/main/resources/01_setup_user.sql /docker-entrypoint-initdb.d/01_setup_user.sql
#RUN chown postgres:postgres /docker-entrypoint-initdb.d
#CMD ["docker-entrypoint.sh", "postgres"]
