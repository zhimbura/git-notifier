# Используем официальный образ PostgreSQL
FROM postgres:latest

ENV POSTGRES_USER bit_admin
ENV POSTGRES_PASSWORD Password1!
ENV POSTGRES_DB bot_pipeline_notifier_db

# Копируем файл бекапа в контейнер
COPY dump.sql /docker-entrypoint-initdb.d/
