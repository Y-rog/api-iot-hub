# 🏠 IoT Hub API

API centralisée pour objets connectés, construite avec **Spring Boot** et une architecture **hexagonale modulaire**.

## Architecture

Le projet suit une architecture **Monolithe Modulaire Hexagonal** — chaque module a un rôle précis et communique via des événements Spring.

```
[Airthings API] → module-integrations → module-devices → module-api → [Front]
```

### Les modules

| Module | Rôle |
|---|---|
| `module-shared` | Classes communes (DeviceData, events, enums) |
| `module-devices` | Gestion et persistance des appareils |
| `module-integrations` | Connexion aux APIs externes (Airthings...) |
| `module-api` | Exposition REST vers le front |
| `module-alerts` | Surveillance et alertes *(à venir)* |
| `module-history` | Historique des mesures *(à venir)* |

## Stack technique

- **Java 21** + **Spring Boot 3.2**
- **PostgreSQL** — persistance des données
- **MapStruct** — mapping entre couches
- **Lombok** — réduction du boilerplate
- **OAuth2** — authentification Airthings

## Endpoints REST

```
GET    /api/devices         → Liste tous les appareils
GET    /api/devices/{id}    → Détail d'un appareil
POST   /api/devices         → Ajoute un appareil
PUT    /api/devices/{id}    → Met à jour un appareil
DELETE /api/devices/{id}    → Supprime un appareil
```

## Intégrations supportées

- ✅ **Airthings** (View Plus) — qualité de l'air, température, CO2, radon...
- ⏳ **Sinopé** — thermostats électriques via MQTT *(à venir)*

## Installation

### Prérequis

- Java 21
- Maven
- PostgreSQL

### Variables d'environnement

Copie le fichier d'exemple et remplis les valeurs :

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

## Auteur

**Grégory Fulgueiras** — Développeur Backend Java Spring Boot
[y-rog.com](https://y-rog.com)