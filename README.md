# Athletix Governance — Backend API

API REST de la plateforme numérique fédérale **Athletix Governance**, développée pour la **Fédération Tunisienne de Natation (FTN)** dans le cadre du module **Pi-DEV — Mission Entreprise** (Classes 1ALINFO / 3ALSLEAM, année universitaire 2025-2026).

Cette API expose l'ensemble des données et fonctionnalités de la fédération : authentification, athlètes, clubs, compétitions, résultats et classements, contenu/actualités, programmes d'entraînement, piscines (réservation de créneaux), et forum communautaire.

Le frontend Angular associé se trouve dans le dépôt **ftn_frontend_nouveau** (`angular-export/`).

## Stack technique

| Composant | Technologie |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Build | Maven (wrapper inclus — `mvnw` / `mvnw.cmd`) |
| Base de données | PostgreSQL 15 |
| Migrations de schéma | Liquibase (changesets XML idempotents) |
| Authentification | JWT (access + refresh token), `jjwt` 0.11.5 |
| Mapping DTO ↔ Entité | ModelMapper |
| Validation | Bean Validation (`spring-boot-starter-validation`) |
| Documentation API | springdoc-openapi (Swagger UI) |
| Conteneurisation | Docker / Docker Compose |

## Architecture — modules fonctionnels

Le projet est organisé en couches `controller / service / repository / model / dtos` par domaine métier, regroupées en 8 modules fonctionnels :

1. **Authentification & Utilisateurs** — inscription, connexion, rafraîchissement de jeton, rôles (RBAC)
2. **Compétitions** — création, calendrier, épreuves, inscriptions
3. **Résultats & Classements** — saisie, validation, classements par catégorie
4. **Athlètes & Clubs** — fiches publiques, CRUD club/membres, staff technique
5. **Contenu & Backoffice** — actualités, statistiques agrégées du tableau de bord public
6. **Programmes d'entraînement** — catalogue par tranche d'âge
7. **Piscines** *(valeur ajoutée)* — infrastructures affiliées, couloirs assignés aux clubs
8. **Forum communautaire** *(valeur ajoutée)* — sujets, réponses, réactions emoji, images

## Prérequis

- JDK 17
- Maven (ou utiliser le wrapper fourni, aucune installation requise)
- PostgreSQL 15 (local **ou** via Docker)
- Docker + Docker Compose (optionnel, mais recommandé)

## Configuration

1. Copier le fichier d'exemple et le compléter :

   ```bash
   cp .env.example .env
   ```

2. Variables à renseigner dans `.env` (voir `.env.example` pour la liste complète et des valeurs de départ) :

   | Variable | Rôle |
   |---|---|
   | `DB_URL`, `DB_USER`, `DB_PASS` | Connexion PostgreSQL |
   | `SERVER_PORT` | Port d'écoute de l'API (8080 par défaut) |
   | `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION` | Authentification par jetons |
   | `APP_COOKIE_SECURE`, `APP_CORS_ALLOWED_*` | Sécurité cookies / CORS |
   | `PGADMIN_EMAIL`, `PGADMIN_PASSWORD` | Identifiants pgAdmin (uniquement si Docker Compose est utilisé) |

   > Le schéma de base de données est entièrement géré par **Liquibase** : aucune commande de migration manuelle n'est nécessaire, les changesets s'appliquent automatiquement au démarrage de l'application.

## Lancement

### Option A — Avec Docker Compose (recommandé)

```bash
docker compose up --build
```

Cela démarre PostgreSQL, pgAdmin (sur http://localhost:5050) et l'API backend (sur http://localhost:8081, mappé vers le port interne 8080).

### Option B — En local (PostgreSQL déjà installé)

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

L'API est alors disponible sur `http://localhost:8080` (ou le port défini par `SERVER_PORT`).

## Documentation de l'API

Une fois l'application lancée, la documentation interactive Swagger est disponible sur :

```
http://localhost:8080/swagger-ui/index.html
```

## Compte administrateur de démonstration

Un compte administrateur est créé automatiquement par les changesets Liquibase de seed (`028-seed-staff-and-links.xml`), pour permettre de tester immédiatement l'espace backoffice :

| Email | Mot de passe |
|---|---|
| `admin@ftn.tn` | `Admin@2024` |

> ⚠️ Ces identifiants sont réservés à l'environnement de démonstration/test. Ne jamais les réutiliser en production.

## Lancer les tests

```bash
./mvnw test
```

## Structure du projet

```
src/main/java/com/ftn/backend/
├── controller/      ← endpoints REST par domaine
├── service/         ← logique métier
├── repository/       ← accès aux données (Spring Data JPA)
├── model/            ← entités JPA
├── dtos/              ← objets de transfert (request/response)
├── security/          ← configuration JWT, filtres, règles d'accès public
└── shared/            ← composants transverses

src/main/resources/
├── application.yml
└── db/changelog/      ← migrations Liquibase (un fichier XML par évolution de schéma)
```

## Auteurs

Projet académique — ESPRIT School of Engineering — Pi-DEV Mission Entreprise — Classes 1ALINFO / 3ALSLEAM — Année universitaire 2025-2026.
Contexte métier fourni par la Fédération Tunisienne de Natation (FTN).
