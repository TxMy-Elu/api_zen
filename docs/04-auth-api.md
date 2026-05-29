# Authentification API - API Zen

## 🔐 Endpoints d'Authentification

Base: `/api/auth`

---

## 1️⃣ Login

Authentifier un utilisateur et obtenir un JWT.

### Requête

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@cesizen.fr",
  "password": "password"
}
```

### Réponse 200 OK

```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "admin@cesizen.fr",
  "nom": "Dupont",
  "prenom": "Jean",
  "role": "ROLE_ADMIN"
}
```

### Réponse 401 Unauthorized

Email ou mot de passe incorrect:
```json
{
  "status": 401,
  "message": "Invalid email or password"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `email` | String | ✅ | Format email |
| `password` | String | ✅ | Min 6 caractères |

---

## 2️⃣ Register

Créer un nouvel utilisateur avec rôle ROLE_USER.

### Requête

```http
POST /api/auth/register
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@gmail.com",
  "password": "SecurePass123!"
}
```

### Réponse 201 Created

```json
{
  "message": "User registered successfully",
  "userId": 42,
  "email": "jean.dupont@gmail.com"
}
```

### Réponse 400 Bad Request

Email déjà utilisé:
```json
{
  "status": 400,
  "message": "Email already exists"
}
```

Validation échouée:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
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

### Processus

1. Validation des données
2. Hachage du mot de passe (BCrypt)
3. Création du user avec `ROLE_USER`
4. Envoi email de bienvenue (optionnel)
5. Retour 201

---

## 3️⃣ Forgot Password

Demander un reset de mot de passe.

### Requête

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "admin@cesizen.fr"
}
```

### Réponse 200 OK

```json
{
  "message": "Password reset email sent",
  "email": "admin@cesizen.fr"
}
```

### Réponse 404 Not Found

Email introuvable:
```json
{
  "status": 404,
  "message": "User not found"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `email` | String | ✅ | Email valide |

### Processus

1. Chercher l'utilisateur par email
2. Générer un token UUID (1 heure de validité)
3. Envoyer email avec lien reset (contient le token)
4. Token stocké en base avec timestamp

---

## 4️⃣ Validate Reset Token

Vérifier qu'un token reset est valide.

### Requête

```http
GET /api/auth/reset-password/validate?token=<uuid>
```

### Réponse 200 OK

Token valide:
```json
{
  "valid": true,
  "message": "Token is valid"
}
```

### Réponse 400 Bad Request

Token expiré ou invalide:
```json
{
  "status": 400,
  "message": "Invalid or expired token"
}
```

### Paramètres

| Paramètre | Type | Requis | Notes |
|-----------|------|--------|-------|
| `token` | String (UUID) | ✅ | Token reçu par email |

---

## 5️⃣ Reset Password

Réinitialiser le mot de passe avec un token valide.

### Requête

```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "NewSecurePass123!"
}
```

### Réponse 200 OK

```json
{
  "message": "Password reset successfully",
  "email": "admin@cesizen.fr"
}
```

### Réponse 400 Bad Request

Token invalide/expiré:
```json
{
  "status": 400,
  "message": "Invalid or expired token"
}
```

Validation mot de passe:
```json
{
  "status": 400,
  "message": "Password must be at least 6 characters"
}
```

### DTOs

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `token` | String (UUID) | ✅ | Token de reset |
| `newPassword` | String | ✅ | Min 6 caractères |

### Processus

1. Valider le token (existe et n'a pas expiré)
2. Valider le nouveau mot de passe
3. Hacher le mot de passe (BCrypt)
4. Mettre à jour l'utilisateur
5. Invalider le token
6. Retour 200

---

## 📊 Flux d'Authentification Complet

```
Client Login
    ↓
POST /api/auth/login
    ↓
Server: Valider email/password
    ↓
Server: Générer JWT token
    ↓
Response: 200 + token
    ↓
Client: Stocke token (localStorage, sessionStorage, etc.)
    ↓
Client: Inclut "Authorization: Bearer <token>" dans futures requêtes
    ↓
Server: JwtFilter valide le token
    ↓
Server: Traite la requête
```

---

## 📧 Emails Envoyés

### À la création (Register)

**Subject**: Bienvenue sur CESIZen  
**Contenu**: HTML + texte brut  
**Info**: Email, nom, prenom  

### À la demande reset (Forgot Password)

**Subject**: Réinitialiser votre mot de passe  
**Contenu**: Lien de reset avec token valide 1h  
**Info**: Email, URL callback  

### À la réussite (Reset Password)

**Subject**: Mot de passe réinitialisé  
**Contenu**: Confirmation succès  
**Info**: Email, date  

---

## 🧪 Exemples de Tests

### Test Login Admin (PowerShell)

```powershell
$login = @{
    email = "admin@cesizen.fr"
    password = "password"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $login

$response
$token = $response.token
```

### Test Register (PowerShell)

```powershell
$register = @{
    nom = "Martin"
    prenom = "Sophie"
    email = "sophie.martin@example.com"
    password = "Password123!"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $register
```

### Test Forgot Password (PowerShell)

```powershell
$forgot = @{
    email = "admin@cesizen.fr"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/forgot-password" `
    -Method Post `
    -ContentType "application/json" `
    -Body $forgot
```

### Test Reset Password (PowerShell)

```powershell
# D'abord obtenir le token depuis l'email reçu
$resetToken = "550e8400-e29b-41d4-a716-446655440000"

$reset = @{
    token = $resetToken
    newPassword = "NewPassword123!"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/reset-password" `
    -Method Post `
    -ContentType "application/json" `
    -Body $reset
```

---

## 🔐 Bonnes Pratiques

1. **Ne jamais envoyer les mots de passe en log**
2. **Utiliser HTTPS en production** (tokens JWT visibles en base64)
3. **Tokens courte durée** (24h recommandé)
4. **Valider les entrées** (@Valid, @NotNull, etc.)
5. **Rate limiting** sur login (optionnel, anti-brute-force)
6. **Logs de connexion** (succès et échecs)

---

## 🔗 Voir Aussi

- **[Sécurité](./03-security.md)** - Modèle de sécurité complet
- **[Utilisateurs](./05-user-api.md)** - Gestion des comptes (admin)
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

