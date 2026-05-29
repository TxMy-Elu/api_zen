# 📖 Navigation - API Zen Documentation

Bienvenue! Voici comment naviguer la documentation de manière optimale selon vos besoins.

---

## 🎯 Par Profil

### 👨‍💻 Je Suis Développeur et Je Commence

**Ordre de lecture recommandé:**

1. **[README principal](../README.md)** (5 min)
   - Vue d'ensemble rapide
   - Démarrage en 2 minutes

2. **[01-overview.md](./01-overview.md)** (15 min)
   - Stack technique
   - Architecture
   - Modèle de données

3. **[02-setup.md](./02-setup.md)** (15 min)
   - Installation JDK 21
   - Démarrer Docker & API
   - Configuration

4. **[03-security.md](./03-security.md)** (10 min)
   - Comprendre JWT
   - Modèle RBAC
   - Routes publiques vs admin

5. **[04-auth-api.md](./04-auth-api.md)** (10 min)
   - Endpoints authentication
   - Comment se connecter
   - Obtenir un token

**Prochaines étapes:** Choisir un endpoint spécifique dans la section "Par Ressource"

---

### 🔌 Je Suis Développeur Frontend / Intégrateur

**Ordre de lecture rapide:**

1. **[README principal](../README.md)** (2 min)

2. **[04-auth-api.md](./04-auth-api.md)** (10 min)
   - Comment faire un login?
   - Format du token?

3. **Choisir vos endpoints:**
   - [Articles](./07-article-api.md) - Lire, créer
   - [Catégories](./06-categorie-api.md) - Lister
   - [Exercices](./08-exercice-api.md) - Lister
   - [Consultations](./09-consulter-api.md) - Enregistrer une vue
   - [Participations](./10-exercer-api.md) - Enregistrer participation

4. **[12-examples.md](./12-examples.md)** (20 min)
   - Copier les exemples PowerShell/cURL
   - Adapter à votre langage
   - Tester directement

5. **[13-error-handling.md](./13-error-handling.md)** (au besoin)
   - Gérer les erreurs 400, 401, 403, 404

---

### 🏗️ Je Suis DevOps / SRE

**Ordre de lecture:**

1. **[02-setup.md](./02-setup.md)** (10 min)
   - Configuration de base
   - Docker Compose

2. **[14-deployment.md](./14-deployment.md)** (30 min)
   - ⚠️ Limites connues
   - Docker multi-conteneurs
   - Kubernetes
   - Checklist production
   - Monitoring

3. **[03-security.md](./03-security.md)** (5 min)
   - Configuration sécurité

---

### 🧪 Je Suis QA / Testeur

**Ordre de lecture:**

1. **[02-setup.md](./02-setup.md)** - Comment lancer l'API

2. **[12-examples.md](./12-examples.md)** - Exemples de requêtes

3. **[13-error-handling.md](./13-error-handling.md)** - Cas d'erreur à tester

4. **Endpoints spécifiques** - Voir ci-dessous

---

## 📚 Par Ressource

### 🔐 Authentification & Sécurité

| Document | Contenu |
|----------|---------|
| [03-security.md](./03-security.md) | JWT, RBAC, routes publiques, matrice d'accès |
| [04-auth-api.md](./04-auth-api.md) | Login, register, password reset |
| [13-error-handling.md](./13-error-handling.md) | 401 Unauthorized, 403 Forbidden |

**Question typique:** "Comment faire un login?"
→ Voir [04-auth-api.md](./04-auth-api.md) section 1️⃣

---

### 👥 Utilisateurs & Rôles

| Document | Contenu |
|----------|---------|
| [05-user-api.md](./05-user-api.md) | CRUD utilisateurs, liste rôles (ADMIN only) |
| [03-security.md](./03-security.md) | RBAC et permissions |

**Question typique:** "Comment créer un utilisateur?"
→ Voir [05-user-api.md](./05-user-api.md) section 3️⃣

---

### 📂 Catégories

| Document | Contenu |
|----------|---------|
| [06-categorie-api.md](./06-categorie-api.md) | Lister, créer, mettre à jour, supprimer |
| [12-examples.md](./12-examples.md) | Exemples PowerShell/cURL |

**Question typique:** "Comment lister les catégories?"
→ Voir [06-categorie-api.md](./06-categorie-api.md) section 1️⃣

---

### 📄 Articles

| Document | Contenu |
|----------|---------|
| [07-article-api.md](./07-article-api.md) | CRUD articles, upload fichier |
| [12-examples.md](./12-examples.md) | Créer article, upload vidéo |
| [13-error-handling.md](./13-error-handling.md) | Validation typeMedia/mediaUrl |

**Question typique:** "Comment uploader une vidéo?"
→ Voir [07-article-api.md](./07-article-api.md) section 7️⃣ + [12-examples.md](./12-examples.md)

---

### 🧘 Exercices

| Document | Contenu |
|----------|---------|
| [08-exercice-api.md](./08-exercice-api.md) | CRUD exercices respiration |
| [12-examples.md](./12-examples.md) | Créer exercice |

**Question typique:** "Comment créer un exercice respiration?"
→ Voir [08-exercice-api.md](./08-exercice-api.md) section 3️⃣

---

### 👀 Suivi des Vues & Participations

| Document | Contenu |
|----------|---------|
| [09-consulter-api.md](./09-consulter-api.md) | Enregistrer consultation article |
| [10-exercer-api.md](./10-exercer-api.md) | Enregistrer participation exercice |
| [12-examples.md](./12-examples.md) | Exemples POST |

**Question typique:** "Comment enregistrer qu'un user a lu un article?"
→ Voir [09-consulter-api.md](./09-consulter-api.md) section 6️⃣

---

### 📊 Logs & Audit

