#!/bin/bash
echo "🚀 Déploiement IoT Hub..."

# 1. Pull le code
git pull

# 2. Compile
echo "📦 Compilation..."
mvn clean package -DskipTests

# 3. Rebuild et relance Docker
echo "🐳 Redémarrage Docker..."
docker compose down
docker compose build --no-cache
docker compose up -d

echo "✅ Déploiement terminé !"
echo "📋 Logs :"
docker compose logs --tail=20 iot-hub-api
