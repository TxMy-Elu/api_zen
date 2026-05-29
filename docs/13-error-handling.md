# Gestion des Erreurs - API Zen

Guide complet des codes d'erreur, messages et solutions pour chaque cas d'erreur.

---

## 📋 Codes HTTP Standards

| Code | Signification | Détail |
|------|---------------|--------|
| **200** | OK | Requête réussie, réponse avec données |
| **201** | Created | Ressource créée avec succès |
| **204** | No Content | Opération réussie, pas de contenu (deletions) |
| **400** | Bad Request | Validation échouée, données invalides |
| **401** | Unauthorized | Token JWT manquant ou invalide |
| **403** | Forbidden | Authentification OK mais permissions insuffisantes |
| **404** | Not Found | Ressource introuvable |
| **409** | Conflict | Conflit d'intégrité (doublon, contrainte FK) |
| **500** | Internal Error | Erreur serveur |

---

## 🔴 400 Bad Request

### Cas 1: Validation DTO Échouée

Quand: Champs manquants ou format invalide

**Exemple - Email invalide:**
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "email": "Invalid email format",
    "password": "Size must be between 6 and 100"
  }
}
```

**Solutions:**
- Vérifier le format des données
- Email doit être `xxx@xxx.xxx`
- Mot de passe ≥ 6 caractères
- Titre d'article non vide
- Durées d'exercice > 0

**Exemple PowerShell (correct):**
```powershell
$body = @{
    email = "user@example.com"  # Format valide
    password = "Password123"     # 11 caractères
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Cas 2: Violation de Règle Métier

Quand: Logique métier échoue

**Exemple - Article sans media URL:**
```json
{
  "status": 400,
  "message": "mediaUrl is required for non-text media types"
}
```

**Cas d'erreur courants:**
- `typeMedia = video` mais `mediaUrl` absent/NULL
- `dureeInspiration = 0` ou négatif
- Email déjà utilisé

**Solution:**
```powershell
# ❌ ERREUR
$article = @{
    titre = "Méditation"
    typeMedia = "video"
    mediaUrl = $null  # Manquant!
} | ConvertTo-Json

# ✅ CORRECT
$article = @{
    titre = "Méditation"
    typeMedia = "video"
    mediaUrl = "/uploads/video.mp4"  # Fourni
} | ConvertTo-Json
```

### Cas 3: Timestamp Invalide (Exercer)

Quand: Format `completedAt` non ISO-8601

**Réponse d'erreur:**
```json
{
  "status": 400,
  "message": "Invalid timestamp format. Use ISO-8601: yyyy-MM-dd'T'HH:mm:ss'Z'"
}
```

**Formats acceptés:**
```powershell
# ✅ CORRECT (ISO-8601 UTC)
$body = @{
    userId = 1
    exerciceId = 2
    completedAt = "2026-04-21T10:30:00Z"
} | ConvertTo-Json

# ✅ CORRECT (Omis = NOW)
$body = @{
    userId = 1
    exerciceId = 2
} | ConvertTo-Json

# ❌ ERREUR (format invalide)
completedAt = "2026-04-21 10:30:00"      # Pas de T
completedAt = "21/04/2026"               # Format français
completedAt = "2026-04-21T10:30:00"      # Pas de Z
```

---

## 🔐 401 Unauthorized

### Cas 1: Token Manquant

Quand: Header `Authorization` absent

**Réponse d'erreur:**
```json
{
  "status": 401,
  "message": "Unauthorized"
}
```

**Solution - Ajouter le header:**
```powershell
$token = "<jwt>"

# ❌ ERREUR (pas de header)
Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Method Get

# ✅ CORRECT
Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
```

### Cas 2: Token Invalide

Quand: Token expiré, corrompu ou signature invalide

**Réponse d'erreur:**
```json
{
  "status": 401,
  "message": "Invalid token"
}
```

**Solutions:**
- Regenerer token via `/api/auth/login`
- Vérifier le format: `Authorization: Bearer <token>`
- Pas d'espaces manquants/supplémentaires

### Cas 3: Login Échoué

Quand: Email ou mot de passe incorrect

**Réponse d'erreur:**
```json
{
  "status": 401,
  "message": "Invalid email or password"
}
```

**Vérifier:**
```powershell
# Email enregistré?
# Mot de passe correct? (case-sensitive)
# Compte actif?

# Utiliser les comptes de demo:
# admin@cesizen.fr / password
# user@cesizen.fr / password
```

---

## 🚫 403 Forbidden

### Cas 1: Role Insuffisant

Quand: User n'a pas `ROLE_ADMIN` pour endpoint admin

**Réponse d'erreur:**
```json
{
  "status": 403,
  "message": "Forbidden - Access Denied"
}
```

**Exemple - Créer un article en tant que USER:**
```powershell
$userToken = "<jwt-user>"  # ROLE_USER

$article = @{
    titre = "Mon article"
    contenu = "..."
    typeMedia = "text"
} | ConvertTo-Json

# ❌ ERREUR 403
Invoke-RestMethod -Uri "http://localhost:8080/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $userToken" } `
    -ContentType "application/json" `
    -Body $article
```

**Solution - Utiliser compte ADMIN:**
```powershell
$adminToken = "<jwt-admin>"  # ROLE_ADMIN

# ✅ SUCCÈS 201
Invoke-RestMethod -Uri "http://localhost:8080/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $adminToken" } `
    -ContentType "application/json" `
    -Body $article
```

### Matrice d'Accès Rappel

| Route | Public | User | Admin |
|-------|--------|------|-------|
| `/api/auth/login` | ✅ | ✅ | ✅ |
| `GET /api/article` | ✅ | ✅ | ✅ |
| `POST /api/article` | ❌ | ❌ | ✅ |
| `GET /api/user/list` | ❌ | ❌ | ✅ |
| `POST /api/consulter` | ❌ | ✅ | ✅ |
| `GET /api/log-activite` | ❌ | ❌ | ✅ |

---

## 🔍 404 Not Found

### Cas 1: Ressource Inexistante

Quand: ID n'existe pas en base

**Exemples:**
```json
{
  "status": 404,
  "message": "User not found"
}
```

```json
{
  "status": 404,
  "message": "Article not found"
}
```

```json
{
  "status": 404,
  "message": "Category not found"
}
```

**Solution - Vérifier l'ID:**
```powershell
# D'abord lister les ressources
$articles = Invoke-RestMethod -Uri "http://localhost:8080/api/article/list" -Method Get

# Puis utiliser un ID valide
$articleId = $articles[0].idArticle  # Premier article existant

$article = Invoke-RestMethod -Uri "http://localhost:8080/api/article/$articleId" `
    -Method Get
```

### Cas 2: User ou Article Introuvable (Create)

Quand: FK invalide lors de création

**Exemple - Exercer avec user invalide:**
```json
{
  "status": 404,
  "message": "User or exercise not found"
}
```

**Solution:**
```powershell
# Vérifier que user et exercice existent
$users = Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Headers @{ Authorization = "Bearer $adminToken" }

$exercices = Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/list"

# Utiliser des IDs valides
$participation = @{
    userId = $users[0].id           # User existant
    exerciceId = $exercices[0].idExercice
} | ConvertTo-Json
```

---

## ⚠️ 409 Conflict

### Cas 1: Doublon (Email, Libelle)

Quand: Violation de contrainte UNIQUE

**Exemple - Email en doublon:**
```json
{
  "status": 409,
  "message": "Email already exists"
}
```

**Exemple - Libelle catégorie en doublon:**
```json
{
  "status": 409,
  "message": "Category label already exists"
}
```

**Solution:**
```powershell
# Vérifier que l'email n'existe pas
$users = Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Headers @{ Authorization = "Bearer $adminToken" }

$existingEmails = $users.email

# Utiliser un nouvel email
if ($existingEmails -contains "nouveau@example.com") {
    Write-Host "Email déjà utilisé!"
} else {
    $newUser = @{
        email = "nouveau@example.com"  # Unique
        # ...
    } | ConvertTo-Json
}
```

### Cas 2: Intégrité Référentielle (FK)

Quand: Suppression impossible car liens existent

**Exemple - Supprimer categorie avec articles liés:**
```json
{
  "status": 409,
  "message": "Cannot delete category - articles are linked"
}
```

**Solution:**
```powershell
# D'abord supprimer les articles
$categoryId = 7
$articles = Invoke-RestMethod -Uri "http://localhost:8080/api/article/list"

$linkedArticles = $articles | Where-Object { $_.idCategorie -eq $categoryId }

foreach ($article in $linkedArticles) {
    Invoke-RestMethod -Uri "http://localhost:8080/api/article/$($article.idArticle)" `
        -Method Delete `
        -Headers @{ Authorization = "Bearer $adminToken" }
}

# Puis supprimer la catégorie
Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/$categoryId" `
    -Method Delete `
    -Headers @{ Authorization = "Bearer $adminToken" }
```

---

## 📊 Tableau Récapitulatif

| Erreur | Cause | Solution |
|--------|-------|----------|
| **400** | Données invalides | Valider format et contenu |
| **401** | Token absent/invalide | Se reconnecter |
| **403** | Role insuffisant | Utiliser compte ADMIN |
| **404** | Ressource inexistante | Vérifier l'ID |
| **409** | Doublon ou contrainte FK | Supprimer liés d'abord |

---

## 🧪 Script de Test - Gestion d'Erreurs

**test-errors.ps1:**
```powershell
$baseUrl = "http://localhost:8080"

# Login admin
$loginBody = @{ email = "admin@cesizen.fr"; password = "password" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token

Write-Host "=== TEST ERREURS ===" -ForegroundColor Green

# Test 1: 400 - Email invalide
Write-Host "`n1. Test 400 - Email invalide" -ForegroundColor Yellow
try {
    $badBody = @{ email = "invalid-email"; password = "pass" } | ConvertTo-Json
    Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $badBody -ContentType "application/json"
} catch {
    Write-Host "✓ Erreur 400 reçue: $($_.Exception.Response.StatusCode)"
}

# Test 2: 401 - Token invalide
Write-Host "`n2. Test 401 - Token invalide" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/user/list" -Method Get `
        -Headers @{ Authorization = "Bearer invalid-token" }
} catch {
    Write-Host "✓ Erreur 401 reçue: $($_.Exception.Response.StatusCode)"
}

