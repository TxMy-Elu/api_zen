# Exemples de Requêtes - API Zen

Guide complet avec exemples PowerShell et cURL pour tester l'API.

---

## 🔑 Authentification & Login

### 1. Login Admin

**PowerShell:**
```powershell
$loginBody = @{
    email = "admin@cesizen.fr"
    password = "password"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginBody

# Extraire et sauvegarder le token
$adminToken = $response.token
Write-Host "Admin Token: $adminToken"
Write-Host "User ID: $($response.userId)"
Write-Host "Role: $($response.role)"
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@cesizen.fr",
    "password": "password"
  }'
```

### 2. Register Nouvel Utilisateur

**PowerShell:**
```powershell
$registerBody = @{
    nom = "Nouveau"
    prenom = "Utilisateur"
    email = "nouveau.utilisateur@example.com"
    password = "Password123!"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $registerBody

$response
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Nouveau",
    "prenom": "Utilisateur",
    "email": "nouveau.utilisateur@example.com",
    "password": "Password123!"
  }'
```

### 3. Forgot Password

**PowerShell:**
```powershell
$forgotBody = @{
    email = "admin@cesizen.fr"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/forgot-password" `
    -Method Post `
    -ContentType "application/json" `
    -Body $forgotBody
```

---

## 📂 Catégories

### 1. Lister les Catégories (PUBLIC)

**PowerShell:**
```powershell
$categories = Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/list" `
    -Method Get

$categories | Format-Table -Property idCategorie, libelle, description
```

**cURL:**
```bash
curl "http://localhost:8080/api/categorie/list"
```

### 2. Créer une Catégorie (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"

$newCategory = @{
    libelle = "Nutrition"
    description = "Conseils nutritionnels et alimentation saine"
} | ConvertTo-Json

$category = Invoke-RestMethod -Uri "http://localhost:8080/api/categorie" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newCategory

Write-Host "Catégorie créée avec ID: $($category.idCategorie)"
```

**cURL:**
```bash
TOKEN="<admin-token>"
curl -X POST "http://localhost:8080/api/categorie" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "libelle": "Nutrition",
    "description": "Conseils nutritionnels et alimentation saine"
  }'
```

### 3. Mettre à Jour une Catégorie (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"
$categoryId = 7

