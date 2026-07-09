# Plan de sécurisation — CESIZen

> Bloc de compétences 3 — Sécuriser les applications
> API Spring Boot · Front Next.js · Mobile Flutter · Version 1.0 — Juillet 2026

---

## 1. Objet et périmètre

Ce document décrit les mesures de sécurité mises en œuvre sur l'ensemble des composants de la plateforme CESIZen, ainsi que les risques résiduels identifiés. Il couvre les **trois applications** :

- **API REST** — Spring Boot 4 / Java 21, cœur métier, authentification, persistance MySQL ;
- **Front web** — Next.js 16 déployé sur Vercel ;
- **Application mobile** — Flutter (Android / iOS), consommatrice de l'API.

La démarche suit le référentiel **OWASP Top 10 (2021)** et applique le principe de **sécurité par conception** : blocage par défaut, ouverture explicite du strict nécessaire.

---

## 2. Couverture OWASP Top 10 (2021)

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

## 3. Mesures détaillées par application

### 3.1 API — Spring Boot

- **Authentification & autorisation.** Stateless JWT : login via `POST /api/auth/login`, token signé HMAC-SHA256 validé à chaque requête par un filtre Spring Security. Rôles portés par le token, contrôlés via `@PreAuthorize`.
- **Rate limiting (bucket4j).** Filtre `OncePerRequestFilter` par IP, réponse HTTP 429 :

  | Endpoint | Limite | Protection |
  |---|---|---|
  | `POST /api/auth/login` | 5 / minute | Brute-force |
  | `POST /api/auth/forgot-password` | 3 / 10 minutes | Énumération de comptes, spam d'e-mails |

- **En-têtes HTTP.** Posés à deux niveaux (Spring Security + nginx) : `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy`, `Content-Security-Policy`, `Permissions-Policy`, `Strict-Transport-Security` (prod).
- **Moindre privilège BDD.** En prod, `cesizen_app` est privé des droits DDL via un script d'init ; seuls SELECT/INSERT/UPDATE/DELETE subsistent. Une injection SQL ne pourrait pas altérer le schéma.
- **Secrets.** Le secret JWT a été retiré du code (il figurait en défaut commité) et régénéré. Il ne vit désormais que dans les variables d'environnement et les secrets GitHub. Gitleaks bloque toute réintroduction.
- **CVE corrigées.** jackson-databind (CVE-2026-54512 / 54513) via BOM 2.21.4 / 3.1.4 ; Tomcat forcé en 11.0.22 ; Swagger désactivé en prod.

### 3.2 Front — Next.js

- **En-têtes de sécurité.** Configurés dans `next.config.ts` : six en-têtes, dont une CSP construite dynamiquement à partir des origines autorisées (API et Supabase uniquement). HSTS émis uniquement en production.
- **CSP — compromis assumé.** La politique autorise `'unsafe-inline'` sur script/style (le App Router de Next injecte des scripts/styles inline). La solution propre (nonce par requête) est identifiée (issue #9) mais hors périmètre immédiat.
- **CVE corrigée.** Next.js 16.2.2 portait des CVE HIGH (contournement de middleware), détectées par le scan CI. Corrigées par montée en 16.2.10. Reste une CVE moderate (postcss du toolchain Next), sous le seuil bloquant, tracée (issue #13).

### 3.3 Mobile — Flutter

- **Analyse & scan.** La CI exécute `flutter analyze` (bloquant) et `flutter test` ; le workflow sécurité scanne `pubspec.lock` (CVE pub.dev), secrets et misconfigs via Trivy, plus Gitleaks.
- **Correction.** Un anti-pattern `use_build_context_synchronously` (BuildContext après un `await`) corrigé par une garde `context.mounted`.
- **Point de vigilance.** Le fichier de démonstration contient un mot de passe en clair (`Cesizen123!`). Le dépôt étant public, s'assurer qu'aucun compte réel de production ne l'utilise.

---

## 4. Sécurité de la chaîne de livraison (CI/CD)

| Outil | Rôle | Portée |
|---|---|---|
| Trivy | CVE dépendances, secrets, misconfigs, image Docker | 3 apps |
| Gitleaks | Détection de secrets dans l'historique git | 3 apps |
| OWASP Dependency Check | Scan approfondi base NVD (nocturne) | API |
| Dependabot | Mises à jour préventives (Maven, npm, pub, actions) | 3 apps |
| SonarCloud | Analyse statique (SAST), couverture | API |

La branche `main` de chaque dépôt est protégée par un **ruleset** : pull request obligatoire et checks de sécurité verts exigés avant tout merge.

---

## 5. Risques résiduels

| Risque | Gravité | Action recommandée |
|---|---|---|
| Rate limiting en mémoire | Faible | Migrer vers bucket4j-redis si HA |
| CSP avec `'unsafe-inline'` (front) | Moyen | Passer aux nonces (issue #9) |
| CVE moderate postcss (toolchain Next) | Faible | Surveiller les releases Next.js (issue #13) |
| Mot de passe de démo public (mobile) | Faible | Vérifier qu'aucun compte prod ne l'utilise |
| Secret JWT dans l'historique git | Faible | Rotation effectuée (ancien secret inopérant) |
| Upload sans validation MIME réelle | Moyen | Vérifier les magic bytes côté serveur |

---

## 6. Conclusion

La plateforme applique une défense en profondeur cohérente sur ses trois composants : authentification robuste, limitation de débit, en-têtes de sécurité, moindre privilège, et une chaîne de livraison qui détecte automatiquement CVE et secrets. Les risques résiduels sont identifiés, hiérarchisés et tracés en issues GitHub.
