# Plan de sécurisation — CESIZen

> Bloc de compétences 3 — Sécuriser les applications
> API Spring Boot · Front Next.js · Mobile Flutter · Version 1.1 — Juillet 2026

---

## 1. Objet et périmètre

Ce document décrit les mesures de sécurité mises en œuvre sur l'ensemble des composants de la plateforme CESIZen, ainsi que les risques résiduels identifiés. Il couvre les **trois applications** :

- **API REST** — Spring Boot 4 / Java 21, cœur métier, authentification, persistance MySQL ;
- **Front web** — Next.js 16 déployé sur Vercel ;
- **Application mobile** — Flutter (Android / iOS), consommatrice de l'API.

La démarche suit le référentiel **OWASP Top 10 (2021)** et applique le principe de **sécurité par conception** : blocage par défaut, ouverture explicite du strict nécessaire.

---

## 2. Architecture et flux de données (DFD)

```
┌──────────────┐        ┌──────────────┐
│  Front Web   │        │   Mobile     │
│  Next.js     │        │   Flutter    │
│  (Vercel)    │        │  (APK/IPA)   │
└──────┬───────┘        └──────┬───────┘
       │  HTTPS                │  HTTPS
       │  JWT Bearer           │  JWT Bearer
       └───────────┬───────────┘
                   ▼
         ┌───────────────────┐
         │   Nginx (:80/443) │  ← seul point d'entrée public
         │   reverse proxy   │     TLS, en-têtes de sécurité
         └─────────┬─────────┘
                   │ réseau Docker interne (cesizen-net)
                   ▼
         ┌───────────────────┐
         │  API Spring Boot  │  ← :8080, non exposé
         │  JWT · RBAC       │     rate limiting, validation
         └─────────┬─────────┘
                   │ JDBC (réseau interne)
                   ▼
         ┌───────────────────┐        ┌──────────────┐
         │   MySQL (:3306)   │        │   Supabase   │
         │   non exposé      │        │   (médias)   │
         └───────────────────┘        └──────────────┘
                                          ▲
                                          │ HTTPS (front uniquement)
                                          └── lecture des médias
```

**Frontières de confiance.** Tout ce qui vient du client (web, mobile) est non fiable : validation systématique côté API. Nginx est la seule surface exposée à Internet ; l'API et MySQL ne sont joignables que depuis le réseau Docker interne.

---

## 3. Couverture OWASP Top 10 (2021)

| # | Catégorie | Mesures mises en place |
|---|-----------|------------------------|
| A01 | Broken Access Control | JWT obligatoire hors routes publiques ; `@EnableMethodSecurity` + `@PreAuthorize` par rôle (ROLE_ADMIN / ROLE_USER) ; comptes désactivables |
| A02 | Cryptographic Failures | Mots de passe BCrypt (force 10) ; JWT signé HMAC-SHA256, secret en variable d'environnement ; HTTPS + HSTS en production |
| A03 | Injection | Spring Data JPA (requêtes paramétrées) ; validation Jakarta `@Valid` sur tous les DTO |
| A04 | Insecure Design | Moindre privilège (user MySQL dédié) ; API stateless ; réponse neutre sur mot de passe oublié (anti-énumération) |
| A05 | Security Misconfiguration | CSRF désactivé (REST stateless) ; Swagger désactivé en prod ; en-têtes HTTP de sécurité |
| A06 | Vulnerable Components | Dependabot (3 écosystèmes) ; Trivy à chaque CI ; CVE Jackson, Tomcat et Next.js corrigées |
| A07 | Auth Failures | Rate limiting bucket4j (5/min login, 3/10 min mot de passe oublié) ; tokens de reset à usage unique ; logs de connexion |
| A08 | Software/Data Integrity | CI avec Gitleaks + Trivy ; images Docker sur GHCR ; Dependabot sur les actions GitHub |
| A09 | Logging & Monitoring | `log_connexion` (login succès/échec, IP) ; `log_activite` (audit AOP) ; Prometheus + Grafana + Loki |
| A10 | SSRF | Aucun endpoint n'effectue de requête vers une URL fournie par l'utilisateur ; SMTP statique |

---

## 4. Modélisation des menaces

Analyse des menaces principales, cotées par probabilité et impact, avec la mesure d'atténuation en place.

