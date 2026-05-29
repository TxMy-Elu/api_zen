# Logs & Journalisation API - API Zen

## 📊 Endpoints Logs Activité

Base: `/api/log-activite`  
**Authentification**: ADMIN UNIQUEMENT

Les logs d'activité enregistrent automatiquement toutes les opérations de création, modification et suppression via l'AOP Aspect `LogActiviteAspect`.

---

## 1️⃣ Logs Récents

Récupérer les 50 dernières activités.

### Requête

```http
GET /api/log-activite/recent
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  {
    "id": 542,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "article",
    "typeAction": "CREATE",
    "idEnregistrement": 13,
    "anciennesValeurs": null,
    "nouvellesValeurs": "{\"titre\": \"4-7-8 Respiration\", \"contenu\": \"...\"}",
    "dateAction": "2026-04-21T15:30:00Z"
  },
  {
    "id": 541,
    "user": { "id": 2, "email": "user@cesizen.fr", "nom": "Martin", "prenom": "Sophie" },
    "tableConcernee": "exercer",
    "typeAction": "CREATE",
    "idEnregistrement": 10,
    "anciennesValeurs": null,
    "nouvellesValeurs": "{\"userId\": 2, \"exerciceId\": 1}",
    "dateAction": "2026-04-21T14:15:00Z"
  }
]
```

---

## 2️⃣ Lister Tous les Logs

Récupérer tous les logs d'activité.

### Requête

```http
GET /api/log-activite/list
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  { ... 50+ items ... }
]
```

---

## 3️⃣ Logs par Utilisateur

Lister toutes les activités d'un utilisateur spécifique.

### Requête

```http
GET /api/log-activite/user/{userId}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `userId` | Long | ✅ |

### Réponse 200 OK

```json
[
  {
    "id": 542,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "article",
    "typeAction": "CREATE",
    "idEnregistrement": 13,
    "anciennesValeurs": null,
    "nouvellesValeurs": "{...}",
    "dateAction": "2026-04-21T15:30:00Z"
  },
  {
    "id": 538,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "categorie",
    "typeAction": "UPDATE",
    "idEnregistrement": 7,
    "anciennesValeurs": "{\"libelle\": \"Fitness\"}",
    "nouvellesValeurs": "{\"libelle\": \"Fitness & Santé\"}",
    "dateAction": "2026-04-21T12:00:00Z"
  }
]
```

---

## 4️⃣ Logs par Table

Lister toutes les activités concernant une table spécifique.

### Requête

```http
GET /api/log-activite/table/{tableName}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis | Valeurs |
|-----------|------|--------|---------|
| `tableName` | String | ✅ | `user`, `article`, `categorie`, `exercice`, `consulter`, `exercer` |

### Réponse 200 OK

```json
[
  {
    "id": 542,
    "user": { "id": 1, "email": "admin@cesizen.fr" },
    "tableConcernee": "article",
    "typeAction": "CREATE",
    "idEnregistrement": 13,
    "nouvellesValeurs": "{...}",
    "dateAction": "2026-04-21T15:30:00Z"
  },
  {
    "id": 539,
    "user": { "id": 1, "email": "admin@cesizen.fr" },
    "tableConcernee": "article",
    "typeAction": "UPDATE",
    "idEnregistrement": 3,
    "anciennesValeurs": "{...}",
    "nouvellesValeurs": "{...}",
    "dateAction": "2026-04-21T11:45:00Z"
  }
]
```

---

## 5️⃣ Logs par Action

Lister toutes les activités d'un type d'action spécifique.

### Requête

