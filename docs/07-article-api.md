# Articles API - API Zen

## 📄 Endpoints Articles

Base: `/api/article`

---

## 1️⃣ Lister Tous les Articles

Récupérer tous les articles (PUBLIC).

### Requête

```http
GET /api/article/list
```

### Réponse 200 OK

```json
[
  {
    "idArticle": 1,
    "titre": "Comprendre la respiration abdominale",
    "contenu": "La respiration abdominale est une technique...",
    "typeMedia": "text",
    "mediaUrl": null,
    "datePublication": "2026-01-10T08:00:00Z",
    "dateModification": "2026-04-15T14:30:00Z",
    "estPublie": true,
    "idCategorie": 1,
    "categorieLibelle": "Respiration"
  },
  {
    "idArticle": 3,
    "titre": "Méditation du soir",
    "contenu": "Pour une meilleure nuit de sommeil...",
    "typeMedia": "video",
    "mediaUrl": "/uploads/meditation-soir.mp4",
    "datePublication": "2026-02-05T10:15:00Z",
    "dateModification": "2026-04-10T11:20:00Z",
    "estPublie": true,
    "idCategorie": 2,
    "categorieLibelle": "Sommeil"
  }
]
```

---

## 2️⃣ Lister Articles Publiés

Récupérer uniquement les articles publiés (PUBLIC).

### Requête

```http
GET /api/article/public
```

### Réponse 200 OK

```json
[
  {
    "idArticle": 1,
    "titre": "Comprendre la respiration abdominale",
    "contenu": "La respiration abdominale est une technique...",
    "typeMedia": "text",
    "mediaUrl": null,
    "datePublication": "2026-01-10T08:00:00Z",
    "dateModification": "2026-04-15T14:30:00Z",
    "estPublie": true,
    "idCategorie": 1,
    "categorieLibelle": "Respiration"
  }
]
```

---

## 3️⃣ Récupérer un Article

Détail d'un article spécifique (PUBLIC).

### Requête

```http
GET /api/article/{id}
```

### Paramètres

| Paramètre | Type | Requis |
|-----------|------|--------|
| `id` | Long | ✅ |

### Réponse 200 OK

```json
{
  "idArticle": 1,
  "titre": "Comprendre la respiration abdominale",
  "contenu": "La respiration abdominale est une technique fondamentale...",
  "typeMedia": "text",
  "mediaUrl": null,
  "datePublication": "2026-01-10T08:00:00Z",
  "dateModification": "2026-04-15T14:30:00Z",
  "estPublie": true,
  "idCategorie": 1,
  "categorieLibelle": "Respiration"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Article not found"
}
```

---

## 4️⃣ Créer un Article

Créer un nouvel article (ADMIN UNIQUEMENT).

### Requête

```http
POST /api/article
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "titre": "4-7-8 Respiration Technique",
  "contenu": "Une technique puissante de respiration pour réduire l'anxiété...",
  "typeMedia": "text",
  "mediaUrl": null,
  "estPublie": true,
  "idCategorie": 1
}
```

### Variante avec Vidéo

```json
{
  "titre": "Méditation Guidée 10 minutes",
  "contenu": "Suivez cette méditation guidée...",
  "typeMedia": "video",
  "mediaUrl": "/uploads/meditation-10min.mp4",
  "estPublie": true,
  "idCategorie": 3
}
```

### Réponse 201 Created

```json
{
  "idArticle": 13,
  "titre": "4-7-8 Respiration Technique",
  "contenu": "Une technique puissante de respiration pour réduire l'anxiété...",
  "typeMedia": "text",
  "mediaUrl": null,
  "datePublication": "2026-04-21T12:00:00Z",
  "dateModification": "2026-04-21T12:00:00Z",
  "estPublie": true,
  "idCategorie": 1,
  "categorieLibelle": "Respiration"
}
```

### Réponse 400 Bad Request

Validation échouée:
```json
{
  "status": 400,
  "message": "Validation error",
  "errors": {
    "titre": "Must not be empty",
    "typeMedia": "Invalid media type"
  }
}
```

Media manquant pour type non-texte:
```json
{
  "status": 400,
  "message": "mediaUrl is required for non-text media types"
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
| `titre` | String | ✅ | Non vide |
| `contenu` | String | ❌ | Optionnel |
| `typeMedia` | Enum | ✅ | `text`, `image`, `video`, `pdf` |
| `mediaUrl` | String | Conditionnel | Requis si typeMedia ≠ text |
| `estPublie` | Boolean | ❌ | Default: false |
| `idCategorie` | Long | ❌ | FK vers Categorie |

---

## 5️⃣ Mettre à Jour un Article

Modifier un article existant (ADMIN UNIQUEMENT).

### Requête

```http
PUT /api/article/{id}
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "titre": "4-7-8 Respiration Technique - Mise à jour",
  "contenu": "Une technique puissante de respiration pour réduire l'anxiété... (mise à jour)",
  "typeMedia": "text",
  "mediaUrl": null,
  "estPublie": true,
  "idCategorie": 1
}
```

### Réponse 200 OK

```json
{
  "idArticle": 13,
  "titre": "4-7-8 Respiration Technique - Mise à jour",
  "contenu": "Une technique puissante de respiration pour réduire l'anxiété... (mise à jour)",
  "typeMedia": "text",
  "mediaUrl": null,
  "datePublication": "2026-04-21T12:00:00Z",
  "dateModification": "2026-04-21T15:30:00Z",
  "estPublie": true,
  "idCategorie": 1,
  "categorieLibelle": "Respiration"
}
```

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Article not found"
}
```