| Menace | Surface exposée | Probabilité | Impact | Mitigation en place |
|---|---|---|---|---|
| Brute-force sur le login | `POST /api/auth/login` | Moyenne | Élevé | Rate limiting 5/min/IP → HTTP 429 ; journalisation des échecs (`log_connexion`) |
| Énumération de comptes | `POST /api/auth/forgot-password` | Moyenne | Moyen | Réponse HTTP 200 systématique, quel que soit l'existence de l'e-mail ; rate limiting 3/10 min |
| Vol de token JWT | En-tête `Authorization` | Faible | Élevé | HTTPS obligatoire + HSTS ; expiration du token ; rotation possible du secret (invalide tous les tokens) |
| Injection SQL | Tous les endpoints | Faible | Critique | JPA / requêtes paramétrées ; user BDD sans droits DDL (une injection ne peut pas `DROP TABLE`) |
| IDOR (accès aux données d'autrui) | `/api/user/{id}`, `/api/exercer` | Moyenne | Élevé | `@PreAuthorize` ; vérification de propriété ; `DELETE /api/user/me` pour l'auto-suppression |
| XSS stocké via contenu d'article | Front web | Faible | Moyen | React échappe par défaut ; CSP restreignant les origines de scripts |
| Clickjacking | Front web | Faible | Faible | `X-Frame-Options: DENY` + `frame-ancestors 'none'` |
| Fuite de secret dans le dépôt | Historique git | Moyenne | Critique | Gitleaks à chaque push + hook pre-commit ; secrets uniquement en variables d'environnement |
| Dépendance vulnérable (CVE) | Toutes les apps | Élevée | Variable | Trivy bloquant (CRITICAL/HIGH) ; Dependabot hebdomadaire ; OWASP Dependency Check nocturne |
| Exposition d'un service d'admin | phpMyAdmin, Grafana | Moyenne | Élevé | Non déployés en production ; réservés au réseau local de développement |

---

## 5. Surface d'exposition — gestion des ports

Principe : **n'exposer que l'indispensable**. Un seul port public en production.

| Service | Port | Exposé en dev | Exposé en prod | Accessible par |
|---|---|---|---|---|
| Nginx | 80 / 443 | 8080 → 80 | ✅ 80, 443 | Public — **seul point d'entrée** |
| API Spring Boot | 8080 | ❌ interne | ❌ interne | Nginx uniquement |
| MySQL | 3306 | ✅ (dev only) | ❌ `ports: !reset []` | API uniquement |
| phpMyAdmin | 8082 | ✅ (dev only) | ❌ absent | Admin local |
| Mailhog (SMTP de test) | 1025 / 8025 | ✅ (dev only) | ❌ absent | Développement |
| Prometheus | 9090 | ✅ (stack séparée) | ❌ interne | Grafana |
| Grafana | 3001 | ✅ (stack séparée) | ⚠️ à protéger | Admin local |
| Loki | 3100 | ✅ (stack séparée) | ❌ interne | Promtail / Grafana |

En production, `docker-compose.prod.yml` supprime explicitement les ports de MySQL (`ports: !reset []`) et n'inclut ni phpMyAdmin ni Mailhog. Seul Nginx écoute sur l'extérieur.

---

## 6. Droits sur les services et les conteneurs

- **Conteneur non-root.** Le `Dockerfile` de l'API crée un utilisateur système dédié (`addgroup -S cesizen && adduser -S -G cesizen cesizen`) et bascule dessus (`USER cesizen`) avant le démarrage. Une évasion applicative ne donne pas les droits root dans le conteneur.
- **Réseau Docker nommé.** Tous les services partagent un bridge dédié `cesizen-net`. MySQL et l'API ne sont pas publiés sur l'hôte : seule la résolution DNS interne (`db`, `api`) permet de les joindre.
- **Utilisateur MySQL à droits minimaux.** En production, `cesizen_app` est privé des droits DDL (`REVOKE CREATE, ALTER, DROP, INDEX, REFERENCES`) via un script d'init monté dans `/docker-entrypoint-initdb.d/`. Ne subsistent que `SELECT`, `INSERT`, `UPDATE`, `DELETE`. Le compte `root` n'est utilisé que par phpMyAdmin, en développement.
  *Limite connue : le script ne s'exécute qu'au premier démarrage, sur un volume vide. Sur une base déjà initialisée, la révocation doit être appliquée à la main.*
- **Fichiers uploadés.** Taille limitée à 10 Mo (`spring.servlet.multipart.max-file-size`). Le type MIME réel n'est pas vérifié — identifié comme risque résiduel.

---

## 7. Analyse statique (SAST) — SonarCloud

Le dépôt `api_zen` est lié à **SonarCloud**. L'analyse s'exécute dans le pipeline d'intégration continue, après la compilation et les tests :

```
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    -Dsonar.projectKey=TxMy-Elu_api_zen \
    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

Le rapport de couverture JaCoCo est transmis à Sonar, qui contrôle bugs, vulnérabilités, *code smells* et duplication. Le step est gardé par `if: env.SONAR_TOKEN != ''` : en l'absence de jeton (par exemple sur une pull request Dependabot), il est sauté sans faire échouer la CI.

Les jetons `SONAR_TOKEN`, `SONAR_ORGANIZATION` et `SONAR_PROJECT_KEY` sont stockés dans les secrets GitHub Actions, jamais dans le code.

> *À joindre au dossier : capture d'écran du tableau de bord SonarCloud (quality gate, couverture).*

---

## 8. Mesures détaillées par application

### 8.1 API — Spring Boot

- **Authentification & autorisation.** Stateless JWT : login via `POST /api/auth/login`, token signé HMAC-SHA256 validé à chaque requête par un filtre Spring Security. Rôles portés par le token, contrôlés via `@PreAuthorize`.
- **Rate limiting (bucket4j).** Filtre `OncePerRequestFilter` par IP, réponse HTTP 429 :

  | Endpoint | Limite | Protection |
  |---|---|---|
  | `POST /api/auth/login` | 5 / minute | Brute-force |
  | `POST /api/auth/forgot-password` | 3 / 10 minutes | Énumération de comptes, spam d'e-mails |

- **En-têtes HTTP.** Posés à deux niveaux (Spring Security + nginx) : `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy`, `Content-Security-Policy`, `Permissions-Policy`, `Strict-Transport-Security` (prod).
- **Secrets.** Le secret JWT a été retiré du code (il figurait en défaut commité) et régénéré. Il ne vit désormais que dans les variables d'environnement et les secrets GitHub. Gitleaks bloque toute réintroduction.
- **CVE corrigées.** jackson-databind (CVE-2026-54512 / 54513) via BOM 2.21.4 / 3.1.4 ; Tomcat forcé en 11.0.22 ; Swagger désactivé en prod.

### 8.2 Front — Next.js

- **En-têtes de sécurité.** Configurés dans `next.config.ts` : six en-têtes, dont une CSP construite dynamiquement à partir des origines autorisées (API et Supabase uniquement). HSTS émis uniquement en production.
- **CSP — compromis assumé.** La politique autorise `'unsafe-inline'` sur script/style (le App Router de Next injecte des scripts/styles inline). La solution propre — un nonce par requête généré via un middleware — constitue une évolution recommandée.
- **CVE corrigée.** Next.js 16.2.2 portait des CVE HIGH (contournement de middleware), détectées par le scan CI. Corrigées par montée en 16.2.10. Reste une CVE moderate (postcss du toolchain Next), sous le seuil bloquant, sous surveillance.

### 8.3 Mobile — Flutter

- **Analyse & scan.** La CI exécute `flutter analyze` (bloquant) et `flutter test` ; le workflow sécurité scanne `pubspec.lock` (CVE pub.dev), secrets et misconfigs via Trivy, plus Gitleaks.
- **Correction.** Un anti-pattern `use_build_context_synchronously` (BuildContext après un `await`) corrigé par une garde `context.mounted`.
- **Point de vigilance.** Le fichier de démonstration contient un mot de passe en clair (`Cesizen123!`). Le dépôt étant public, s'assurer qu'aucun compte réel de production ne l'utilise.

---

## 9. Sécurité de la chaîne de livraison (CI/CD)

| Outil | Rôle | Portée |
|---|---|---|
| Trivy | CVE dépendances, secrets, misconfigs, image Docker | 3 apps |
| Gitleaks | Détection de secrets dans l'historique git | 3 apps |
| OWASP Dependency Check | Scan approfondi base NVD (nocturne) | API |
| Dependabot | Mises à jour préventives (Maven, npm, pub, actions) | 3 apps |
| SonarCloud | Analyse statique (SAST), couverture | API |

La branche `main` de chaque dépôt est protégée par un **ruleset** : pull request obligatoire et checks de sécurité verts exigés avant tout merge.

**Traitement d'une alerte Dependabot.** Revue de la PR → vérification de la CI verte → merge. Une montée majeure cassante est fermée et neutralisée par une règle `ignore` si elle récidive. Les artefacts d'un même BOM sont groupés pour éviter les bumps partiels.

---

## 10. Conformité RGPD

### 10.1 Données à caractère personnel collectées

| Donnée | Finalité | Durée de conservation | Sensible |
|---|---|---|---|
| Nom, prénom | Identification de l'utilisateur | Durée de vie du compte | Non |
| Adresse e-mail | Authentification, réinitialisation de mot de passe | Durée de vie du compte | Non |
| Mot de passe (haché BCrypt) | Sécurité de l'accès | Durée de vie du compte | Oui |
| Date de création du compte | Traçabilité | Durée de vie du compte | Non |
| Adresse IP, horodatage, e-mail tenté (`log_connexion`) | Sécurité, détection de brute-force | 90 jours (recommandé) | Non |
| Historique d'activité (`log_activite`) | Audit des mutations | 90 jours (recommandé) | Non |
| Historique d'exercices de respiration | Suivi du bien-être personnel | Durée de vie du compte | **Oui — santé** |

L'historique d'exercices relève des **données de santé** au sens du RGPD : il nécessite un consentement explicite et une protection renforcée.

### 10.2 Droits des personnes — état d'implémentation

| Droit | Endpoint / mécanisme | État |
|---|---|---|
| Droit d'accès | `GET /api/user/{id}` — consultation du profil | ✅ Implémenté |
| Droit de rectification | `PUT /api/user/{id}` | ✅ Implémenté |
| Droit à l'effacement | `DELETE /api/user/me` (auto-suppression) ; `DELETE /api/user/{id}` (anonymisation par un administrateur) | ✅ Implémenté |
| Droit à la portabilité | Export structuré des données (JSON/CSV) | ❌ **Non implémenté** |
| Droit d'opposition / limitation | Désactivation du compte (`active = false`) | ⚠️ Partiel |

### 10.3 Écarts identifiés

- **Consentement explicite.** Aucun champ de consentement n'est stocké dans le modèle de données de l'API. Le recueil, s'il existe, se fait côté interface sans trace persistée. Or les données de santé exigent un consentement explicite et **démontrable**. → *Action requise : ajouter un champ de consentement horodaté à l'inscription.*
- **Durée de conservation.** Aucune purge automatique des journaux n'est en place. Les durées indiquées ci-dessus sont des recommandations, pas des mécanismes actifs. → *Action requise : tâche planifiée de purge à 90 jours.*
- **Portabilité.** Aucun export structuré n'est proposé.

### 10.4 Violation de données

En cas de violation de données à caractère personnel, notification à la **CNIL sous 72 heures** et information des personnes concernées si le risque est élevé (voir la procédure de réponse à incident, §11).

---

## 11. Gestion de crise — réponse à incident

1. **Détection** — alerte Dependabot, échec de scan de sécurité, journaux anormaux (`log_connexion` / `log_activite`), ou signalement utilisateur via une issue GitHub.
2. **Isolation** — couper le service concerné si nécessaire ; en cas de compromission de token, rotation immédiate du secret JWT (invalide l'ensemble des tokens émis).
3. **Analyse** — examiner les journaux d'activité et de connexion pour délimiter le périmètre et l'impact.
4. **Correction** — patch → pull request → CI verte → redéploiement.
5. **Communication** — informer les utilisateurs si des données personnelles sont concernées.
6. **Obligation RGPD** — notification à la CNIL sous 72 h en cas de violation de données personnelles.

**Responsabilité des journaux.** L'administrateur de la plateforme est responsable de la conservation et de la purge des journaux. Durée de rétention recommandée : 90 jours.

---

## 12. Risques résiduels

| Risque | Gravité | Action recommandée |
|---|---|---|
| Consentement RGPD non tracé (données de santé) | **Élevée** | Ajouter un champ de consentement horodaté à l'inscription |
| Aucune purge automatique des journaux | Moyenne | Tâche planifiée de suppression à 90 jours |
| Droit à la portabilité non implémenté | Moyenne | Endpoint d'export JSON des données utilisateur |
| CSP avec `'unsafe-inline'` (front) | Moyenne | Passer aux nonces |
| Upload sans validation MIME réelle | Moyenne | Vérifier les magic bytes côté serveur |
| Rate limiting en mémoire | Faible | Migrer vers bucket4j-redis si haute disponibilité |
| CVE moderate postcss (toolchain Next) | Faible | Surveiller les releases Next.js |
| Mot de passe de démo public (mobile) | Faible | Vérifier qu'aucun compte prod ne l'utilise |
| Secret JWT dans l'historique git | Faible | Rotation effectuée (ancien secret inopérant) |
| Grafana en prod : mot de passe par défaut | Moyenne | Changer `GRAFANA_ADMIN_PASSWORD` ; exposer via tunnel SSH uniquement |

---

## 13. Conclusion

La plateforme applique une défense en profondeur cohérente sur ses trois composants : authentification robuste, limitation de débit, en-têtes de sécurité, moindre privilège (conteneur non-root, utilisateur MySQL sans droits DDL, un seul port public), et une chaîne de livraison qui détecte automatiquement CVE et secrets.

Les écarts les plus significatifs concernent la **conformité RGPD** — traçabilité du consentement pour des données de santé, purge des journaux, portabilité — et sont hiérarchisés en tête des risques résiduels. L'ensemble des points ouverts est recensé dans le tableau des risques résiduels ci-dessus, garantissant leur suivi dans la durée.
