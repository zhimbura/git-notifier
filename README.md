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

Для запуска
- Создать базу данных PostgreSQL на основании бекапа `database/dump.sql`
- Добавить переменные окружения указанные в файле `.env`
- Должно заработать 