---

## 6️⃣ Supprimer un Article

Supprimer un article (ADMIN UNIQUEMENT).

### Requête

```http
DELETE /api/article/{id}
Authorization: Bearer <token-admin>
```

### Réponse 204 No Content

(Aucun body de réponse)

### Réponse 404 Not Found

```json
{
  "status": 404,
  "message": "Article not found"
}
```

---

## 7️⃣ Upload de Fichier Media

Uploader un fichier media pour un article (ADMIN UNIQUEMENT).

### Requête

```http
POST /api/article/upload
Authorization: Bearer <token-admin>
Content-Type: multipart/form-data

file: <fichier-binary>
```

Avec cURL:
```bash
TOKEN="<jwt-admin>"
curl -X POST "http://localhost:8080/api/article/upload" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/chemin/vers/mon-fichier.pdf"
```

Avec PowerShell:
```powershell
$token = "<jwt-admin>"
$filePath = "C:\temp\mon-fichier.pdf"

# Créer le form-data
$form = @{
    file = Get-Item -Path $filePath
}

Invoke-RestMethod -Uri "http://localhost:8080/api/article/upload" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -Form $form
```

### Réponse 200 OK

```json
{
  "filename": "1712153400000_mon-fichier.pdf",
  "url": "/uploads/1712153400000_mon-fichier.pdf",
  "size": 245632,
  "uploadedAt": "2026-04-21T12:30:00Z"
}
```

### Réponse 400 Bad Request

Fichier trop volumineux (max 10MB):
```json
{
  "status": 400,
  "message": "File size exceeds maximum allowed (10MB)"
}
```

Aucun fichier:
```json
{
  "status": 400,
  "message": "No file provided"
}
```

### Réponse 403 Forbidden

Non-admin:
```json
{
  "status": 403,
  "message": "Forbidden - Access Denied"
}
```

### Détails Upload

- **Limite**: 10 MB
- **Stockage**: `src/main/resources/static/uploads/`
- **Format nom**: `<timestamp>_<nom-fichier-original>`
- **URL publique**: `/uploads/<filename>`
- **Accès**: Pas d'authentification requise pour accéder au fichier

---

## 📊 Résumé des Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/api/article/list` | Public | Lister tous |
| GET | `/api/article/public` | Public | Lister publiés |
| GET | `/api/article/{id}` | Public | Détail article |
| POST | `/api/article` | ADMIN | Créer article |
| PUT | `/api/article/{id}` | ADMIN | Mettre à jour |
| DELETE | `/api/article/{id}` | ADMIN | Supprimer |
| POST | `/api/article/upload` | ADMIN | Upload média |

---

## 🧪 Exemples de Tests

### Créer un article texte (PowerShell)

```powershell
$token = "<jwt-admin>"

$newArticle = @{
    titre = "Respiration 4-7-8"
    contenu = "Technique puissante pour l'anxiété"
    typeMedia = "text"
    estPublie = $true
    idCategorie = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/article" `
    -Method Post `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $newArticle
```

### Mettre à jour un article (PowerShell)

```powershell
$token = "<jwt-admin>"
$articleId = 13

$updateArticle = @{
    titre = "Respiration 4-7-8 - Mise à jour"
    contenu = "Technique mise à jour..."
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

### Uploader un fichier (PowerShell)

```powershell
$token = "<jwt-admin>"
$filePath = "C:\downloads\meditation.mp4"

curl.exe -X POST "http://localhost:8080/api/article/upload" `
    -H "Authorization: Bearer $token" `
    -F "file=@$filePath"
```

---

## 💡 Règles Métier

1. **Types de media**
   - `text`: Contenu uniquement texte (mediaUrl optionnel)
   - `image`: Nécessite mediaUrl (ex: /uploads/image.png)
   - `video`: Nécessite mediaUrl (ex: /uploads/video.mp4)
   - `pdf`: Nécessite mediaUrl (ex: /uploads/document.pdf)

2. **Titre obligatoire**
   - Non vide
   - Pas de restriction d'unicité

3. **Catégorie optionnelle**
   - Si fournie, doit exister
   - Peut être NULL

4. **Publication**
   - `estPublie=false`: Brouillon (visible en list mais pas public)
   - `estPublie=true`: Publié (visible partout)

5. **Dates auto**
   - `datePublication`: Définie à la création
   - `dateModification`: Mise à jour à chaque PUT

6. **Uploads**
   - Stockés avec timestamp unique
   - Accessibles publiquement via `/uploads/`
   - Max 10 MB

---

## 🔗 Voir Aussi

- **[Catégories](./06-categorie-api.md)** - Catégorisation des articles
- **[Consultations](./09-consulter-api.md)** - Suivi des vues d'articles
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP
- **[Exemples](./12-examples.md)** - Requêtes complètes

---

**Dernière mise à jour**: 2026-04-21

