# Déploiement & Production - API Zen

Guide complet pour déployer l'API Zen en environnement de production avec meilleures pratiques.

---

## ⚠️ Limites Connues et Recommandations

### 1. Configuration en Clair

**❌ Problème CRITIQUE:**
```properties
# JAMAIS en production!
spring.datasource.password=root
jwt.secret=secretKeySuperSecretNotSafe
spring.mail.password=password123
```

**✅ Solution - Variables d'Environnement:**
```bash
# Linux/Mac
export DB_PASSWORD=prod_password_secure
export JWT_SECRET=very_long_random_string_min_32_chars
export MAIL_PASSWORD=app_password_from_gmail

# Windows (PowerShell)
$env:DB_PASSWORD = "prod_password_secure"
$env:JWT_SECRET = "very_long_random_string_min_32_chars"
```

**✅ Utilisation dans application.properties:**
```properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET:defaultSecretIfEnvMissing}
spring.mail.password=${MAIL_PASSWORD}
```

### 2. DDL Auto = Create (DANGEREUX EN PROD)

**❌ Configuration de développement:**
```properties
spring.jpa.hibernate.ddl-auto=create  # Récréé les tables à CHAQUE démarrage!
```

**✅ Configuration de production:**
```properties
spring.jpa.hibernate.ddl-auto=validate  # Valide seulement
# Ou:
spring.jpa.hibernate.ddl-auto=update    # Ajoute colonnes/tables (safer)
```

**Migrations recommandées:**
- Utiliser Flyway ou Liquibase pour gérer les migrations
- Tester en staging avant prod

### 3. Init Data Réinjectée à Chaque Démarrage

**❌ Problème:**
```properties
spring.sql.init.mode=always  # Réexécute data-init.sql à chaque redémarrage
```

**✅ Solutions:**
```properties
# Option 1: Désactiver en production
spring.sql.init.mode=never

# Option 2: Conditonnel par profil
# application-prod.properties:
spring.sql.init.mode=never
spring.jpa.hibernate.ddl-auto=validate
```

### 4. Secrets en Clair dans le Code

**❌ JAMAIS faire cela:**
```java
public static final String JWT_SECRET = "secretKeyHardcodedInTheCode";
```

**✅ Utiliser Spring Cloud Config ou AWS Secrets Manager:**
```java
@Value("${jwt.secret}")
private String jwtSecret;
```

### 5. Logging en DEBUG

**❌ Production:**
```properties
logging.level.root=DEBUG  # Trop verbeux, ralentit le serveur
```

**✅ Production:**
```properties
logging.level.root=WARN
logging.level.edu.tx.api_zen=INFO
logging.level.org.springframework=WARN
```

### 6. HTTPS Manquant

**❌ En clair (HTTP):**
```
http://api.cesizen.fr
# JWT tokens visibles en base64 en transit!
```

**✅ Sécurisé:**
```
https://api.cesizen.fr  # Certificat SSL/TLS obligatoire
```

### 7. CORS Trop Large

**❌ Production:**
```java
.allowedOrigins("*")  // Accepte n'importe quel domaine!
```

**✅ Production:**
```java
.allowedOrigins("https://cesizen.fr", "https://www.cesizen.fr")
```

---

## 🚀 Déploiement en Production

### Architecture Recommandée

```
┌─────────────────┐
│   Client App    │
│  (Web/Mobile)   │
└────────┬────────┘
         │ HTTPS
         ↓
┌─────────────────────────────────────┐
│  Load Balancer / Reverse Proxy      │
│  (nginx, Apache, AWS ALB)           │
│  - SSL/TLS Termination              │
│  - Rate Limiting                    │
│  - Request Logging                  │
└────────┬────────────────────────────┘
         │
         ↓
┌──────────────────────────────────────┐
│  API Zen Spring Boot Instances       │
│  (Docker containers / K8s pods)      │
│  - Multiple replicas for HA          │
└────────┬─────────────────────────────┘
         │
         ↓
┌──────────────────────────────────────┐
│  MySQL 8.4+ (Cloud or On-Premise)    │
│  - Replication for HA                │
│  - Regular Backups                   │
│  - Encrypted Connections             │
└──────────────────────────────────────┘
```

### Option 1: Docker Container (Heroku, AWS ECS, etc.)

**Dockerfile:**
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy built JAR
COPY target/api_zen-1.0.0.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", \
  "--spring.profiles.active=prod", \
  "--server.port=8080"]
```

**Build & Push:**
```bash
# Build
mvn clean package -DskipTests

# Docker
docker build -t cesizen-api:1.0.0 .
docker tag cesizen-api:1.0.0 registry.example.com/cesizen-api:latest
docker push registry.example.com/cesizen-api:latest
```

### Option 2: Docker Compose pour Staging

**docker-compose-prod.yml:**
```yaml
version: '3.8'

services:
  api:
    image: cesizen-api:1.0.0
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: mysql
      DB_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
    depends_on:
      - mysql
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: cesizen
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"

volumes:
  mysql_data:
```

**Déployer:**
```bash
# Copy env file
cp .env.prod .env

# Start stack
docker compose -f docker-compose-prod.yml up -d

