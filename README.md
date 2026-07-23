# 🏠 IoT Hub API

API centralisée pour objets connectés, construite avec Spring Boot et une architecture hexagonale modulaire.

## Architecture

Le projet suit une architecture Monolithe Modulaire Hexagonal — chaque module a un rôle précis, communique via des ports/adaptateurs et des événements Spring.

[Airthings API] ──► module-integrations ──► module-devices ──► module-api ──► [Front]
[Sinopé Zigbee] ──►        │
module-alerts ──► notifications push
module-history
module-auth ──► JWT

### Les modules

| Module | Rôle |
|---|---|
| module-shared | Classes communes (DeviceData, events, enums) |
| module-devices | Gestion et persistance des appareils |
| module-integrations | Connexion aux APIs externes (Airthings OAuth2, Sinopé MQTT) |
| module-api | Exposition REST vers le front |
| module-alerts | Surveillance des seuils, cooldown, notifications push (Web Push/VAPID) |
| module-history | Historique des mesures |
| module-auth | Authentification JWT, gestion des utilisateurs |

## Stack technique

- Java 21 + Spring Boot 3.2
- PostgreSQL — persistance des données
- Flyway — migrations de base de données versionnées
- Spring Security + JWT — authentification stateless
- MapStruct — mapping entre couches
- Lombok — réduction du boilerplate
- OAuth2 — authentification Airthings
- MQTT / Mosquitto — communication avec les thermostats Sinopé
- Zigbee2MQTT — bridge Zigbee vers MQTT
- Web Push (VAPID) — notifications push navigateur et mobile

## Sécurité

- Authentification JWT sur toutes les routes, sauf /api/auth/**
- Mots de passe hashés en BCrypt
- Rate limiting sur le login (5 tentatives/minute par IP, Bucket4j)
- Logs de traçabilité des tentatives de connexion
- Validation des entrées (température limitée entre 7°C et 22°C)
- Backend jamais exposé directement sur internet — accessible uniquement via tunnel Tailscale depuis le VPS de production, pare-feu (ufw) restreignant les ports accessibles même depuis le tailnet
- Aucun secret en dur — tout injecté via variables d'environnement
- CORS restreint à une liste explicite d'origines autorisées

## Infrastructure IoT

- Raspberry Pi 4 — serveur domotique local
- ConBee III — coordinateur Zigbee USB
- Zigbee2MQTT — bridge Zigbee → MQTT
- Mosquitto — broker MQTT (Docker)
- Tailscale — réseau privé (VPN WireGuard) reliant le Pi au VPS de production

## Endpoints REST

POST   /api/auth/login              → Connexion, retourne un token JWT

GET    /api/devices                 → Liste tous les appareils
GET    /api/devices/{id}            → Détail d'un appareil

GET    /api/history/{deviceId}      → Historique des mesures d'un appareil

GET    /api/thermostats/{id}/temperature   → Dernière température connue
PUT    /api/thermostats/{id}/temperature   → Modifie la consigne (7-22°C)

GET    /api/alerts                  → 5 dernières alertes

GET    /api/push/vapid-public-key   → Clé publique pour l'abonnement push
POST   /api/push/subscribe          → Enregistre un abonnement push

GET    /swagger-ui/index.html       → Documentation interactive

Toutes les routes ci-dessus, sauf /api/auth/login, nécessitent un header Authorization: Bearer <token>.

## Intégrations supportées

- Airthings (View Plus) — qualité de l'air, température, CO2, radon, humidité, composés organiques volatils, pression, PM1, PM2.5
- Sinopé TH1124ZB — thermostats électriques 240V via Zigbee/MQTT
- Web Push — notifications temps réel sur seuils dépassés (Chrome/Android et Safari/iOS)
- Autres devices Zigbee (à venir)

## Installation

### Prérequis

- Java 21
- Maven
- PostgreSQL
- Raspberry Pi 4 + ConBee III (pour les thermostats Sinopé)
- htpasswd (apache2-utils) — pour la création de comptes utilisateurs

### Variables d'environnement

export DB_URL=jdbc:postgresql://localhost:5432/iot_hub
export DB_USERNAME=iot_user
export DB_PASSWORD=ton_mot_de_passe

export AIRTHINGS_CLIENT_ID=ton_client_id
export AIRTHINGS_CLIENT_SECRET=ton_client_secret
export AIRTHINGS_ACCOUNT_ID=ton_account_id

export JWT_SECRET=une_chaine_aleatoire_longue

export VAPID_PUBLIC_KEY=ta_cle_publique
export VAPID_PRIVATE_KEY=ta_cle_privee
export VAPID_SUBJECT=mailto:ton-email@exemple.com

export SPRING_PROFILES_ACTIVE=dev

### Créer un compte utilisateur

htpasswd -bnBC 10 "" TonMotDePasse | cut -d: -f2 > /tmp/hash.txt
psql -U iot_user -d iot_hub -c "INSERT INTO users (email, password, role) VALUES ('ton-email@exemple.com', '$(cat /tmp/hash.txt)', 'USER');"
rm /tmp/hash.txt

### Lancement

mvn clean install -DskipTests
mvn spring-boot:run -pl module-api

L'API est disponible sur http://localhost:8080
La documentation Swagger sur http://localhost:8080/swagger-ui/index.html

## Déploiement en production

Déployé via Docker Compose sur Raspberry Pi, avec un reverse proxy Nginx sur un VPS distinct (HTTPS via Let's Encrypt) communiquant avec le Pi à travers un tunnel Tailscale.

git pull
docker compose up -d --build iot-hub-api

## Auteur
docker ps
sudo ufw status
Grégory Fulgueiras — Développeur Backend Java Spring Boot
y-rog.com