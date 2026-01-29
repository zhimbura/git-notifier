#!/usr/bin/env bash
# Деплой git-notifier в Yandex Serverless Containers (минимальные лимиты, amd64)
# Требует: yc CLI, Docker, переменные TG_API_TOKEN, YDB_CONNECTION_STRING, TG_ADMIN_USERNAME

set -e
REGISTRY_ID=${REGISTRY_ID:-crpXXXXXXXX}   # ID Container Registry
CONTAINER_NAME=${CONTAINER_NAME:-git-notifier}
IMAGE_TAG=${IMAGE_TAG:-latest}
PLATFORM=linux/amd64

# Сборка образа с указанием архитектуры
echo "Building image for $PLATFORM..."
docker build --platform "$PLATFORM" -t "cr.yandex/$REGISTRY_ID/$CONTAINER_NAME:$IMAGE_TAG" -f server/Dockerfile server/

# Пуш в Registry
echo "Pushing to Container Registry..."
docker push "cr.yandex/$REGISTRY_ID/$CONTAINER_NAME:$IMAGE_TAG"

# Создание/обновление контейнера с минимальными лимитами
echo "Creating/updating serverless container..."
yc serverless container create --name "$CONTAINER_NAME" 2>/dev/null || true

# Создать контейнер, если ещё нет: yc serverless container create --name "$CONTAINER_NAME"
yc serverless container revision deploy \
  --container-name "$CONTAINER_NAME" \
  --image "cr.yandex/$REGISTRY_ID/$CONTAINER_NAME:$IMAGE_TAG" \
  --memory 256MB \
  --cores 1 \
  --concurrency 1 \
  --runtime http \
  --environment "APP_PORT=8080" \
  --environment "APP_HOST=0.0.0.0" \
  --environment "YDB_CONNECTION_STRING=${YDB_CONNECTION_STRING}" \
  --environment "TG_API_TOKEN=${TG_API_TOKEN}" \
  --environment "TG_ADMIN_USERNAME=${TG_ADMIN_USERNAME:-zhimbura}" \
  --min-instances 0

echo "Done. Перед запуском задайте: REGISTRY_ID, YDB_CONNECTION_STRING, TG_API_TOKEN, TG_ADMIN_USERNAME (опционально SA_ID)."