# Test 3: 403 - User essayant créer article
Write-Host "`n3. Test 403 - User sans permissions" -ForegroundColor Yellow
$userLoginBody = @{ email = "user@cesizen.fr"; password = "password" } | ConvertTo-Json
$userLogin = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $userLoginBody -ContentType "application/json"
$userToken = $userLogin.token

try {
    $article = @{ titre = "Test"; contenu = "Test"; typeMedia = "text" } | ConvertTo-Json
    Invoke-RestMethod -Uri "$baseUrl/api/article" -Method Post `
        -Headers @{ Authorization = "Bearer $userToken" } `
        -Body $article -ContentType "application/json"
} catch {
    Write-Host "✓ Erreur 403 reçue: $($_.Exception.Response.StatusCode)"
}

# Test 4: 404 - Article inexistant
Write-Host "`n4. Test 404 - Article inexistant" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/article/999999" -Method Get
} catch {
    Write-Host "✓ Erreur 404 reçue: $($_.Exception.Response.StatusCode)"
}

Write-Host "`n=== TESTS COMPLÉTÉS ===" -ForegroundColor Green
```

---

## 🔗 Voir Aussi

- **[Sécurité](./03-security.md)** - Authentification et autorisation
- **[Exemples](./12-examples.md)** - Requêtes valides
- **[Endpoints](./04-auth-api.md)** - Documentation API complète

---

**Dernière mise à jour**: 2026-04-21

