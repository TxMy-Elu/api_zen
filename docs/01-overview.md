# Vue d'ensemble - API Zen

## 📋 À propos du projet

**API Zen** est une plateforme REST complète dédiée au bien-être et aux exercices de respiration.

Fonctionnalités principales:
- 🔐 **Authentification JWT** - Connexion, inscription, reset de mot de passe sécurisé
- 👥 **Gestion utilisateurs & rôles** - RBAC avec Admin/User
- 📚 **Contenu riche** - Catégories, articles, exercices de respiration
- 📊 **Suivi engagement** - Consultations d'articles, participations aux exercices
- 🔍 **Logs & audit** - Journalisation complète des activités et connexions
- 📖 **Documentation auto** - Swagger UI intégré, Collection Bruno

---

## 🏗️ Architecture Technique

### Stack

| Composant | Version | Rôle |
|-----------|---------|------|
| **Java** | 21 | Langage |
| **Spring Boot** | 4.x | Framework web/REST |
| **Spring Web** | - | Contrôleurs REST |
| **Spring Security** | - | Authentification/Autorisation |
| **Spring Data JPA** | - | ORM, accès aux données |
| **Spring Validation** | - | Validation Bean (@Valid, @NotNull, etc.) |
| **JWT (JJWT)** | - | Tokens JWT |
| **MySQL** | 8.4.7 | Base de données relationnelle |
| **Maven** | Wrapper | Build tool & gestion dépendances |
| **SpringDoc OpenAPI** | - | Swagger UI & OpenAPI 3.0 |

### Modules Spring Boot Activés

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `mysql-connector-java`
- `jjwt` (JWT)
- `springdoc-openapi-starter-webmvc-ui` (Swagger)

---

## 🗄️ Modèle de Données

### Entités principales

```
User (Utilisateur)
├── id (PK)
├── email (UNIQUE)
├── password (hashé)
├── nom, prenom
├── active (boolean)
├── creationDate
├── role (FK → Role)
├── LogActivite (1-N)
├── LogConnexion (1-N)
├── Consulter (1-N)
└── Exercer (1-N)

Role
├── id (PK)
├── libelle (ROLE_ADMIN, ROLE_USER)
└── User (N-1)

Article
├── id (PK)
├── titre
├── contenu
├── typeMedia (text, image, video, pdf)
├── mediaUrl
├── datePublication, dateModification
├── estPublie (boolean)
├── categorie (FK → Categorie)
└── Consulter (1-N)

Categorie
├── id (PK)
├── libelle (UNIQUE)
├── description
└── Article (1-N)

Exercice
├── id (PK)
├── nom
├── dureeInspiration, dureeApnee, dureeExpiration
├── description
└── Exercer (1-N)

Consulter (Consultation)
├── id (PK)
├── user (FK → User)
├── article (FK → Article)
└── viewedAt (timestamp)

Exercer (Participation)
├── id (PK)
├── user (FK → User)
├── exercice (FK → Exercice)
└── completedAt (timestamp)

LogActivite (Audit)
├── id (PK)
├── user (FK → User)
├── tableConcernee (USER, ARTICLE, EXERCICE, etc.)
├── typeAction (CREATE, UPDATE, DELETE)
├── idEnregistrement (l'ID affecté)
├── anciennesValeurs, nouvellesValeurs (JSON)
└── dateAction (timestamp)

LogConnexion
├── id (PK)
├── email
├── ip
├── reussi (boolean)
└── dateConnexion (timestamp)
```

---

## 🔌 Flux de Requête Type

```
Request HTTP
     ↓
CORS Filter (OPTIONS passent)
     ↓
JWT Filter (parse token, set SecurityContext)
     ↓
Security Filter (vérifie permissions @PreAuthorize)
     ↓
Controller (valide @Valid, délégie au Service)
     ↓
Service (logique métier, log automatiquement via Aspect)
     ↓
Repository (JPA → SQL → MySQL)
     ↓
Response JSON
```

---

## 🔐 Modèle de Sécurité

### Authentification
- **Type**: JWT Bearer Token
- **Durée**: 24h (configurable)
- **Contenu du token**:
  - `email` (subject)
  - `userId`
  - `role` (ROLE_ADMIN ou ROLE_USER)
- **Header requis**: `Authorization: Bearer <token>`

### Autorisation
- **Admin**: Accès complet (routes `/api/user/**`, `/api/log-*/**`, toutes les écritures)
- **User**: Accès limité (lecture articles/catégories/exercices, consultation, participation)
- **Public**: Login, register, GET articles/catégories/exercices, Swagger

---

## 📝 Conventions de Code

