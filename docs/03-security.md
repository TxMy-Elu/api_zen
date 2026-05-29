# Sécurité & Autorisations - API Zen

## 🔐 Modèle de Sécurité

### Vue d'ensemble

L'API Zen utilise une architecture de sécurité basée sur:
- **JWT (JSON Web Tokens)** pour l'authentification stateless
- **Spring Security** pour le contrôle d'accès
- **Roles** (ROLE_ADMIN, ROLE_USER) pour l'autorisation
- **@PreAuthorize** pour les restrictions au niveau des méthodes

---

## 🎫 JWT (JSON Web Tokens)

### Structure du Token

Un JWT contient 3 parties séparées par des points:

```
header.payload.signature
```

**Exemple de payload décodé**:
```json
{
  "sub": "admin@cesizen.fr",
  "userId": 1,
  "role": "ROLE_ADMIN",
  "iat": 1703001200,
  "exp": 1703087600
}
```

### Génération

**Endpoint**: `POST /api/auth/login`

```json
{
  "email": "admin@cesizen.fr",
  "password": "password"
}
```

**Réponse 200**:
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

### Propriétés

| Propriété | Valeur | Notes |
|-----------|--------|-------|
| **Secret** | (dans `application.properties`) | Doit être > 32 caractères |
| **Expiration** | 86400000 ms (24h) | Configurable |
| **Algorithm** | HS512 | HMAC avec SHA-512 |

**Code source**: `JwtUtils.java`

---

## 🔑 Utilisation du Token

### Format du Header

```http
Authorization: Bearer <token>
```

### Exemple avec PowerShell

```powershell
$token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/user/list" -Headers $headers -Method Get
```

### Exemple avec cURL

```bash
TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

curl -X GET "http://localhost:8080/api/user/list" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

---

## 📍 Routes Publiques (Pas de Token Requis)

Configuration source: `SecurityConfig.java`

```
OPTIONS /**                          (CORS preflight)
/api/auth/**                         (login, register, password reset)
/v3/api-docs/**                      (OpenAPI spec)
/swagger-ui/**                       (Swagger UI assets)
/swagger-ui.html                     (Swagger UI page)
GET /api/article/**                  (Lecture articles)
GET /api/categorie/**                (Lecture catégories)
GET /api/exercice/**                 (Lecture exercices)
/uploads/**                          (Fichiers media)
```

### Accès sans token
```powershell
# Pas de header Authorization requis
Invoke-RestMethod -Uri "http://localhost:8080/api/article/list" -Method Get
```

---

## 🔓 Routes Authentifiées (JWT Requis)

Toute route non publique nécessite un token JWT valide.

Exemples:
- `GET /api/user/list` - Authentification requise
- `GET /api/consulter/list` - Authentification requise
- `POST /api/exercer` - Authentification requise

**Réponse 401 si token manquant ou invalide**:
```json
{
  "status": 401,
  "message": "Unauthorized"
}
```

---

## 👮 Routes Admin (@PreAuthorize("hasRole('ADMIN')"))

Seuls les utilisateurs avec `ROLE_ADMIN` peuvent y accéder.

### Utilisateurs
```
GET /api/user/list
GET /api/user/{id}
POST /api/user
PUT /api/user/{id}
DELETE /api/user/{id}
```

### Logs (Audit)
```
GET /api/log-activite/**
GET /api/log-connexion/**
```

### Écritures (Catégories, Articles, Exercices, Utilisateurs)
```
POST /api/categorie
PUT /api/categorie/{id}
DELETE /api/categorie/{id}

POST /api/article
PUT /api/article/{id}
DELETE /api/article/{id}
POST /api/article/upload

POST /api/exercice
PUT /api/exercice/{id}
DELETE /api/exercice/{id}
```

**Réponse 403 si role insuffisant**:
```json
{
  "status": 403,
  "message": "Forbidden - Access Denied"
}
```

---

## 👤 Routes User (JWT Requis - Pas Admin)

Les utilisateurs standards peuvent accéder à:

- `GET /api/categorie/**` - Lecture
- `GET /api/article/**` - Lecture
- `GET /api/exercice/**` - Lecture
- `POST /api/consulter` - Enregistrer une consultation
- `GET /api/consulter/**` - Voir ses consultations
- `POST /api/exercer` - Enregistrer une participation
- `GET /api/exercer/**` - Voir ses participations
- `GET /api/role/list` - Liste des rôles

---

## 🏠 Contrôleur de Sécurité

Fichier: `src/main/java/edu/tx/api_zen/config/SecurityConfig.java`

### Configuration CORS

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .cors(...)          // CORS autorisé
            .csrf().disable()   // CSRF désactivé (API REST)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // ... autres configurations
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## 🔍 JWT Filter

Fichier: `JwtAuthenticationFilter.java`

Le filter intercepte chaque requête et:
1. Extrait le token du header `Authorization: Bearer <token>`
2. Valide le token via `JwtUtils`
3. Crée un `SecurityContext` avec les claims (email, userId, role)
4. Spring Security utilise ce context pour `@PreAuthorize`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String token = extractTokenFromRequest(request);
        if (token != null && jwtUtils.validateToken(token)) {
            String email = jwtUtils.getEmailFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            // Construire SecurityContext
            setSecurityContext(email, role);
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## 🎯 Matrice d'Accès

|  | Public | User | Admin |
|---|--------|------|-------|
| **Login** | ✅ | ✅ | ✅ |
| **Register** | ✅ | ✅ | ✅ |
| **Swagger** | ✅ | ✅ | ✅ |
| **GET Articles** | ✅ | ✅ | ✅ |
| **POST Article** | ❌ | ❌ | ✅ |
| **GET User** | ❌ | ❌ | ✅ |
| **POST User** | ❌ | ❌ | ✅ |
| **Consulter** | ❌ | ✅ | ✅ |
| **Exercer** | ❌ | ✅ | ✅ |
| **Logs** | ❌ | ❌ | ✅ |

---

## 🛡️ Bonnes Pratiques

1. **Garder le secret JWT sécurisé**
   - Ne JAMAIS committer `jwt.secret` en clair
   - Utiliser des variables d'environnement en prod

2. **Renouveler les tokens**
   - Implémenter un refresh token mecanism (optionnel)
   - Durée d'expiration courte (24h recommandé)

3. **HTTPS obligatoire en production**
   - Les tokens JWT sont lisibles en base64
   - HTTPS les protège en transit

4. **Audit & Logging**
   - `LogActiviteAspect` log toutes les écritures
   - `LogConnexion` track les connexions (success/fail)

5. **Validation entrante**
   - Tous les DTOs utilisent `@Valid`
   - Spring lève `ConstraintViolationException` → 400

---

## 🔗 Voir Aussi

- **[Authentification API](./04-auth-api.md)** - Routes auth détaillées
- **[Utilisateurs](./05-user-api.md)** - Gestion des utilisateurs (admin)
- **[Logs](./11-logs-api.md)** - Journalisation et audit
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes de réponse

---

**Dernière mise à jour**: 2026-04-21

