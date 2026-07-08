# OWASP Top 10 — Analyse de risque CESIZen

> Référentiel : OWASP Top 10 2021  
> Application : API Spring Boot + Frontend Next.js  
> Date : 2026-07

---

## Tableau de risques

| # | Catégorie | Risque | Mesures mises en place | Statut |
|---|-----------|--------|------------------------|--------|
| A01 | **Broken Access Control** | Accès à des ressources ou actions sans autorisation | JWT obligatoire sur toutes les routes sauf publiques ; `@EnableMethodSecurity` + contrôles par rôle (`ROLE_ADMIN` / `ROLE_USER`) ; utilisateur désactivable (`active=false`) | ✅ Couvert |
| A02 | **Cryptographic Failures** | Exposition de données sensibles (mots de passe, tokens) | Mots de passe hashés BCrypt (force 10) ; JWT signé HMAC-SHA256 ; secret injecté via variable d'environnement ; HTTPS en prod (TLS 1.2/1.3) ; HSTS `max-age=31536000` | ✅ Couvert |
| A03 | **Injection** | SQL injection, injection de commandes | Spring Data JPA avec requêtes paramétrées (pas de SQL dynamique) ; validation Jakarta `@Valid` sur tous les DTO ; `@Column(unique)` empêche les doublons | ✅ Couvert |
| A04 | **Insecure Design** | Architecture sans sécurité intégrée | Principe du moindre privilège (user MySQL dédié `cesizen_app`) ; API stateless (pas de session serveur) ; endpoint mot de passe oublié avec réponse neutre (anti-énumération email) | ✅ Couvert |
| A05 | **Security Misconfiguration** | Config par défaut non sécurisée | CSRF désactivé (API REST stateless) ; Swagger désactivé en prod ; Actuator restreint aux endpoints nécessaires ; headers HTTP de sécurité (CSP, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, Permissions-Policy) | ✅ Couvert |
| A06 | **Vulnerable and Outdated Components** | Dépendances avec CVE connues | Dependabot (mises à jour auto hebdomadaires) ; scan Trivy image Docker à chaque CI ; Spring Boot 4.0.6 + Tomcat 11.0.22 + Jackson 2.21.4 / 3.1.4 (CVE-2026-54512, CVE-2026-54513 corrigés) | ✅ Couvert |
| A07 | **Identification and Authentication Failures** | Brute-force, credentials faibles, tokens mal gérés | Rate limiting bucket4j : 5 req/min sur `/api/auth/login`, 3 req/10 min sur `/api/auth/forgot-password` ; tokens de réinitialisation à usage unique avec expiration ; logs de connexion (`log_connexion`) | ✅ Couvert |
| A08 | **Software and Data Integrity Failures** | Intégrité du code et des données | Pipeline CI avec Gitleaks (scan de secrets), Trivy (scan image) ; images Docker poussées sur GHCR privé ; Dependabot pour les actions GitHub | ✅ Couvert |
| A09 | **Security Logging and Monitoring Failures** | Absence de logs de sécurité | `LogConnexion` : enregistrement de chaque login (succès/échec, IP, timestamp) ; `LogActivite` : audit AOP de toutes les mutations (CREATE/UPDATE/DELETE) ; stack Prometheus + Grafana + Loki | ✅ Couvert |
| A10 | **Server-Side Request Forgery (SSRF)** | L'API effectue des requêtes vers des URLs fournies par l'utilisateur | Aucun endpoint ne prend d'URL en entrée pour effectuer une requête sortante ; les emails sont envoyés via JavaMailSender vers un serveur SMTP configuré statiquement | ✅ Couvert |

---

## Risques résiduels identifiés

| Risque | Description | Action recommandée |
|--------|-------------|--------------------|
| Rate limiting en mémoire | Les buckets bucket4j sont perdus au redémarrage du conteneur | Migrer vers bucket4j-redis si haute disponibilité requise |
| CORS permissif en dev | `allowedOriginPatterns` inclut tous les ports localhost | Restreindre aux origines exactes en prod via variable d'environnement |
| Swagger accessible hors prod | Swagger actif en dev/staging expose la surface d'attaque | S'assurer que `SPRING_PROFILES_ACTIVE=prod` est bien utilisé en production |
| Upload de fichiers sans validation de contenu | La taille est limitée à 10 Mo mais le type MIME n'est pas vérifié côté serveur | Ajouter une vérification du type MIME réel (magic bytes) |

---

## Niveaux de risque

| Niveau | Critère |
|--------|---------|
| 🔴 Critique | Exploitation triviale + impact majeur (ex. RCE, fuite totale de BDD) |
| 🟠 Élevé | Exploitation possible avec impact significatif |
| 🟡 Moyen | Exploitation difficile ou impact limité |
| 🟢 Faible | Risque accepté ou impact négligeable |
