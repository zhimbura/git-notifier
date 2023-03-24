
# База данных

Для работы данного проекта используется база данных PostgreSQL

В файле `./dump.sql` расположен бекап базы

Если база данных у вас в докере то можете импортировать ее следующей командой 

```bash
docker exec -i container_name psql --username pg_username [--password pg_password] db_name <dump.sql --set ON_ERROR_STOP=on 
```

[Подробнее про импорт экспорт](https://muxtarovich.medium.com/backup-and-restore-postgresql-docker-4c2a68c98d22)