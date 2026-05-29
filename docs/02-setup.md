# Configuration & Lancement - API Zen

## 📋 Prérequis

### Obligatoires
- **JDK 21** (⚠️ PAS JRE - compiler requis)
- **Docker Desktop** (pour MySQL)
- **PowerShell 5.1+** ou bash

### Vérification rapide

```powershell
# Vérifier Java
java -version     # Doit afficher Java 21
javac -version    # Doit afficher compiler, pas "command not found"

# Vérifier Docker
docker --version
docker compose --version
```

---

## 🐳 Démarrer MySQL avec Docker

### 1️⃣ Lancer le container

```powershell
# Depuis la racine du projet
docker compose up -d
```

Le fichier `docker-compose.yml` démarre:
- **Image**: `mysql:8.4.7`
- **Port**: `3306`
- **Root password**: `root`
- **Volume**: `cesizen_db` (persiste les données)

### 2️⃣ Vérifier la connexion

```powershell
# Accéder à MySQL depuis le container
docker exec -it cesizen_mysql mysql -u root -proot

# Ou utiliser une GUI (DBeaver, MySQL Workbench, etc.)
# Host: localhost
# Port: 3306
# User: root
# Password: root
# Database: cesizen (créée automatiquement par l'API)
```

### 3️⃣ Arrêter la base

```powershell
docker compose down
# Ou avec -v pour supprimer les données:
docker compose down -v
```

---

## 🚀 Lancer l'API en Local

### Option 1️⃣ : Via Maven Wrapper (Recommandé)

```powershell
# Depuis la racine du projet
.\mvnw.cmd spring-boot:run
```

L'API démarre sur `http://localhost:8080`

### Option 2️⃣ : Build & JAR

```powershell
# Build le projet
.\mvnw.cmd clean package -DskipTests

# Lancer le JAR
java -jar target/api_zen-1.0.0.jar
```

### Option 3️⃣ : Maven direct (si mvn dans PATH)

```powershell
mvn spring-boot:run
```

---

## 🧪 Lancer les Tests

### Tests unitaires & intégration

```powershell
# Tous les tests
.\mvnw.cmd test

# Test spécifique
.\mvnw.cmd test -Dtest=AuthServiceTest

# Avec rapport de coverage
.\mvnw.cmd test jacoco:report
```

---

## ⚙️ Configuration de l'Application

Fichier: **`src/main/resources/application.properties`**

### Propriétés principales

#### Base de données
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cesizen
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Note**: `ddl-auto=create` récréé les tables à chaque démarrage (utile en dev, DANGEREUX en prod).

#### Initialisation des données
```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data-init.sql
```

Le fichier `data-init.sql` s'exécute automatiquement et injecte:
- 2 rôles (ROLE_ADMIN, ROLE_USER)
- 5 utilisateurs de démo
- 5 exercices
- 6 catégories
- 12 articles
- Relations complètes

#### JWT (Authentification)
```properties
jwt.secret=<clé-secrète-longue>
jwt.expiration=86400000
# 86400000 ms = 24 heures
```

#### Upload de fichiers
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
# Les fichiers sont stockés en: src/main/resources/static/uploads/
```

#### Email (SMTP Gmail)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<email-gmail>
spring.mail.password=<app-password-gmail>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Swagger / OpenAPI
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

#### Logging
```properties
logging.level.root=INFO
logging.level.edu.tx.api_zen=DEBUG
```

---

## 📝 Fichiers de Configuration Importants

### `pom.xml`
- Définit toutes les dépendances Maven
- Propriétés (versions, encoding, etc.)
- Plugins (compiler, surefire pour tests, etc.)

Utilisation:
```powershell
# Si dépendances changent dans pom.xml
.\mvnw.cmd clean install
```

### `docker-compose.yml`
```yaml
version: '3.8'
services:
  cesizen_mysql:
    image: mysql:8.4.7
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: cesizen
    ports:
      - "3306:3306"
    volumes:
      - cesizen_db:/var/lib/mysql

volumes:
  cesizen_db:
```

### `application.properties`
Voir section Configuration ci-dessus. Modifiez ces valeurs pour adapter l'environnement (dev, staging, prod).

---

## 🔐 Comptes de Démo

Injectés via `data-init.sql`:

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| `admin@cesizen.fr` | `password` | ROLE_ADMIN |
| `user@cesizen.fr` | `password` | ROLE_USER |
| `user2@cesizen.fr` | `password` | ROLE_USER |
| `user3@cesizen.fr` | `password` | ROLE_USER |
| `user4@cesizen.fr` | `password` | ROLE_USER |

---

## 🧠 Points clés à Retenir

1. **JDK 21 obligatoire** - `javac` doit fonctionner
2. **Docker pour MySQL** - Évite les installations locales
3. **ddl-auto=create** - Pratique en dev, à changer en prod
4. **data-init.sql** - Re-injecte les données à chaque démarrage
5. **JWT expiration** - 24h configurable
6. **Uploads** - Stockés en `static/uploads/`, accessibles via `/uploads/{filename}`

---

## ✅ Checklist de Démarrage

```powershell
# 1. Vérifier Java
java -version; javac -version

# 2. Lancer Docker
docker compose up -d

# 3. Attendre 5-10 secondes (MySQL initialization)
Start-Sleep -Seconds 10

# 4. Lancer Spring Boot
.\mvnw.cmd spring-boot:run

# 5. Vérifier la santé
Invoke-RestMethod -Uri "http://localhost:8080/swagger-ui.html"

# 6. Se connecter
$body = @{ email = "admin@cesizen.fr"; password = "password" } | ConvertTo-Json
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$response.token
```

---

## 🐛 Troubleshooting

### Erreur: "No compiler is provided in this environment"
**Cause**: JRE au lieu de JDK
**Solution**: Installer JDK 21 (pas JRE)

### Erreur: "Connection refused" (MySQL)
**Cause**: Docker n'est pas démarré ou MySQL ne répond pas
**Solution**:
```powershell
docker compose down
docker compose up -d
Start-Sleep -Seconds 10
```

### Erreur: Port 3306 déjà utilisé
**Cause**: MySQL localhost déjà présent ou ancien container
**Solution**:
```powershell
docker ps
docker kill <container-id>
docker compose up -d
```

### Erreur: Port 8080 déjà utilisé
**Cause**: Une autre application ou une autre instance Spring Boot
**Solution**:
```powershell
# Changer le port dans application.properties:
# server.port=8081
```

---

## 🔗 Voir Aussi

- **[Vue d'ensemble](./01-overview.md)** - Architecture et stack
- **[Sécurité](./03-security.md)** - Configuration JWT
- **[Endpoints](./04-auth-api.md)** - Commencer avec l'API

---

**Dernière mise à jour**: 2026-04-21

