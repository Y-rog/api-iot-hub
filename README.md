# 🏠 IoT Hub API

API centralisée pour objets connectés, construite avec **Spring Boot** et une architecture **hexagonale modulaire**.

## Architecture

Le projet suit une architecture **Monolithe Modulaire Hexagonal** — chaque module a un rôle précis et communique via des événements Spring.

```
[Airthings API] ──► module-integrations ──► module-devices ──► module-api ──► [Front]
[Sinopé Zigbee] ──►        │
                     module-alerts
                     module-history
```

### Les modules

| Module | Rôle |
|---|---|
| `module-shared` | Classes communes (DeviceData, events, enums) |
| `module-devices` | Gestion et persistance des appareils |
| `module-integrations` | Connexion aux APIs externes (Airthings, Sinopé MQTT) |
| `module-api` | Exposition REST vers le front |
| `module-alerts` | Surveillance et alertes |
| `module-history` | Historique des mesures |

## Stack technique

- **Java 21** + **Spring Boot 3.2**
- **PostgreSQL** — persistance des données
- **MapStruct** — mapping entre couches
- **Lombok** — réduction du boilerplate
- **OAuth2** — authentification Airthings
- **MQTT / Mosquitto** — communication avec les thermostats Sinopé
- **Zigbee2MQTT** — bridge Zigbee vers MQTT

## Infrastructure IoT

- **Raspberry Pi 4** — serveur domotique local
- **ConBee III** — coordinateur Zigbee USB
- **Zigbee2MQTT** — bridge Zigbee → MQTT
- **Mosquitto** — broker MQTT (Docker)

## Endpoints REST

```
GET    /api/devices              → Liste tous les appareils
GET    /api/devices/{id}         → Détail d'un appareil
POST   /api/devices              → Ajoute un appareil
PUT    /api/devices/{id}         → Met à jour un appareil
DELETE /api/devices/{id}         → Supprime un appareil

GET    /api/alerts               → Liste les alertes
PUT    /api/alerts/{id}/read     → Marquer comme lue

GET    /api/alertRules           → Liste les règles d'alerte
POST   /api/alertRules           → Créer une règle
PUT    /api/alertRules/{id}      → Modifier une règle
DELETE /api/alertRules/{id}      → Supprimer une règle

GET    /api/history/{deviceId}   → Historique des mesures

GET    /swagger-ui/index.html    → Documentation interactive
```

## Intégrations supportées

- ✅ **Airthings** (View Plus) — qualité de l'air, température, CO2, radon, PM2.5...
- ✅ **Sinopé TH1124ZB** — thermostats électriques 240V via Zigbee/MQTT
- ⏳ Autres devices Zigbee *(à venir)*

## Installation

### Prérequis

- Java 21
- Maven
- PostgreSQL
- Raspberry Pi 4 + ConBee III (pour les thermostats Sinopé)

### Variables d'environnement

```bash
# Base de données
export DB_URL=jdbc:postgresql://localhost:5432/iot_hub
export DB_USERNAME=iot_user
export DB_PASSWORD=ton_mot_de_passe

# Airthings API
export AIRTHINGS_CLIENT_ID=ton_client_id
export AIRTHINGS_CLIENT_SECRET=ton_client_secret
export AIRTHINGS_ACCOUNT_ID=ton_account_id

# Profil Spring
export SPRING_PROFILES_ACTIVE=dev
```

### Lancement

```bash
# Compiler
mvn clean install -DskipTests

# Lancer
mvn spring-boot:run -pl module-api
```

L'API est disponible sur `http://localhost:8080`
La documentation Swagger sur `http://localhost:8080/swagger-ui/index.html`

## Auteur

**Grégory Fulgueiras** — Développeur Backend Java Spring Boot
[y-rog.com](https://y-rog.com)