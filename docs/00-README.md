# API Zen - Documentation Complète

Bienvenue dans la documentation officielle de l'API Zen. Cette documentation est organisée en modules indépendants pour une meilleure lisibilité et maintenance.

## 📚 Table des matières

### Getting Started
1. **[Vue d'ensemble](./01-overview.md)** - Présentation générale du projet, stack technique
2. **[Configuration & Lancement](./02-setup.md)** - Prérequis, Docker, Maven, base de données

### 🔐 Sécurité & Authentification
3. **[Sécurité & Autorisations](./03-security.md)** - Routes publiques, authentification JWT, RBAC
4. **[Authentification API](./04-auth-api.md)** - Endpoints login, register, password reset

### 🔌 Endpoints API

#### Users & Roles
5. **[Utilisateurs & Rôles](./05-user-api.md)** - Gestion des utilisateurs (admin), liste des rôles

#### Contenu
6. **[Catégories](./06-categorie-api.md)** - Gestion des catégories de contenu
7. **[Articles](./07-article-api.md)** - Gestion des articles avec support média et upload fichier
8. **[Exercices](./08-exercice-api.md)** - Gestion des exercices de respiration

#### Suivi Utilisateur
9. **[Consultations](./09-consulter-api.md)** - Suivi des consultations d'articles
10. **[Participations](./10-exercer-api.md)** - Suivi des participations aux exercices

### 📊 Administration & Monitoring
11. **[Logs & Journalisation](./11-logs-api.md)** - Logs activité et connexions (admin)

### 📖 Guides & Exemples
12. **[Exemples de Requêtes](./12-examples.md)** - Requêtes PowerShell/cURL prêtes à l'emploi
13. **[Gestion des Erreurs](./13-error-handling.md)** - Codes d'erreur, formats de réponse
14. **[Déploiement & Recommandations](./14-deployment.md)** - Passage en production, limites connues

---

## ⚡ Démarrage Rapide

### 1️⃣ Lancer la base de données
```powershell
docker compose up -d
```

### 2️⃣ Lancer l'API
```powershell
.\mvnw.cmd spring-boot:run
```

### 3️⃣ Se connecter
```powershell
# POST /api/auth/login
$body = @{ email = "admin@cesizen.fr"; password = "password" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $body
```

### 4️⃣ Accéder à Swagger
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📖 Documentation Comptes de Demo

- **Admin**: `admin@cesizen.fr` / `password` (ROLE_ADMIN)
- **User**: `user@cesizen.fr` / `password` (ROLE_USER)

Ces comptes sont injectés via `data-init.sql` au démarrage.

---

## 🔗 Ressources

| Ressource | URL |
|-----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Collection Bruno | `/CESIZen/` |
| Configuration | `src/main/resources/application.properties` |
| Init Data | `src/main/resources/data-init.sql` |

---

## 📞 Structure du Dossier Docs

```
docs/
├── 00-README.md                    ← Vous êtes ici
├── 01-overview.md                  ← Stack technique, architecture
├── 02-setup.md                     ← Installation, configuration
├── 03-security.md                  ← JWT, RBAC, routes publiques
├── 04-auth-api.md                  ← Login, register, password reset
├── 05-user-api.md                  ← User CRUD (admin)
├── 06-categorie-api.md             ← Categorie CRUD
├── 07-article-api.md               ← Article CRUD + upload
├── 08-exercice-api.md              ← Exercice CRUD
├── 09-consulter-api.md             ← Consultation tracking
├── 10-exercer-api.md               ← Exercice participation
├── 11-logs-api.md                  ← Logs (admin)
├── 12-examples.md                  ← Requêtes d'exemple
├── 13-error-handling.md            ← Codes d'erreur
└── 14-deployment.md                ← Production, limites
```

---

## 🚀 Contribution & Maintenance

Pour ajouter une fonctionnalité:
1. Ajouter les routes dans le fichier API correspondant
2. Ajouter des exemples dans `12-examples.md`
3. Mettre à jour les limites connues si applicable
4. Vérifier la cohérence avec Swagger UI

---

**Dernière mise à jour**: 2026-04-21  
**Version**: 1.0 (Documentation Ultra-Pro)

