# Catégories API - API Zen

## 📂 Endpoints Catégories

Base: `/api/categorie`

---

## 1️⃣ Lister les Catégories

Récupérer tous les catégories (PUBLIC).

### Requête

```http
GET /api/categorie/list
```

### Réponse 200 OK

```json
[
  {
    "idCategorie": 1,
    "libelle": "Respiration",
    "description": "Techniques de respiration et méditation"
  },
  {
    "idCategorie": 2,
    "libelle": "Sommeil",
    "description": "Ressources pour améliorer le sommeil"
  },
  {
    "idCategorie": 3,
    "libelle": "Stress",
    "description": "Gestion du stress et relaxation"
  }
]
```

---

## 2️⃣ Récupérer une Catégorie

Détail d'une catégorie spécifique (PUBLIC).

### Requête

```http
GET /api/categorie/{id}
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 200 OK

```json
{
  "idCategorie": 1,
  "libelle": "Respiration",
  "description": "Techniques de respiration et méditation"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Category not found"
}
```

---

## 3️⃣ Créer une Catégorie

Créer une nouvelle catégorie (ADMIN UNIQUEMENT).

### Requête

```http
POST /api/categorie
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "libelle": "Nutrition",
  "description": "Conseils nutritionnels et bien-être"
}
```

### Réponse 201 Created

```json
{
  "idCategorie": 7,
  "libelle": "Nutrition",
  "description": "Conseils nutritionnels et bien-être"
}
```

### Réponse 400 Bad Request

Libelle manquant:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "libelle": "Must not be empty"
  }
}
```

Libelle en doublon:
```json
{
  "status": 400,
  "message": "Category label already exists"
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
| `libelle` | String | ✅ | Non vide, unique |
| `description` | String | ❌ | Optionnel |

---

## 4️⃣ Mettre à Jour une Catégorie

Modifier une catégorie existante (ADMIN UNIQUEMENT).

### Requête

```http
PUT /api/categorie/{id}
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "libelle": "Nutrition & Santé",
  "description": "Conseils nutritionnels et santé globale"
}
```

### Réponse 200 OK

```json
{
  "idCategorie": 7,
  "libelle": "Nutrition & Santé",
  "description": "Conseils nutritionnels et santé globale"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Category not found"
}
```

### Réponse 409 Conflict

Libelle en doublon:
```json
{
  "status": 409,
  "message": "Category label already exists"
}
```

---

## 5️⃣ Supprimer une Catégorie

Supprimer une catégorie (ADMIN UNIQUEMENT).

### Requête

```http
DELETE /api/categorie/{id}
Authorization: Bearer <token-admin>
```

### Réponse 204 No Content

(Aucun body de réponse)

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Category not found"
}
```

### Réponse 409 Conflict

Catégorie liée à des articles:
```json
{
  "status": 409,
  "message": "Cannot delete category - articles are linked"
}
```

Pour supprimer, d'abord supprimer les articles associés.

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/categorie/list` | Public | Lister toutes |
| GET | `/api/categorie/{id}` | Public | Détail catégorie |
| POST | `/api/categorie` | ADMIN | Créer catégorie |
| PUT | `/api/categorie/{id}` | ADMIN | Mettre à jour |
| DELETE | `/api/categorie/{id}` | ADMIN | Supprimer |

---

## 🧪 Exemples de Tests

### Lister les catégories (PowerShell)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/list" `
    -Method Get
```

### Récupérer une catégorie (PowerShell)

```powershell
$categoryId = 1

Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/$categoryId" `
    -Method Get
```

### Créer une catégorie (PowerShell)

```powershell
$token = "<jwt-admin>"

$newCategory = @{
    libelle = "Fitness"
    description = "Exercices et entraînement physique"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/categorie" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newCategory

$response
```

### Mettre à jour une catégorie (PowerShell)

```powershell
$token = "<jwt-admin>"
$categoryId = 7

$updateCategory = @{
    libelle = "Fitness & Sports"
    description = "Exercices, entraînement physique et sports"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/$categoryId" `
    -Method Put `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $updateCategory
```

### Supprimer une catégorie (PowerShell)

```powershell
$token = "<jwt-admin>"
$categoryId = 7

Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/$categoryId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 💡 Règles Métier

1. **Libelle unique**
   - Deux catégories ne peuvent pas avoir le même libelle

2. **Lecture publique**
   - Tout le monde peut lire les catégories
   - Pas de restrictions d'accès sur GET

3. **Écritures admin uniquement**
   - POST, PUT, DELETE réservés aux ROLE_ADMIN

4. **Intégrité référentielle**
   - Impossible de supprimer une catégorie si articles liés
   - Foreign key constraint en base

5. **Description optionnelle**
   - Libelle obligatoire
   - Description facultative (peut être NULL)

---

## 🔗 Voir Aussi

- **[Articles](./07-article-api.md)** - Articles liés aux catégories
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP
- **[Exemples](./12-examples.md)** - Requêtes complètes

---

**Dernière mise à jour**: 2026-04-21