```http
GET /api/log-activite/action/{actionType}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis | Valeurs |
|-----------|------|--------|---------|
| `actionType` | String | ✅ | `CREATE`, `UPDATE`, `DELETE` |

### Réponse 200 OK

```json
[
  {
    "id": 542,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "article",
    "typeAction": "CREATE",
    "idEnregistrement": 13,
    "nouvellesValeurs": "{...}",
    "dateAction": "2026-04-21T15:30:00Z"
  },
  {
    "id": 520,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "categorie",
    "typeAction": "CREATE",
    "idEnregistrement": 7,
    "nouvellesValeurs": "{...}",
    "dateAction": "2026-04-20T10:00:00Z"
  }
]
```

---

## 6️⃣ Historique d'un Élément Spécifique

Voir toutes les modifications d'un enregistrement spécifique.

### Requête

```http
GET /api/log-activite/table/{tableName}/{recordId}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis | Notes |
|-----------|------|--------|-------|
| `tableName` | String | ✅ | Nom de la table |
| `recordId` | Long | ✅ | ID de l'enregistrement |

### Réponse 200 OK

```json
[
  {
    "id": 542,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "article",
    "typeAction": "CREATE",
    "idEnregistrement": 13,
    "anciennesValeurs": null,
    "nouvellesValeurs": "{\"titre\": \"4-7-8 Respiration\", \"typeMedia\": \"text\"}",
    "dateAction": "2026-04-21T15:30:00Z"
  },
  {
    "id": 543,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "tableConcernee": "article",
    "typeAction": "UPDATE",
    "idEnregistrement": 13,
    "anciennesValeurs": "{\"titre\": \"4-7-8 Respiration\", \"typeMedia\": \"text\"}",
    "nouvellesValeurs": "{\"titre\": \"4-7-8 Respiration Technique\", \"typeMedia\": \"text\"}",
    "dateAction": "2026-04-21T16:00:00Z"
  }
]
```

Montre l'historique complet: création → modifications → suppression.

---

## 📊 Logs Connexion

Base: `/api/log-connexion`  
**Authentification**: ADMIN UNIQUEMENT

---

## 1️⃣ Connexions Récentes

Récupérer les 50 dernières connexions.

### Requête

```http
GET /api/log-connexion/recent
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  {
    "id": 1001,
    "email": "admin@cesizen.fr",
    "ip": "192.168.1.100",
    "reussi": true,
    "dateConnexion": "2026-04-21T15:30:00Z"
  },
  {
    "id": 1000,
    "email": "user@cesizen.fr",
    "ip": "192.168.1.101",
    "reussi": true,
    "dateConnexion": "2026-04-21T14:15:00Z"
  },
  {
    "id": 999,
    "email": "fake@gmail.com",
    "ip": "203.0.113.45",
    "reussi": false,
    "dateConnexion": "2026-04-21T13:00:00Z"
  }
]
```

---

## 2️⃣ Lister Toutes les Connexions

### Requête

```http
GET /api/log-connexion/list
Authorization: Bearer <token-admin>
```

---

## 3️⃣ Connexions par Email

Lister toutes les tentatives de connexion pour un email.

### Requête

```http
GET /api/log-connexion/email/{email}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `email` | String | ✅ |

### Réponse 200 OK

```json
[
  {
    "id": 1001,
    "email": "admin@cesizen.fr",
    "ip": "192.168.1.100",
    "reussi": true,
    "dateConnexion": "2026-04-21T15:30:00Z"
  },
  {
    "id": 980,
    "email": "admin@cesizen.fr",
    "ip": "192.168.1.105",
    "reussi": true,
    "dateConnexion": "2026-04-20T10:00:00Z"
  }
]
```

---

## 4️⃣ Connexions Réussies

Lister toutes les connexions réussies.

### Requête

```http
GET /api/log-connexion/success
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  {
    "id": 1001,
    "email": "admin@cesizen.fr",
    "ip": "192.168.1.100",
    "reussi": true,
    "dateConnexion": "2026-04-21T15:30:00Z"
  },
  {
    "id": 1000,
    "email": "user@cesizen.fr",
    "ip": "192.168.1.101",
    "reussi": true,
    "dateConnexion": "2026-04-21T14:15:00Z"
  }
]
```

---

## 5️⃣ Connexions Échouées

Lister toutes les tentatives de connexion échouées.

### Requête

```http
GET /api/log-connexion/failed
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  {
    "id": 999,
    "email": "fake@gmail.com",
    "ip": "203.0.113.45",
    "reussi": false,
    "dateConnexion": "2026-04-21T13:00:00Z"
  },
  {
    "id": 998,
    "email": "admin@cesizen.fr",
    "ip": "203.0.113.50",
    "reussi": false,
    "dateConnexion": "2026-04-21T12:45:00Z"
  }
]
```

Détecte les tentatives de brute-force.

---

## 6️⃣ Connexions par IP

Lister toutes les connexions d'une adresse IP spécifique.

### Requête

```http
GET /api/log-connexion/ip/{ipAddress}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `ipAddress` | String | ✅ |

