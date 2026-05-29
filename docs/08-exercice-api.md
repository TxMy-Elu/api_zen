# Exercices API - API Zen

## 🧘 Endpoints Exercices

Base: `/api/exercice`

---

## 1️⃣ Lister les Exercices

Récupérer tous les exercices (PUBLIC).

### Requête

```http
GET /api/exercice/list
```

### Réponse 200 OK

```json
[
  {
    "idExercice": 1,
    "nom": "Respiration Abdominale",
    "dureeInspiration": 4,
    "dureeApnee": 4,
    "dureeExpiration": 6,
    "description": "Technique classique de respiration profonde"
  },
  {
    "idExercice": 2,
    "nom": "4-7-8 Respiration",
    "dureeInspiration": 4,
    "dureeApnee": 7,
    "dureeExpiration": 8,
    "description": "Technique pour calmer l'anxiété et le système nerveux"
  },
  {
    "idExercice": 3,
    "nom": "Box Breathing",
    "dureeInspiration": 4,
    "dureeApnee": 4,
    "dureeExpiration": 4,
    "description": "Respiration en carré, technique militaire"
  }
]
```

---

## 2️⃣ Récupérer un Exercice

Détail d'un exercice spécifique (PUBLIC).

### Requête

```http
GET /api/exercice/{id}
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 200 OK

```json
{
  "idExercice": 1,
  "nom": "Respiration Abdominale",
  "dureeInspiration": 4,
  "dureeApnee": 4,
  "dureeExpiration": 6,
  "description": "Technique classique de respiration profonde"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Exercise not found"
}
```

---

## 3️⃣ Créer un Exercice

Créer un nouvel exercice (ADMIN UNIQUEMENT).

### Requête

```http
POST /api/exercice
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nom": "Cohérence Cardiaque",
  "dureeInspiration": 5,
  "dureeApnee": 0,
  "dureeExpiration": 5,
  "description": "Respiration synchronisée avec le cœur pour l'équilibre"
}
```

### Réponse 201 Created

```json
{
  "idExercice": 6,
  "nom": "Cohérence Cardiaque",
  "dureeInspiration": 5,
  "dureeApnee": 0,
  "dureeExpiration": 5,
  "description": "Respiration synchronisée avec le cœur pour l'équilibre"
}
```

### Réponse 400 Bad Request

Validation échouée:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "nom": "Must not be empty",
    "dureeInspiration": "Must be positive"
  }
}
```

### Réponse 403 Forbidden

Utilisateur non-admin:
```json
{
  "status": 403,
  "message": "Forbidden - Access Denied"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `nom` | String | ✅ | Non vide |
| `dureeInspiration` | Integer | ✅ | Secondes (> 0) |
| `dureeApnee` | Integer | ✅ | Secondes (≥ 0) |
| `dureeExpiration` | Integer | ✅ | Secondes (> 0) |
| `description` | String | ❌ | Optionnel |

---

## 4️⃣ Mettre à Jour un Exercice

Modifier un exercice existant (ADMIN UNIQUEMENT).

### Requête

```http
PUT /api/exercice/{id}
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nom": "Cohérence Cardiaque - Version avancée",
  "dureeInspiration": 6,
  "dureeApnee": 0,
  "dureeExpiration": 6,
  "description": "Respiration avancée synchronisée avec le cœur"
}
```

### Réponse 200 OK

```json
{
  "idExercice": 6,
  "nom": "Cohérence Cardiaque - Version avancée",
  "dureeInspiration": 6,
  "dureeApnee": 0,
  "dureeExpiration": 6,
  "description": "Respiration avancée synchronisée avec le cœur"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Exercise not found"
}
```

---

## 5️⃣ Supprimer un Exercice

Supprimer un exercice (ADMIN UNIQUEMENT).

### Requête

```http
DELETE /api/exercice/{id}
Authorization: Bearer <token-admin>
```

### Réponse 204 No Content

(Aucun body de réponse)

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Exercise not found"
}
```

### Réponse 409 Conflict

Exercice lié à des participations:
```json
{
  "status": 409,
  "message": "Cannot delete exercise - participations are linked"
}
```

Pour supprimer, d'abord supprimer les participations associées (optionnel selon règles métier).

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/exercice/list` | Public | Lister tous |
| GET | `/api/exercice/{id}` | Public | Détail exercice |
| POST | `/api/exercice` | ADMIN | Créer exercice |
| PUT | `/api/exercice/{id}` | ADMIN | Mettre à jour |
| DELETE | `/api/exercice/{id}` | ADMIN | Supprimer |

---

## 🧪 Exemples de Tests

### Lister les exercices (PowerShell)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/list" `
    -Method Get
```

### Récupérer un exercice (PowerShell)

```powershell
$exerciceId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/$exerciceId" `
    -Method Get
```

### Créer un exercice (PowerShell)

```powershell
$token = "<jwt-admin>"

$newExercice = @{
    nom = "Respiration Navale"
    dureeInspiration = 3
    dureeApnee = 3
    dureeExpiration = 3
    description = "Respiration rapide et énergisante"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/exercice" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newExercice

$response
```

### Mettre à jour un exercice (PowerShell)

```powershell
$token = "<jwt-admin>"
$exerciceId = 6

$updateExercice = @{
    nom = "Cohérence Cardiaque Pro"
    dureeInspiration = 5
    dureeApnee = 0
    dureeExpiration = 5
    description = "Version professionnelle pour experts"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/$exerciceId" `
    -Method Put `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $updateExercice
```

### Supprimer un exercice (PowerShell)

```powershell
$token = "<jwt-admin>"
$exerciceId = 6

Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/$exerciceId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 💡 Règles Métier

1. **Durées en secondes**
   - `dureeInspiration`: Combien de temps inspirer (> 0)
   - `dureeApnee`: Combien de temps retenir (≥ 0, optionnel)
   - `dureeExpiration`: Combien de temps expirer (> 0)

2. **Nom obligatoire**
   - Non vide
   - Pas de restriction d'unicité

3. **Description optionnelle**
   - Peut être NULL
   - Format libre

4. **Calcul durée totale**
   - Durée cycle = inspiration + apnée + expiration
   - Exemple 4-7-8: Cycle de 19 secondes

5. **Lecture publique**
   - Tous peuvent voir les exercices
   - Pas de restriction d'accès sur GET

6. **Intégrité référentielle**
   - Impossible de supprimer si participations liées (optionnel)

---

## 🔗 Voir Aussi

- **[Participations](./10-exercer-api.md)** - Suivi des participations aux exercices
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP
- **[Exemples](./12-examples.md)** - Requêtes complètes

---

**Dernière mise à jour**: 2026-04-21