### DTOs (Data Transfer Objects)
- **CreateDto**: Utilisé pour POST/création (pas d'ID)
- **UpdateDto**: Utilisé pour PUT/modification (peut inclure l'ID)
- **Dto**: Réponse GET (contient ID et champs complets)

Exemple:
```java
// POST /api/categorie
CategorieCreateDto { libelle, description }

// GET /api/categorie/{id}
CategorieDto { idCategorie, libelle, description }
```

### Validation
- Tous les DTOs utilisent `@Valid` dans les Controllers
- Annotations: `@NotNull`, `@NotEmpty`, `@Size`, `@Email`, etc.
- Réponses d'erreur: `400 Bad Request` si validation échoue

### Logging
- `LogActiviteAspect` capture automatiquement les `create()`, `update()`, `delete()`
- Table `log_activite` historise toutes les opérations
- Admin peut consulter via `/api/log-activite/**`

---

## 📦 Structure des Dossiers

```
src/main/
├── java/edu/tx/api_zen/
│   ├── ApiZenApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java          ← Configuration JWT & permissions
│   │   ├── PasswordEncoderConfig.java   ← Configuration encodeur mot de passe
│   │   ├── LogActiviteAspect.java       ← AOP logging automatique
│   │   └── GlobalExceptionInterceptor.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── ArticleController.java
│   │   ├── CategorieController.java
│   │   ├── ExerciceController.java
│   │   ├── ConsulterController.java
│   │   ├── ExercerController.java
│   │   ├── LogActiviteController.java
│   │   ├── LogConnexionController.java
│   │   ├── RoleController.java
│   │   └── RestExceptionHandler.java    ← Gestion globale erreurs
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── ArticleService.java
│   │   ├── CategorieService.java
│   │   ├── ConsulterService.java
│   │   ├── ExercerService.java
│   │   ├── ExerciceService.java
│   │   ├── LogActiviteService.java
│   │   ├── LogConnexionService.java
│   │   ├── EmailService.java
│   │   ├── PasswordResetService.java
│   │   └── ... (autres services)
│   ├── dao/                             ← Repositories (DAOs)
│   │   ├── UserDao.java
│   │   ├── ArticleDao.java
│   │   ├── CategorieDao.java
│   │   ├── ExerciceDao.java
│   │   ├── ConsulterDao.java
│   │   ├── ExercerDao.java
│   │   ├── LogActiviteDao.java
│   │   ├── LogConnexionDao.java
│   │   ├── RoleDao.java
│   │   ├── PasswordResetTokenDao.java
│   │   └── ... (autres DAOs)
│   ├── model/                           ← Entities JPA
│   │   ├── User.java
│   │   ├── Article.java
│   │   ├── Categorie.java
│   │   ├── Exercice.java
│   │   ├── Consulter.java
│   │   ├── Exercer.java
│   │   ├── LogActivite.java
│   │   ├── LogConnexion.java
│   │   ├── Role.java
│   │   ├── PasswordResetToken.java
│   │   └── ... (autres entities)
│   ├── dto/
│   │   ├── AuthRequest.java
│   │   ├── ArticleCreateDto.java
│   │   └── ... (tous les DTOs)
│   ├── mapper/
│   │   ├── ArticleMapper.java
│   │   ├── CategorieMapper.java
│   │   ├── UserMapper.java
│   │   └── ... (autres mappers)
│   ├── security/
│   │   ├── JwtUtils.java                ← Génération/validation tokens
│   │   ├── JwtAuthenticationFilter.java ← Filter JWT
│   │   └── CustomUserDetailsService.java
│   └── GenerateHash.java
├── resources/
│   ├── application.properties           ← Configuration (DB, JWT, mail, etc.)
│   ├── data-init.sql                    ← Données de démo
│   ├── static/uploads/                  ← Fichiers uploadés
│   └── templates/
```

---

## 🚀 Processus de Démarrage

1. **Docker compose** crée la base MySQL
2. **Spring Boot** démarre et se connecte
3. **Hibernate** crée les tables (`ddl-auto=create`)
4. **data-init.sql** injecte les données de démo
5. **API** prête à recevoir des requêtes sur `http://localhost:8080`

---

## 📊 Statistiques du Projet

- **11 Contrôleurs** couvrant 50+ endpoints
- **8 Services** impliquant la logique métier
- **8 Entities** JPA pour le modèle de données
- **19 Test Files** (unitaires & intégration)
- **100+ DTOs** pour la sérialisation/désérialisation

---

## 🔗 Voir Aussi

- **[Configuration & Lancement](./02-setup.md)** - Comment démarrer localement
- **[Sécurité](./03-security.md)** - Détails JWT et RBAC
- **[Endpoints](./04-auth-api.md)** - Routes détaillées
- **[Exemples](./12-examples.md)** - Requêtes prêtes à l'emploi

---

**Version API**: 1.0  
**Dernière mise à jour**: 2026-04-21

