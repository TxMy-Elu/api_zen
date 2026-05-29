# API Zen - Documentation Professionnelle

📖 **[→ Aller à la Documentation Complète](./docs/00-README.md)**

---

## ⚡ Démarrage Rapide (2 minutes)

### 1️⃣ Démarrer la Base de Données
```powershell
docker compose up -d
```

### 2️⃣ Lancer l'API
```powershell
.\mvnw.cmd spring-boot:run
```

### 3️⃣ Se Connecter
```powershell
$body = @{ email = "admin@cesizen.fr"; password = "password" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

### 4️⃣ Accéder à l'API
- 🔗 **Swagger UI**: http://localhost:8080/swagger-ui.html
- 📚 **Documentation**: `./docs/`
- 🧪 **Collection Bruno**: `./CESIZen/`

---

## 📚 Structure de la Documentation

La documentation a été réorganisée en modules indépendants pour une meilleure navigation:

### 🚀 Getting Started
- **[Vue d'ensemble](./docs/01-overview.md)** - Stack technique, architecture globale
- **[Configuration & Lancement](./docs/02-setup.md)** - Installation, Docker, Maven

### 🔐 Sécurité & Authentification
- **[Sécurité & Autorisations](./docs/03-security.md)** - JWT, RBAC, routes publiques
- **[Authentification API](./docs/04-auth-api.md)** - Login, register, password reset

### 🔌 Endpoints API (Routes Détaillées)
- **[Utilisateurs & Rôles](./docs/05-user-api.md)** - Gestion des utilisateurs (admin)
- **[Catégories](./docs/06-categorie-api.md)** - CRUD catégories
- **[Articles](./docs/07-article-api.md)** - CRUD articles + upload fichier
- **[Exercices](./docs/08-exercice-api.md)** - CRUD exercices respiration
- **[Consultations](./docs/09-consulter-api.md)** - Suivi consultations articles
- **[Participations](./docs/10-exercer-api.md)** - Suivi participations exercices
- **[Logs & Journalisation](./docs/11-logs-api.md)** - Logs activité et connexions (admin)

### 📖 Guides & Références
- **[Exemples de Requêtes](./docs/12-examples.md)** - Requêtes PowerShell/cURL prêtes à l'emploi
- **[Gestion des Erreurs](./docs/13-error-handling.md)** - Codes HTTP, solutions
- **[Déploiement & Production](./docs/14-deployment.md)** - Docker, Kubernetes, recommandations

---

## 📋 Stack Technique

| Composant | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 4.x |
| MySQL | 8.4.7 |
| JWT (JJWT) | Latest |
| Docker | Latest |

---

## 🔑 Comptes de Démonstration

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| `admin@cesizen.fr` | `password` | ROLE_ADMIN |
| `user@cesizen.fr` | `password` | ROLE_USER |

Injectés via `src/main/resources/data-init.sql`

---

## 🔗 Liens Rapides

| Ressource | URL |
|-----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |
| **Collection Bruno** | `/CESIZen/` |
| **Configuration App** | `src/main/resources/application.properties` |
| **Données de Test** | `src/main/resources/data-init.sql` |

---

## 📖 Guide de Navigation

### Pour un **Nouveau Développeur**:
1. [Vue d'ensemble](./docs/01-overview.md) - Comprendre l'architecture
2. [Configuration & Lancement](./docs/02-setup.md) - Installer et lancer localement
3. [Sécurité](./docs/03-security.md) - JWT et autorisations
4. [Authentification API](./docs/04-auth-api.md) - Commencer avec l'API

### Pour un **Intégrateur Frontend**:
1. [Authentification API](./docs/04-auth-api.md) - Login, tokens
2. [Exemplaires de Requêtes](./docs/12-examples.md) - Code prêt à copier
3. Endpoints spécifiques (Articles, Catégories, Exercices)
4. [Gestion des Erreurs](./docs/13-error-handling.md) - Gérer les erreurs

### Pour un **DevOps / SRE**:
1. [Déploiement & Production](./docs/14-deployment.md) - Docker, K8s, checklist
2. [Configuration](./docs/02-setup.md) - Configuration de l'app
3. [Gestion des Erreurs](./docs/13-error-handling.md) - Monitoring

---

## ✅ Checklist de Démarrage

- [ ] JDK 21 installé (`javac -version`)
- [ ] Docker Desktop en cours d'exécution
- [ ] `docker compose up -d` - MySQL démarré
- [ ] `.\mvnw.cmd spring-boot:run` - API démarrée
- [ ] Accès à Swagger: http://localhost:8080/swagger-ui.html
- [ ] Login avec `admin@cesizen.fr` / `password`

---

## 📞 Support & Questions

**Problèmes courants?** → Voir [Gestion des Erreurs](./docs/13-error-handling.md)

**Déployer en prod?** → Voir [Déploiement & Production](./docs/14-deployment.md)

**Intégrer l'API?** → Voir [Exemples de Requêtes](./docs/12-examples.md)

---

**Version API**: 1.0  
**Dernière mise à jour**: 2026-04-21  
**Status**: Production-Ready ✅

