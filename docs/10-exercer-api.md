# Participations Exercices API - API Zen

## 🏋️ Endpoints Participations

Base: `/api/exercer`  
**Authentification**: JWT requis

---

## 1️⃣ Lister les Participations

Récupérer toutes les participations aux exercices.

### Requête

```http
GET /api/exercer/list
Authorization: Bearer <token>
```

### Réponse 200 OK

```json
[
  {
    "idExercer": 1,
    "user": {
      "id": 1,
      "email": "admin@cesizen.fr",
      "nom": "Dupont",
      "prenom": "Jean"
    },
    "exercice": {
      "idExercice": 1,
      "nom": "Respiration Abdominale",
      "dureeInspiration": 4,
      "dureeApnee": 4,
      "dureeExpiration": 6
    },
    "completedAt": "2026-04-20T14:30:00Z"
  },
  {
    "idExercer": 2,
    "user": {
      "id": 2,
      "email": "user@cesizen.fr",
      "nom": "Martin",
      "prenom": "Sophie"
    },
    "exercice": {
      "idExercice": 2,
      "nom": "4-7-8 Respiration",
      "dureeInspiration": 4,
      "dureeApnee": 7,
      "dureeExpiration": 8
    },
    "completedAt": "2026-04-21T09:00:00Z"
  }
]
```

---

## 2️⃣ Lister les Participations (Public)

Alias pour /list.

### Requête

```http
GET /api/exercer/public
Authorization: Bearer <token>
```

---

## 3️⃣ Participations par Utilisateur

Lister toutes les participations d'un utilisateur.

### Requête

```http
GET /api/exercer/user/{userId}
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
    "idExercer": 1,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "exercice": { "idExercice": 1, "nom": "Respiration Abdominale", ... },
    "completedAt": "2026-04-20T14:30:00Z"
  },
  {
    "idExercer": 3,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "exercice": { "idExercice": 2, "nom": "4-7-8 Respiration", ... },
    "completedAt": "2026-04-20T15:15:00Z"
  }
]
```

---

## 4️⃣ Participations par Exercice

Lister toutes les participations pour un exercice.

### Requête

```http
GET /api/exercer/exercice/{exerciceId}
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `exerciceId` | Long | ✅ |

### Réponse 200 OK

```json
[
  {
    "idExercer": 1,
    "user": { "id": 1, "email": "admin@cesizen.fr", "nom": "Dupont", "prenom": "Jean" },
    "exercice": { "idExercice": 1, "nom": "Respiration Abdominale", ... },
    "completedAt": "2026-04-20T14:30:00Z"
  },
  {
    "idExercer": 5,
    "user": { "id": 3, "email": "user3@cesizen.fr", "nom": "Bernard", "prenom": "Carole" },
    "exercice": { "idExercice": 1, "nom": "Respiration Abdominale", ... },
    "completedAt": "2026-04-21T10:00:00Z"
  }
]
```

---

## 5️⃣ Détail d'une Participation

Récupérer une participation spécifique.

### Requête

```http
GET /api/exercer/{id}
Authorization: Bearer <token>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 200 OK

```json
{
  "idExercer": 1,
  "user": {
    "id": 1,
    "email": "admin@cesizen.fr",
    "nom": "Dupont",
    "prenom": "Jean"
  },
  "exercice": {
    "idExercice": 1,
    "nom": "Respiration Abdominale",
    "dureeInspiration": 4,
    "dureeApnee": 4,
    "dureeExpiration": 6,
    "description": "Technique classique de respiration profonde"
  },
  "completedAt": "2026-04-20T14:30:00Z"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Participation not found"
}
```

---

## 6️⃣ Créer une Participation

Enregistrer qu'un utilisateur a participé à un exercice.

### Requête Simple (Date courante)

```http
POST /api/exercer
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "exerciceId": 2
}
```

### Requête avec Timestamp personnalisé

```http
POST /api/exercer
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "exerciceId": 2,
  "completedAt": "2026-04-21T10:30:00Z"
}
```

