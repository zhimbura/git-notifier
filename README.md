[![Docker Pulls](https://img.shields.io/docker/pulls/ustinovtikhon/notifier-server.svg)](https://hub.docker.com/r/ustinovtikhon/notifier-server)

# Telegram бот для уведомлений

Перенос проекта с JS версии [gitlab-bot-webhook](https://github.com/Tihon-Ustinov/gitlab-bot-webhook) на Kotlin

## Суть

Суть бота заключается в том чтобы он мог в телеграммах уведомлять о разных событиях в проекте.

## Функции
- [x] Связать ник телеграм с ником git
- [x] Возможность указывать разные GitLab репозитории
- [x] Добавлять проекты в чаты

### GitLab
- [x] События GitLab Pipeline
- [x] Событие MergeRequest

### GitHub
- [ ] События GitHub Action
- [ ] События PullRequest

## Запуск

### Локально / Docker
- Создать базу YDB (Yandex Cloud или локальную) и выполнить схему `database/ydb_schema.yql`
- Добавить переменные окружения (см. `.env.example`):
  - `YDB_CONNECTION_STRING` — строка подключения к YDB (например `grpc://localhost:2136/local` или `grpcs://ydb.serverless.yandexcloud.net:2135/ru-central1/...`)
  - `APP_HOST`, `APP_PORT` — хост и порт сервера
  - `TG_BOT_TOKEN` — токен Telegram-бота
  - `TG_ADMIN_USERNAME` — username администратора
- Запустить server как обычный Ktor-проект или через Docker

### Yandex Serverless Containers
См. раздел «Деплой» в репозитории и скрипт `deploy/serverless.sh` (если есть).