# Monitor logs
docker compose -f docker-compose-prod.yml logs -f api
```

### Option 3: Kubernetes (GKE, EKS, AKS)

**k8s-deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cesizen-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: cesizen-api
  template:
    metadata:
      labels:
        app: cesizen-api
    spec:
      containers:
      - name: api
        image: registry.example.com/cesizen-api:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: mysql.default.svc.cluster.local
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: cesizen-secrets
              key: db-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: cesizen-secrets
              key: jwt-secret
        resources:
          requests:
            cpu: "250m"
            memory: "512Mi"
          limits:
            cpu: "500m"
            memory: "1Gi"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

**Déployer:**
```bash
# Create secrets
kubectl create secret generic cesizen-secrets \
  --from-literal=db-password=secure_password \
  --from-literal=jwt-secret=very_long_secure_key

# Deploy
kubectl apply -f k8s-deployment.yaml

# Monitor
kubectl logs -f deployment/cesizen-api
```

---

## 📋 Checklist de Production

- [ ] **Configuration**
  - [ ] `jwt.secret` en variable d'environnement (32+ chars)
  - [ ] `spring.datasource.password` en env var
  - [ ] `spring.mail.password` en env var
  - [ ] `ddl-auto=validate` (pas create!)
  - [ ] `sql.init.mode=never` (pas always!)
  - [ ] Logging level = INFO (pas DEBUG)

- [ ] **Sécurité**
  - [ ] HTTPS/SSL obligatoire
  - [ ] CORS restreint aux domaines valides
  - [ ] Rate limiting configuré
  - [ ] Passwords hachés (BCrypt)
  - [ ] SQL Injection: JPA (protected), validation input

- [ ] **Performance**
  - [ ] Connection pool MySQL optimisé
  - [ ] Caching si nécessaire
  - [ ] Indexes sur colonnes fréquentes
  - [ ] Logs asynchrones

- [ ] **Monitoring & Backup**
  - [ ] Health checks actifs (`/actuator/health`)
  - [ ] Logs centralisés (ELK, CloudWatch)
  - [ ] Monitoring CPU/RAM/Disk
  - [ ] Backups MySQL quotidiens
  - [ ] Alertes sur erreurs
  - [ ] Monitoring de latence

- [ ] **Conformité**
  - [ ] RGPD: Droits d'accès/suppression données
  - [ ] Audit logs: Journalisation complète
  - [ ] Chiffrage données sensibles

---

## 🔧 Profiles Spring

**application-dev.properties:**
```properties
spring.jpa.hibernate.ddl-auto=create
spring.sql.init.mode=always
logging.level.root=DEBUG
```

**application-prod.properties:**
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
logging.level.root=WARN
```

Utilisation:
```bash
# Dev
java -jar api.jar --spring.profiles.active=dev

# Prod
java -jar api.jar --spring.profiles.active=prod
```

---

## 📊 Monitoring Recommandé

### Health Check
```bash
curl https://api.cesizen.fr/actuator/health
```

Ajouter à pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Métriques Importantes

| Métrique | Seuil | Action |
|----------|-------|--------|
| CPU | > 80% | Scale up |
| RAM | > 85% | Scale up |
| DB Connections | > 80% pool | Scale up |
| Latency P95 | > 1s | Investigate |
| Error Rate | > 1% | Alert |
| Disk Usage | > 90% | Cleanup |

---

## 🔄 Processus de Déploiement

```
1. Développement Terminé
   ↓
2. Tests Unitaires & Intégration (CI/CD)
   ↓
3. Build Docker
   ↓
4. Push Registry
   ↓
5. Deploy Staging
   ↓
6. Tests Smoke (API disponible)
   ↓
7. Tests de Performance
   ↓
8. Approval Prod
   ↓
9. Blue-Green Deployment Prod
   ↓
10. Smoke Tests Prod
    ↓
11. Monitor 24h
    ↓
12. Cleanup ancienne version
```

---

## 🆘 Rollback Plan

```bash
# Si problème en production
docker pull registry.example.com/cesizen-api:previous
docker stack update cesizen --file docker-compose-prod.yml

# Ou Kubernetes:
kubectl rollout undo deployment/cesizen-api
```

---

## 📞 Escalade de Incident

1. **Symptôme**: API lente/down
2. **Check 1**: Health `/actuator/health`
3. **Check 2**: Logs serveur (`docker logs api`)
4. **Check 3**: DB connexion (MySQL running?)
5. **Check 4**: CPU/RAM/Disk (instance saturée?)
6. **Action**: Scale up instance ou rollback dernière version

---

## 🔗 Voir Aussi

- **[Configuration](./02-setup.md)** - Configuration locale
- **[Sécurité](./03-security.md)** - JWT et RBAC
- **[Gestion des Erreurs](./13-error-handling.md)** - Codes HTTP

---

**Dernière mise à jour**: 2026-04-21

---

## 📚 Ressources Supplémentaires

- [Spring Boot Production-Ready Features](https://spring.io/blog/2020/02/10/spring-boot-actuator-and-kubernetes-liveness-and-readiness-probes)
- [12 Factor App](https://12factor.net/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Security](https://kubernetes.io/docs/concepts/security/)