| Document | Contenu |
|----------|---------|
| [11-logs-api.md](./11-logs-api.md) | Logs activité et connexions (ADMIN) |
| [12-examples.md](./12-examples.md) | Voir historique, connexions échouées |

**Question typique:** "Comment voir l'historique d'un article?"
→ Voir [11-logs-api.md](./11-logs-api.md) section 6️⃣

---

## 🔍 Recherche Rapide par Mot-Clé

| Mot-clé | Document | Section |
|---------|----------|---------|
| Login | [04-auth-api.md](./04-auth-api.md) | 1️⃣ Login |
| Token JWT | [03-security.md](./03-security.md) | JWT (JSON Web Tokens) |
| Upload fichier | [07-article-api.md](./07-article-api.md) | 7️⃣ Upload |
| Email doublon | [13-error-handling.md](./13-error-handling.md) | 409 Conflict |
| 403 Forbidden | [13-error-handling.md](./13-error-handling.md) | 403 Forbidden |
| Docker | [02-setup.md](./02-setup.md) | 🐳 Démarrer MySQL |
| Production | [14-deployment.md](./14-deployment.md) | Toute le document |
| Kubernetes | [14-deployment.md](./14-deployment.md) | Option 3: Kubernetes |
| Rate Limiting | [14-deployment.md](./14-deployment.md) | ⚠️ Limites Connues |

---

## 📈 Arborescence de Documentation

```
docs/
├── 00-README.md                    ← INDEX PRINCIPAL
│
├── 01-overview.md                  ← ARCHITECTURE
├── 02-setup.md                     ← INSTALLATION
│
├── 03-security.md                  ← JWT / RBAC
├── 04-auth-api.md                  ← LOGIN
│
├── 05-user-api.md                  ← USERS (ADMIN)
├── 06-categorie-api.md             ← CATEGORIES
├── 07-article-api.md               ← ARTICLES + UPLOAD
├── 08-exercice-api.md              ← EXERCICES
├── 09-consulter-api.md             ← CONSULTATIONS
├── 10-exercer-api.md               ← PARTICIPATIONS
├── 11-logs-api.md                  ← LOGS (ADMIN)
│
├── 12-examples.md                  ← EXEMPLES CODE
├── 13-error-handling.md            ← ERREURS
└── 14-deployment.md                ← PRODUCTION
```

---

## ⏱️ Temps de Lecture Estimé

| Document | Temps | Priorité |
|----------|-------|----------|
| 00-README.md | 5 min | 🔴 ESSENTIEL |
| 01-overview.md | 15 min | 🔴 ESSENTIEL |
| 02-setup.md | 15 min | 🔴 ESSENTIEL |
| 03-security.md | 10 min | 🔴 ESSENTIEL |
| 04-auth-api.md | 10 min | 🟠 Important |
| 05-user-api.md | 10 min | 🟡 Selon besoin |
| 06-categorie-api.md | 8 min | 🟡 Selon besoin |
| 07-article-api.md | 12 min | 🟡 Selon besoin |
| 08-exercice-api.md | 8 min | 🟡 Selon besoin |
| 09-consulter-api.md | 8 min | 🟡 Selon besoin |
| 10-exercer-api.md | 8 min | 🟡 Selon besoin |
| 11-logs-api.md | 10 min | 🟡 Selon besoin |
| 12-examples.md | 20 min | 🟢 Référence |
| 13-error-handling.md | 15 min | 🟢 Référence |
| 14-deployment.md | 30 min | 🟢 Production only |

**Total (complet):** ~180 minutes (3 heures)  
**Essentiel (dev):** ~45 minutes (45 min)  
**Essentiel (ops):** ~55 minutes (55 min)

---

## 🆘 Troubleshooting Rapide

**Mon API ne démarre pas?**
→ [02-setup.md](./02-setup.md) - Section Troubleshooting

**Login échoue?**
→ [13-error-handling.md](./13-error-handling.md) - Section 401 Unauthorized

**Erreur 403 sur créer article?**
→ [13-error-handling.md](./13-error-handling.md) - Section 403 Forbidden

**Erreur validation?**
→ [13-error-handling.md](./13-error-handling.md) - Section 400 Bad Request

**Uploader un fichier?**
→ [07-article-api.md](./07-article-api.md) - Section 7️⃣ + [12-examples.md](./12-examples.md)

**Déployer en production?**
→ [14-deployment.md](./14-deployment.md) - Checklist production

---

## 💡 Tips de Navigation

1. **Utilisez Ctrl+F** pour chercher un mot-clé dans le document
2. **Les liens internes** permettent de naviguer entre documents
3. **Les tables de résumé** en fin de document listent tous les endpoints
4. **Les exemples PowerShell** sont prêts à copier-coller
5. **Les emoji** aident à identifier rapidement le type de contenu

---

## 🔗 Ressources Externes

| Ressource | URL |
|-----------|-----|
| Swagger UI (local) | http://localhost:8080/swagger-ui.html |
| OpenAPI Spec (local) | http://localhost:8080/v3/api-docs |
| Collection Bruno | `./CESIZen/` |
| Code Source | `./src/` |

---

## 📞 Questions Fréquentes

**"Par où commencer?"**
→ Lisez le [README principal](../README.md) d'abord

**"Comment faire un login?"**
→ Allez à [04-auth-api.md](./04-auth-api.md) section 1️⃣

**"Je veux voir un exemple de requête?"**
→ Consultez [12-examples.md](./12-examples.md)

**"Comment dépanner une erreur?"**
→ Allez à [13-error-handling.md](./13-error-handling.md)

**"Comment déployer?"**
→ Consultez [14-deployment.md](./14-deployment.md)

---

**Dernière mise à jour**: 2026-04-21  
**Bonne lecture!** 📚