### Réponse 201 Created

```json
{
  "idExercer": 10,
  "user": {
    "id": 1,
    "email": "admin@cesizen.fr",
    "nom": "Dupont",
    "prenom": "Jean"
  },
  "exercice": {
    "idExercice": 2,
    "nom": "4-7-8 Respiration",
    "dureeInspiration": 4,
    "dureeApnee": 7,
    "dureeExpiration": 8,
    "description": "Technique pour calmer l'anxiété"
  },
  "completedAt": "2026-04-21T10:30:00Z"
}
```

### Réponse 400 Bad Request

Validation échouée:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "userId": "Must not be null",
    "exerciceId": "Must not be null"
  }
}
```

Timestamp invalide:
```json
{
  "status": 400,
  "message": "Invalid timestamp format. Use ISO-8601: yyyy-MM-dd'T'HH:mm:ss'Z'"
}
```

### Réponse 404 Not Found

Utilisateur ou exercice introuvable:
```json
{
  "status": 404,
  "message": "User or exercise not found"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `userId` | Long | ✅ | ID utilisateur existant |
| `exerciceId` | Long | ✅ | ID exercice existant |
| `completedAt` | String (ISO-8601) | ❌ | Format: `2026-04-21T10:30:00Z` |

---

## 7️⃣ Supprimer une Participation

Supprimer une participation spécifique.

### Requête

```http
DELETE /api/exercer/{id}
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
  "message": "Participation not found"
}
```

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/exercer/list` | JWT | Lister toutes |
| GET | `/api/exercer/public` | JWT | Alias pour list |
| GET | `/api/exercer/user/{userId}` | JWT | Par utilisateur |
| GET | `/api/exercer/exercice/{exerciceId}` | JWT | Par exercice |
| GET | `/api/exercer/{id}` | JWT | Détail participation |
| POST | `/api/exercer` | JWT | Créer participation |
| DELETE | `/api/exercer/{id}` | JWT | Supprimer participation |

---

## 🧪 Exemples de Tests

### Enregistrer une participation (PowerShell)

```powershell
$token = "<jwt>"

$newParticipation = @{
    userId = 1
    exerciceId = 2
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/exercer" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newParticipation

$response
```

### Avec timestamp personnalisé (PowerShell)

```powershell
$token = "<jwt>"

$newParticipation = @{
    userId = 1
    exerciceId = 2
    completedAt = "2026-04-21T10:30:00Z"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/exercer" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newParticipation
```

### Lister les participations d'un utilisateur (PowerShell)

```powershell
$token = "<jwt>"
$userId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/exercer/user/$userId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Lister les participations à un exercice (PowerShell)

```powershell
$token = "<jwt>"
$exerciceId = 2

Invoke-RestMethod -Uri "http://localhost:8080/api/exercer/exercice/$exerciceId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Supprimer une participation (PowerShell)

```powershell
$token = "<jwt>"
$participationId = 10

Invoke-RestMethod -Uri "http://localhost:8080/api/exercer/$participationId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 💡 Règles Métier

1. **Timestamp auto**
   - Si `completedAt` omis, utilise NOW()
   - Si fourni, doit être format ISO-8601 valide

2. **Utilisateur & exercice obligatoires**
   - Les deux ForeignKeys doivent être valides
   - Erreur 404 si introuvables

3. **Pas de doublon physique**
   - Possible de créer 2 participations identiques
   - Utile pour tracker les répétitions

4. **Authentification requise**
   - Tous les endpoints demandent un JWT valide
   - Pas d'accès public

5. **Pas de restrictions d'accès utilisateur**
   - Un user peut enregistrer ses propres participations
   - Un admin peut enregistrer pour n'importe quel user (optionnel selon règles métier)

---

## 🔗 Voir Aussi

- **[Exercices](./08-exercice-api.md)** - Exercices disponibles
- **[Utilisateurs](./05-user-api.md)** - Utilisateurs participant
- **[Consultations](./09-consulter-api.md)** - Suivi consultation articles
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

