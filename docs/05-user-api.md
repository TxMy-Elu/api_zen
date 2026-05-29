# Utilisateurs & Rôles API - API Zen

## 👥 Endpoints Utilisateurs

Base: `/api/user`  
**Authentification**: JWT requis (ROLE_ADMIN uniquement)

---

## 1️⃣ Lister les Utilisateurs

Récupérer tous les utilisateurs.

### Requête

```http
GET /api/user/list
Authorization: Bearer <token-admin>
```

### Réponse 200 OK

```json
[
  {
    "id": 1,
    "email": "admin@cesizen.fr",
    "nom": "Dupont",
    "prenom": "Jean",
    "active": true,
    "creationDate": "2026-01-15T10:30:00Z",
    "roleId": 1,
    "roleLibelle": "ROLE_ADMIN"
  },
  {
    "id": 2,
    "email": "user@cesizen.fr",
    "nom": "Martin",
    "prenom": "Sophie",
    "active": true,
    "creationDate": "2026-01-20T14:15:00Z",
    "roleId": 2,
    "roleLibelle": "ROLE_USER"
  }
]
```

### Réponse 403 Forbidden

Utilisateur non-admin:
```json
{
  "status": 403,
  "message": "Forbidden - Access Denied"
}
```

---

## 2️⃣ Récupérer un Utilisateur

Détail d'un utilisateur spécifique.

### Requête

```http
GET /api/user/{id}
Authorization: Bearer <token-admin>
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 200 OK

```json
{
  "id": 1,
  "email": "admin@cesizen.fr",
  "nom": "Dupont",
  "prenom": "Jean",
  "active": true,
  "creationDate": "2026-01-15T10:30:00Z",
  "roleId": 1,
  "roleLibelle": "ROLE_ADMIN"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "User not found"
}
```

---

## 3️⃣ Créer un Utilisateur

Créer un nouvel utilisateur avec un rôle spécifique.

### Requête

```http
POST /api/user
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nom": "Leclerc",
  "prenom": "Marc",
  "email": "marc.leclerc@cesizen.fr",
  "password": "SecurePass123!",
  "roleId": 2
}
```

### Réponse 201 Created

```json
{
  "id": 42,
  "email": "marc.leclerc@cesizen.fr",
  "nom": "Leclerc",
  "prenom": "Marc",
  "active": true,
  "creationDate": "2026-04-21T12:00:00Z",
  "roleId": 2,
  "roleLibelle": "ROLE_USER"
}
```

### Réponse 400 Bad Request

Email en doublon:
```json
{
  "status": 400,
  "message": "Email already exists"
}
```

Validation:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "email": "Invalid email format",
    "nom": "Must not be empty",
    "roleId": "Invalid role"
  }
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `nom` | String | ✅ | Non vide |
| `prenom` | String | ✅ | Non vide |
| `email` | String | ✅ | Email valide, unique |
| `password` | String | ✅ | Min 6 caractères |
| `roleId` | Long | ✅ | ID du rôle existant (1 ou 2) |

---

## 4️⃣ Mettre à Jour un Utilisateur

Modifier les informations d'un utilisateur.

### Requête

```http
PUT /api/user/{id}
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nom": "Leclerc",
  "prenom": "Marc",
  "email": "marc.leclerc@cesizen.fr",
  "password": "NewPassword123!",
  "roleId": 1
}
```

### Réponse 200 OK

```json
{
  "id": 42,
  "email": "marc.leclerc@cesizen.fr",
  "nom": "Leclerc",
  "prenom": "Marc",
  "active": true,
  "creationDate": "2026-04-21T12:00:00Z",
  "roleId": 1,
  "roleLibelle": "ROLE_ADMIN"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "User not found"
}
```

### Réponse 400 Bad Request

Email déjà utilisé par quelqu'un d'autre:
```json
{
  "status": 400,
  "message": "Email already exists"
}
```

### DTOs

Mêmes champs que Create (tous optionnels pour les updates complètes).

---

## 5️⃣ Supprimer un Utilisateur

Supprimer un utilisateur de la base.

### Requête

```http
DELETE /api/user/{id}
Authorization: Bearer <token-admin>
```

### Réponse 204 No Content

(Aucun body de réponse)

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "User not found"
}
```

### Réponse 409 Conflict

Impossible de supprimer (liens intégrité):
```json
{
  "status": 409,
  "message": "Cannot delete user - foreign key constraint"
}
```

---

## 🎭 Endpoints Rôles

Base: `/api/role`  
**Authentification**: JWT requis

---

## 1️⃣ Lister les Rôles

Récupérer tous les rôles disponibles.

### Requête

```http
GET /api/role/list
Authorization: Bearer <token>
```

### Réponse 200 OK

```json
[
  {
    "id": 1,
    "libelle": "ROLE_ADMIN"
  },
  {
    "id": 2,
    "libelle": "ROLE_USER"
  }
]
```

### Rôles existants

| ID | Libelle | Description |
|----|------------|-------------|
| 1 | `ROLE_ADMIN` | Accès complet (gestion users, logs, écritures contenu) |
| 2 | `ROLE_USER` | Accès limité (lecture contenu, participation) |

---

## 🧪 Exemples de Tests

### Lister les utilisateurs (PowerShell)

```powershell
$token = "<jwt-admin>"

Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Créer un utilisateur (PowerShell)

```powershell
$token = "<jwt-admin>"

$newUser = @{
    nom = "Bernard"
    prenom = "Carole"
    email = "carole.bernard@cesizen.fr"
    password = "Password123!"
    roleId = 2  # ROLE_USER
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/user" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newUser

$response
```

### Mettre à jour un utilisateur (PowerShell)

```powershell
$token = "<jwt-admin>"
$userId = 42

$updateUser = @{
    nom = "Bernard"
    prenom = "Carole"
    email = "carole.bernard.updated@cesizen.fr"
    password = "NewPassword123!"
    roleId = 1  # Promotion admin
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/user/$userId" `
    -Method Put `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $updateUser
```

### Supprimer un utilisateur (PowerShell)

```powershell
$token = "<jwt-admin>"
$userId = 42

Invoke-RestMethod -Uri "http://localhost:8080/api/user/$userId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $token" }
```

### Lister les rôles (PowerShell)

```powershell
$token = "<jwt>"

Invoke-RestMethod -Uri "http://localhost:8080/api/role/list" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/user/list` | ADMIN | Lister tous |
| GET | `/api/user/{id}` | ADMIN | Détail utilisateur |
| POST | `/api/user` | ADMIN | Créer utilisateur |
| PUT | `/api/user/{id}` | ADMIN | Mettre à jour |
| DELETE | `/api/user/{id}` | ADMIN | Supprimer |
| GET | `/api/role/list` | JWT requis | Lister rôles |

---

## 🔐 Bonnes Pratiques

1. **Seul un admin peut gérer les users**
   - `/api/user/**` nécessite `ROLE_ADMIN`

2. **Emails uniques**
   - Impossible d'avoir 2 users avec le même email

3. **Mot de passe toujours hashé (BCrypt)**
   - Jamais stocké en clair en base

4. **Rôles immuables**
   - `ROLE_ADMIN` et `ROLE_USER` prédéfinis
   - Pas d'API pour créer/supprimer rôles

5. **Active flag**
   - Optionnel pour soft-delete (non implémenté actuellement)

---

## 🔗 Voir Aussi

- **[Sécurité](./03-security.md)** - Modèle RBAC
- **[Authentification](./04-auth-api.md)** - Login, register
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