### Réponse 200 OK

```json
[
  {
    "id": 1001,
    "email": "admin@cesizen.fr",
    "ip": "192.168.1.100",
    "reussi": true,
    "dateConnexion": "2026-04-21T15:30:00Z"
  },
  {
    "id": 998,
    "email": "user2@cesizen.fr",
    "ip": "192.168.1.100",
    "reussi": true,
    "dateConnexion": "2026-04-21T12:00:00Z"
  }
]
```

---

## 📊 Résumé des Endpoints

### Logs Activité

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/log-activite/recent` | ADMIN | 50 dernières activités |
| GET | `/api/log-activite/list` | ADMIN | Toutes les activités |
| GET | `/api/log-activite/user/{userId}` | ADMIN | Par utilisateur |
| GET | `/api/log-activite/table/{tableName}` | ADMIN | Par table |
| GET | `/api/log-activite/action/{actionType}` | ADMIN | Par action (CREATE/UPDATE/DELETE) |
| GET | `/api/log-activite/table/{tableName}/{recordId}` | ADMIN | Historique d'un élément |

### Logs Connexion

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/log-connexion/recent` | ADMIN | 50 dernières connexions |
| GET | `/api/log-connexion/list` | ADMIN | Toutes les connexions |
| GET | `/api/log-connexion/email/{email}` | ADMIN | Par email |
| GET | `/api/log-connexion/success` | ADMIN | Connexions réussies |
| GET | `/api/log-connexion/failed` | ADMIN | Tentatives échouées |
| GET | `/api/log-connexion/ip/{ipAddress}` | ADMIN | Par adresse IP |

---

## 🧪 Exemples de Tests

### Voir les derniers logs (PowerShell)

```powershell
$token = "<jwt-admin>"

Invoke-RestMethod -Uri "http://localhost:8080/api/log-activite/recent" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Voir les logs d'une table (PowerShell)

```powershell
$token = "<jwt-admin>"

Invoke-RestMethod -Uri "http://localhost:8080/api/log-activite/table/article" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Voir l'historique d'un article (PowerShell)

```powershell
$token = "<jwt-admin>"
$articleId = 13

Invoke-RestMethod -Uri "http://localhost:8080/api/log-activite/table/article/$articleId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Voir les connexions échouées (PowerShell)

```powershell
$token = "<jwt-admin>"

Invoke-RestMethod -Uri "http://localhost:8080/api/log-connexion/failed" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Voir les connexions d'un IP (PowerShell)

```powershell
$token = "<jwt-admin>"
$ip = "192.168.1.100"

Invoke-RestMethod -Uri "http://localhost:8080/api/log-connexion/ip/$ip" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 💡 Aspects Techniques

### LogActiviteAspect
- Capture automatiquement les appels `create()`, `update()`, `delete()` des services
- Enregistre les anciennes et nouvelles valeurs en JSON
- Exclut `LogActiviteService` pour éviter les boucles infinies
- Inclut l'utilisateur actuel (du SecurityContext)

### LogConnexion
- Enregistrée lors de chaque tentative de login (succès/échec)
- Capture l'adresse IP du client
- Utile pour détecter les brute-force attacks

### Stockage
- Logs conservés en base de données (MySQL)
- Aucune limite de rétention (à configurer en prod)
- Pas de suppression automatique

---

## 🔗 Voir Aussi

- **[Sécurité](./03-security.md)** - Contrôle d'accès ADMIN
- **[Utilisateurs](./05-user-api.md)** - Utilisateurs créant les actions
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