$updateCategory = @{
    libelle = "Nutrition & Santé"
    description = "Conseils nutritionnels et santé globale"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/categorie/$categoryId" `
    -Method Put `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $updateCategory
```

---

## 📄 Articles

### 1. Lister tous les Articles (PUBLIC)

**PowerShell:**
```powershell
$articles = Invoke-RestMethod -Uri "http://localhost:8080/api/article/list" `
    -Method Get

$articles | ForEach-Object {
    Write-Host "$($_.idArticle): $($_.titre) [$($_.categorieLibelle)]"
}
```

### 2. Créer un Article Texte (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"

$newArticle = @{
    titre = "Respiration 4-7-8 Technique Complète"
    contenu = "La respiration 4-7-8 est une technique puissante..."
    typeMedia = "text"
    estPublie = $true
    idCategorie = 1
} | ConvertTo-Json

$article = Invoke-RestMethod -Uri "http://localhost:8080/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newArticle

Write-Host "Article créé: ID $($article.idArticle)"
```

### 3. Créer un Article avec Vidéo (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"

$videoArticle = @{
    titre = "Méditation Guidée 10 minutes"
    contenu = "Suivez cette méditation guidée pour détendre votre esprit"
    typeMedia = "video"
    mediaUrl = "/uploads/meditation-10min.mp4"
    estPublie = $true
    idCategorie = 3
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $videoArticle
```

### 4. Upload de Fichier Media (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"
$filePath = "C:\downloads\mon-ebook.pdf"

# Avec PowerShell 7+
$form = @{
    file = Get-Item -Path $filePath
}

$uploadResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/article/upload" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -Form $form

Write-Host "Fichier uploadé: $($uploadResponse.url)"
```

**cURL (Bash/Git Bash):**
```bash
TOKEN="<admin-token>"
curl -X POST "http://localhost:8080/api/article/upload" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/chemin/vers/mon-ebook.pdf"
```

### 5. Mettre à Jour un Article (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"
$articleId = 13

$updateArticle = @{
    titre = "Respiration 4-7-8 - Mise à Jour"
    contenu = "Contenu mis à jour avec plus de détails..."
    typeMedia = "text"
    estPublie = $true
    idCategorie = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/article/$articleId" `
    -Method Put `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $updateArticle
```

---

## 🧘 Exercices

### 1. Lister les Exercices (PUBLIC)

**PowerShell:**
```powershell
$exercices = Invoke-RestMethod -Uri "http://localhost:8080/api/exercice/list" `
    -Method Get

$exercices | ForEach-Object {
    $cycle = $_.dureeInspiration + $_.dureeApnee + $_.dureeExpiration
    Write-Host "$($_.idExercice): $($_.nom) (cycle: ${cycle}s)"
}
```

### 2. Créer un Exercice (ADMIN)

**PowerShell:**
```powershell
$token = "<admin-token>"

$newExercice = @{
    nom = "Cohérence Cardiaque"
    dureeInspiration = 5
    dureeApnee = 0
    dureeExpiration = 5
    description = "Respiration synchronisée avec le cœur"
} | ConvertTo-Json

$exercice = Invoke-RestMethod -Uri "http://localhost:8080/api/exercice" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newExercice

Write-Host "Exercice créé: ID $($exercice.idExercice)"
```

---

## 👥 Utilisateurs (ADMIN)

### 1. Lister les Utilisateurs

**PowerShell:**
```powershell
$token = "<admin-token>"

$users = Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$users | Format-Table -Property id, email, nom, prenom, roleLibelle
```

### 2. Créer un Utilisateur

**PowerShell:**
```powershell
$token = "<admin-token>"

$newUser = @{
    nom = "Dupré"
    prenom = "Marc"
    email = "marc.dupre@example.com"
    password = "Password123!"
    roleId = 2  # ROLE_USER
} | ConvertTo-Json

$user = Invoke-RestMethod -Uri "http://localhost:8080/api/user" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newUser

Write-Host "Utilisateur créé: $($user.email) (ID: $($user.id))"
```

---

## 👀 Consultations d'Articles

### 1. Enregistrer une Consultation

**PowerShell:**
```powershell
$token = "<jwt>"

$newConsultation = @{
    idUtilisateur = 1
    idArticle = 3
} | ConvertTo-Json

$consultation = Invoke-RestMethod -Uri "http://localhost:8080/api/consulter" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newConsultation

Write-Host "Consultation enregistrée: $($consultation.idConsulter)"
```

### 2. Voir les Articles Lus par un Utilisateur

**PowerShell:**
```powershell
$token = "<jwt>"
$userId = 1

$consultations = Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/user/$userId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$consultations | ForEach-Object {
    Write-Host "$($_.titreArticle) - lu le $($_.viewedAt)"
}
```

### 3. Compter les Vues d'un Article

**PowerShell:**
```powershell
$token = "<jwt>"
$articleId = 1

$stats = Invoke-RestMethod -Uri "http://localhost:8080/api/consulter/article/$articleId/count" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

Write-Host "Article '$($stats.articleTitle)' : $($stats.viewCount) vues"
```

---

## 🏋️ Participations aux Exercices

### 1. Enregistrer une Participation

**PowerShell:**
```powershell
$token = "<jwt>"

$newParticipation = @{
    userId = 1
    exerciceId = 2
} | ConvertTo-Json

$participation = Invoke-RestMethod -Uri "http://localhost:8080/api/exercer" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newParticipation

Write-Host "Participation enregistrée: $($participation.idExercer)"
```

### 2. Avec Timestamp Personnalisé

**PowerShell:**
```powershell
$token = "<jwt>"

$participation = @{
    userId = 1
    exerciceId = 2
    completedAt = "2026-04-21T10:30:00Z"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/exercer" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $participation
```

### 3. Voir les Participations d'un Utilisateur

**PowerShell:**
```powershell
$token = "<jwt>"
$userId = 1

$participations = Invoke-RestMethod -Uri "http://localhost:8080/api/exercer/user/$userId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$participations | ForEach-Object {
    Write-Host "$($_.exercice.nom) - complété le $($_.completedAt)"
}
```

---

## 📊 Logs (ADMIN)

### 1. Voir les 50 Derniers Logs

**PowerShell:**
```powershell
$token = "<admin-token>"

$logs = Invoke-RestMethod -Uri "http://localhost:8080/api/log-activite/recent" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$logs | ForEach-Object {
    Write-Host "$($_.dateAction): $($_.user.email) - $($_.typeAction) sur $($_.tableConcernee)"
}
```

### 2. Voir l'Historique d'un Article

**PowerShell:**
```powershell
$token = "<admin-token>"
$articleId = 13

$history = Invoke-RestMethod -Uri "http://localhost:8080/api/log-activite/table/article/$articleId" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$history | ForEach-Object {
    Write-Host "$($_.dateAction): $($_.typeAction)"
    Write-Host "Nouvelles valeurs: $($_.nouvellesValeurs)"
}
```

### 3. Voir les Connexions Échouées

**PowerShell:**
```powershell
$token = "<admin-token>"

$failedLogins = Invoke-RestMethod -Uri "http://localhost:8080/api/log-connexion/failed" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }

$failedLogins | ForEach-Object {
    Write-Host "Tentative échouée: $($_.email) depuis $($_.ip) à $($_.dateConnexion)"
}
```

---

## 🧪 Script de Test Complet

Voici un script complet testant l'API:

**test-api.ps1:**
```powershell
# Configuration
$baseUrl = "http://localhost:8080"

# 1. LOGIN
Write-Host "=== LOGIN ===" -ForegroundColor Green
$loginBody = @{
    email = "admin@cesizen.fr"
    password = "password"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginBody

$token = $loginResponse.token
Write-Host "✓ Connecté en tant que $($loginResponse.email)"

# 2. LISTER CATÉGORIES
Write-Host "`n=== CATÉGORIES ===" -ForegroundColor Green
$categories = Invoke-RestMethod -Uri "$baseUrl/api/categorie/list" `
    -Method Get

Write-Host "✓ $($categories.Count) catégories trouvées"
$categories | Format-Table -Property idCategorie, libelle

# 3. LISTER ARTICLES
Write-Host "`n=== ARTICLES ===" -ForegroundColor Green
$articles = Invoke-RestMethod -Uri "$baseUrl/api/categorie/list" `
    -Method Get

Write-Host "✓ Articles listés"

# 4. LISTER EXERCICES
Write-Host "`n=== EXERCICES ===" -ForegroundColor Green
$exercices = Invoke-RestMethod -Uri "$baseUrl/api/exercice/list" `
    -Method Get

Write-Host "✓ $($exercices.Count) exercices trouvés"

# 5. CRÉER ARTICLE
Write-Host "`n=== CRÉER ARTICLE ===" -ForegroundColor Green
$newArticle = @{
    titre = "Test Respiration $((Get-Date).Ticks)"
    contenu = "Contenu de test"
    typeMedia = "text"
    estPublie = $true
    idCategorie = 1
} | ConvertTo-Json

$createdArticle = Invoke-RestMethod -Uri "$baseUrl/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newArticle

Write-Host "✓ Article créé: ID $($createdArticle.idArticle)"

# 6. ENREGISTRER CONSULTATION
Write-Host "`n=== CONSULTATION ===" -ForegroundColor Green
$consultation = @{
    idUtilisateur = 1
    idArticle = 1
} | ConvertTo-Json

$createdConsultation = Invoke-RestMethod -Uri "$baseUrl/api/consulter" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $consultation

Write-Host "✓ Consultation enregistrée: ID $($createdConsultation.idConsulter)"

Write-Host "`n=== TESTS COMPLETS ===" -ForegroundColor Green
```

Exécuter: `.\test-api.ps1`

---

## 🔗 Voir Aussi

- **[Vue d'ensemble](./01-overview.md)** - Architecture
- **[Sécurité](./03-security.md)** - Authentification JWT
- **[Tous les Endpoints](./04-auth-api.md)** - Documentation détaillée

---

**Dernière mise à jour**: 2026-04-21

