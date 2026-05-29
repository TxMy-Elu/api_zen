# Consultations API - API Zen

## 👀 Endpoints Consultations

Base: `/api/consulter`  
**Authentification**: JWT requis

---

## 1️⃣ Lister les Consultations

Récupérer toutes les consultations d'articles.

### Requête

```http
GET /api/consulter/list
Authorization: Bearer <token>
```

### Réponse 200 OK

```json
[
  {
    "idConsulter": 1,
    "idUtilisateur": 1,
    "nomUtilisateur": "Jean Dupont",
    "idArticle": 1,
    "titreArticle": "Comprendre la respiration abdominale",
    "viewedAt": "2026-04-20T10:30:00Z"
  },
  {
    "idConsulter": 2,
    "idUtilisateur": 2,
    "nomUtilisateur": "Sophie Martin",
    "idArticle": 3,
    "titreArticle": "Méditation du soir",
    "viewedAt": "2026-04-21T08:15:00Z"
  }
]
```

---

## 2️⃣ Consultations par Utilisateur

Lister toutes les consultations d'un utilisateur.

### Requête

```http
GET /api/consulter/user/{userId}
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `userId` | Long | ✅ |

### Réponse 200 OK

```json
[
  {
    "idConsulter": 1,
    "idUtilisateur": 1,
    "nomUtilisateur": "Jean Dupont",
    "idArticle": 1,
    "titreArticle": "Comprendre la respiration abdominale",
    "viewedAt": "2026-04-20T10:30:00Z"
  },
  {
    "idConsulter": 5,
    "idUtilisateur": 1,
    "nomUtilisateur": "Jean Dupont",
    "idArticle": 2,
    "titreArticle": "4-7-8 Respiration Technique",
    "viewedAt": "2026-04-21T09:00:00Z"
  }
]
```

---

## 3️⃣ Consultations par Article

Lister toutes les consultations d'un article.

### Requête

```http
GET /api/consulter/article/{articleId}
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `articleId` | Long | ✅ |

### Réponse 200 OK

```json
[
  {
    "idConsulter": 1,
    "idUtilisateur": 1,
    "nomUtilisateur": "Jean Dupont",
    "idArticle": 1,
    "titreArticle": "Comprendre la respiration abdominale",
    "viewedAt": "2026-04-20T10:30:00Z"
  },
  {
    "idConsulter": 7,
    "idUtilisateur": 3,
    "nomUtilisateur": "Carole Bernard",
    "idArticle": 1,
    "titreArticle": "Comprendre la respiration abdominale",
    "viewedAt": "2026-04-21T11:45:00Z"
  }
]
```

---

## 4️⃣ Nombre de Vues d'un Article

Compter combien de fois un article a été consulté.

### Requête

```http
GET /api/consulter/article/{articleId}/count
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `articleId` | Long | ✅ |

### Réponse 200 OK

```json
{
  "articleId": 1,
  "articleTitle": "Comprendre la respiration abdominale",
  "viewCount": 12
}
```

---

## 5️⃣ Nombre d'Articles Consultés par Utilisateur

Compter combien d'articles un utilisateur a consulté.

### Requête

```http
GET /api/consulter/user/{userId}/count
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `userId` | Long | ✅ |

### Réponse 200 OK

```json
{
  "userId": 1,
  "userName": "Jean Dupont",
  "articleCount": 5
}
```

---

## 6️⃣ Créer une Consultation

Enregistrer qu'un utilisateur a consulté un article.

### Requête

```http
POST /api/consulter
Authorization: Bearer <token>
Content-Type: application/json

{
  "idUtilisateur": 1,
  "idArticle": 3
}
```

### Réponse 201 Created

```json
{
  "idConsulter": 23,
  "idUtilisateur": 1,
  "nomUtilisateur": "Jean Dupont",
  "idArticle": 3,
  "titreArticle": "Méditation du soir",
  "viewedAt": "2026-04-21T12:30:00Z"
}
```

### Réponse 400 Bad Request

Validation échouée:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "idUtilisateur": "Must not be null",
    "idArticle": "Must not be null"
  }
}
```

### Réponse 404 Not Found

Utilisateur ou article introuvable:
```json
{
  "status": 404,
  "message": "User or article not found"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `idUtilisateur` | Long | ✅ | ID utilisateur existant |
| `idArticle` | Long | ✅ | ID article existant |

---

## 7️⃣ Supprimer une Consultation

Supprimer une consultation spécifique.

### Requête

```http
DELETE /api/consulter/{id}
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 204 No Content

(Aucun body de réponse)

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Consultation not found"
}
```

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/consulter/list` | JWT | Lister toutes |
| GET | `/api/consulter/user/{userId}` | JWT | Par utilisateur |
| GET | `/api/consulter/article/{articleId}` | JWT | Par article |
| GET | `/api/consulter/article/{articleId}/count` | JWT | Compteur vues article |
| GET | `/api/consulter/user/{userId}/count` | JWT | Compteur articles user |
| POST | `/api/consulter` | JWT | Créer consultation |
| DELETE | `/api/consulter/{id}` | JWT | Supprimer consultation |

---

## 🧪 Exemples de Tests

### Enregistrer une consultation (PowerShell)

```powershell
$token = "<jwt>"

$newConsultation = @{
    idUtilisateur = 1
    idArticle = 3
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/consulter" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newConsultation

$response
```

### Lister les consultations d'un utilisateur (PowerShell)

```powershell
$token = "<jwt>"
$userId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/user/$userId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Compter les vues d'un article (PowerShell)

```powershell
$token = "<jwt>"
$articleId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/article/$articleId/count" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Compter les articles lus par un utilisateur (PowerShell)

```powershell
$token = "<jwt>"
$userId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/user/$userId/count" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Supprimer une consultation (PowerShell)

```powershell
$token = "<jwt>"
$consultationId = 23

Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/$consultationId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 💡 Règles Métier

1. **Enregistrement automatique**
   - Les consultations doivent être enregistrées manuellement via POST
   - Pas de tracking automatique au moment du GET article

2. **Timestamp automatique**
   - `viewedAt` défini au moment de la création (NOW())
   - Pas de possibilité de le modifier

3. **Utilisateur & article obligatoires**
   - Les deux ForeignKeys doivent être valides
   - Erreur 404 si introuvables

4. **Pas de doublon physique**
   - Possible d'enregistrer 2 fois la même consultation
   - Utile pour tracker les relectures

5. **Authentification requise**
   - Tous les endpoints demandent un JWT valide
   - Pas d'accès public

---

## 🔗 Voir Aussi

- **[Articles](./07-article-api.md)** - Articles consultés
- **[Utilisateurs](./05-user-api.md)** - Utilisateurs qui consultent
- **[Participations](./10-exercer-api.md)** - Suivi participation exercices
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

