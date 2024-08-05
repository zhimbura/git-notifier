# Используем официальный образ PostgreSQL
FROM postgres:latest

# Копируем файл бекапа в контейнер
COPY dump.sql /docker-entrypoint-initdb.d/
